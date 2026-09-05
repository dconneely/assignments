package com.davidconneely.purchase.client;

import static com.davidconneely.purchase.client.ClientUtils.formatLocalDate;

import com.davidconneely.purchase.config.ClientProperties;
import com.davidconneely.purchase.dto.RatesOfExchangeResponse;
import com.davidconneely.purchase.exception.RateNotAvailableException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
public class CachedRatesOfExchangeClient implements RatesOfExchangeClient {
  private final ClientProperties properties;
  private final RestTemplate restTemplate;
  private final Cache cache;
  private final ReadWriteLock lock;

  public CachedRatesOfExchangeClient(
      ClientProperties properties, JsonMapper jsonMapper, RestTemplateBuilder restTemplateBuilder) {
    this.properties = properties;
    this.restTemplate = restTemplateBuilder.build();
    this.cache = newPrepoulatedCache(jsonMapper);
    this.lock = new ReentrantReadWriteLock();
  }

  public BigDecimal getSingleRate(String countryCurrencyDesc, LocalDate transactionDate) {
    lock.readLock().lock();
    // do we need to update the cache? is it more than a day since the last update, and is the date
    // requested in the last quarter or later (which are still being updated)?
    LocalDate lastRecordDate = cache.getLastRecordDate();
    LocalDate lastUpdateDate = cache.getLastUpdateDate();
    if (transactionDate.isAfter(lastRecordDate) && LocalDate.now().isAfter(lastUpdateDate)) {
      lock.readLock().unlock(); // must release read lock before acquiring write lock
      lock.writeLock().lock();
      try {
        updateCache();
        lock.readLock().lock(); // downgrade by acquiring read lock before releasing write lock
      } finally {
        lock.writeLock().unlock();
      }
    }
    try {
      // find the series of exchange rates for the requested countryCurrencyDesc.
      Map<String, NavigableSet<Cache.Entry>> cacheData = cache.getData();
      NavigableSet<Cache.Entry> series = cacheData.get(countryCurrencyDesc);
      if (series != null) {
        // now find the nearest earlier exchange rate and check its age.
        Cache.Entry dummyRate = new Cache.Entry(transactionDate, BigDecimal.ZERO);
        Cache.Entry rate = series.floor(dummyRate);
        LocalDate sixMonthsEarlier = transactionDate.minusMonths(6);
        if (rate != null && !rate.date().isBefore(sixMonthsEarlier)) {
          return rate.exchangeRate();
        }
      }
    } finally {
      lock.readLock().unlock();
    }
    throw new RateNotAvailableException(
        "there was no available exchange rate within 6 months before the purchase transaction date");
  }

  /**
   * So we are not constantly hitting the API, we pre-populate the cache with data from a resource.
   */
  private static Cache newPrepoulatedCache(JsonMapper jsonMapper) {
    Cache cache = new Cache();
    cache.setData(new HashMap<>());
    cache.setLastRecordDate(LocalDate.EPOCH);
    cache.setLastUpdateDate(LocalDate.EPOCH);
    try (InputStream in =
        RatesOfExchangeClient.class.getResourceAsStream("/RprtRateXchg_20010331_20231231.json")) {
      RatesOfExchangeResponse dto = jsonMapper.readValue(in, RatesOfExchangeResponse.class);
      parseResponseIntoCache(dto, cache);
    } catch (IOException e) {
      log.info("#newPrepopulatedCache: Unexpected exception reading resource", e);
    }
    cache.setLastUpdateDate(cache.getLastRecordDate());
    return cache;
  }

  /** Merge new data from the API into the data already in the cache. */
  private void updateCache() {
    LocalDate lastRecordDate = cache.getLastRecordDate();
    RatesOfExchangeResponse dto = fetchAllRatesSince(lastRecordDate, 1);
    parseResponseIntoCache(dto, cache);
    int totalPages = dto.meta() != null ? dto.meta().totalPages() : 0;
    if (totalPages > 1) {
      log.debug("#updateCache: there is more than one (" + totalPages + ") page of responses");
      for (int pageNumber = 2; pageNumber <= totalPages; ++pageNumber) {
        dto = fetchAllRatesSince(lastRecordDate, pageNumber);
        parseResponseIntoCache(dto, cache);
      }
    }
    cache.setLastUpdateDate(LocalDate.now());
  }

  /**
   * Download 'Rates of Exchange' data from the Treasury.gov FiscalData API.
   *
   * @param since the date to load forward from.
   * @param pageNumber the offset in the data to start from (offset = (pageNumber-1)*pageSize).
   * @return RatesOfExchangeResponse the API response of exchange rates since the `since` date.
   */
  private RatesOfExchangeResponse fetchAllRatesSince(LocalDate since, int pageNumber) {
    String baseURL = properties.getBaseURL();
    String ratesOfExchangeEndpoint = properties.getRatesOfExchangeEndpoint();
    // Note just using String.format would not escape URL-unfriendly characters (e.g. '&' or '#') in
    // the parameters.
    URI uri =
        UriComponentsBuilder.fromUriString(baseURL)
            .path(ratesOfExchangeEndpoint)
            .queryParam("fields", "record_date,country_currency_desc,exchange_rate,effective_date")
            .queryParam("filter", String.format("effective_date:gte:%s", formatLocalDate(since)))
            .queryParam("format", "json")
            .queryParam("page[number]", Integer.toString(pageNumber))
            .queryParam("page[size]", "5000")
            .queryParam("sort", "country_currency_desc,effective_date")
            .build()
            .toUri();
    if (log.isDebugEnabled()) {
      log.debug("#fetchAllRatesSince(" + since + ", " + pageNumber + "): request URI " + uri);
    }
    ResponseEntity<RatesOfExchangeResponse> response =
        restTemplate.getForEntity(uri, RatesOfExchangeResponse.class);
    if (!response.getStatusCode().is2xxSuccessful()) {
      log.info(
          "#fetchAllRatesSince("
              + since
              + ", "
              + pageNumber
              + "): response error status "
              + response.getStatusCode());
      throw new RateNotAvailableException("error status response from Rates of Exchange API");
    }
    return response.getBody();
  }

  /**
   * Updates a cache of currency exchange rates over time from the API response.
   *
   * @param dto the API response entity.
   * @param cache the cache to add the data too.
   */
  private static void parseResponseIntoCache(RatesOfExchangeResponse dto, Cache cache) {
    Map<String, NavigableSet<Cache.Entry>> cacheData = cache.getData();
    LocalDate lastRecordDate = cache.getLastRecordDate();
    for (RatesOfExchangeResponse.Datum datum : Objects.requireNonNull(dto).data()) {
      String countryCurrencyDesc = datum.countryCurrencyDesc();
      Cache.Entry rate = new Cache.Entry(datum.effectiveDate(), datum.exchangeRate());
      NavigableSet<Cache.Entry> series =
          cacheData.computeIfAbsent(countryCurrencyDesc, k -> new TreeSet<>());
      series.add(rate);
      LocalDate recordDate = datum.recordDate();
      if (recordDate.isAfter(lastRecordDate)) {
        lastRecordDate = recordDate;
      }
    }
    if (lastRecordDate.isAfter(cache.getLastRecordDate())) {
      cache.setLastRecordDate(lastRecordDate);
    }
  }
}
