package com.davidconneely.purchase.client;

import static com.davidconneely.purchase.client.ClientUtils.formatLocalDate;

import com.davidconneely.purchase.config.ClientProperties;
import com.davidconneely.purchase.dto.RatesOfExchangeResponse;
import com.davidconneely.purchase.exception.RateNotAvailableException;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class LiveRatesOfExchangeClient implements RatesOfExchangeClient {
  private final ClientProperties properties;
  private final RestTemplate restTemplate;

  public LiveRatesOfExchangeClient(
      ClientProperties properties, RestTemplateBuilder restTemplateBuilder) {
    this.properties = properties;
    this.restTemplate = restTemplateBuilder.build();
  }

  @Override
  public BigDecimal getSingleRate(String countryCurrencyDesc, LocalDate transactionDate) {
    RatesOfExchangeResponse dto = fetchSingleRate(countryCurrencyDesc, transactionDate);
    return parseResponseToSingleRate(dto);
  }

  /**
   * Download 'Rates of Exchange' data from the Treasury.gov FiscalData API.
   *
   * @param countryCurrencyDesc the country_currency_desc to request.
   * @param transactionDate the date to search back from.
   * @return RatesOfExchangeResponse the API response of exchange rate nearest to the
   *     transactionDate.
   */
  private RatesOfExchangeResponse fetchSingleRate(
      String countryCurrencyDesc, LocalDate transactionDate) {
    String baseURL = properties.getBaseURL();
    String ratesOfExchangeEndpoint = properties.getRatesOfExchangeEndpoint();
    LocalDate sixMonthsEarlier = transactionDate.minusMonths(6);
    URI uri =
        UriComponentsBuilder.fromUriString(baseURL)
            .path(ratesOfExchangeEndpoint)
            .queryParam("fields", "record_date,country_currency_desc,exchange_rate,effective_date")
            .queryParam("format", "json")
            .queryParam(
                "filter",
                String.format(
                    "country_currency_desc:in:(%s),effective_date:lte:%s,effective_date:gte:%s",
                    countryCurrencyDesc,
                    formatLocalDate(transactionDate),
                    formatLocalDate(sixMonthsEarlier)))
            .queryParam("page[number]", "1")
            .queryParam("page[size]", "1")
            .queryParam("sort", "-effective_date")
            .build()
            .toUri();
    if (log.isDebugEnabled()) {
      log.debug(
          "#fetchSingleRate("
              + countryCurrencyDesc
              + ", "
              + transactionDate
              + "): request URI "
              + uri);
    }
    ResponseEntity<RatesOfExchangeResponse> response =
        restTemplate.getForEntity(uri, RatesOfExchangeResponse.class);
    if (!response.getStatusCode().is2xxSuccessful()) {
      log.info(
          "#fetchSingleRate("
              + countryCurrencyDesc
              + ", "
              + transactionDate
              + "): response error status "
              + response.getStatusCode());
      throw new RateNotAvailableException("error status response from Rates of Exchange API");
    }
    return response.getBody();
  }

  /**
   * Converts the API response (containing a single datum) into a single exchange rate.
   *
   * @param dto the API response entity.
   * @return BigDecimal the single exchange rate.
   */
  private static BigDecimal parseResponseToSingleRate(RatesOfExchangeResponse dto) {
    if (Objects.requireNonNull(dto).data().isEmpty()) {
      log.debug("#parseResponseToSingleRate: empty data");
      throw new RateNotAvailableException(
          "there was no available exchange rate within 6 months before the purchase transaction date");
    }
    return dto.data().get(0).exchangeRate();
  }
}
