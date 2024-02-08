package com.davidconneely.purchase.client;

import com.davidconneely.purchase.config.PurchaseProperties;
import com.davidconneely.purchase.dto.RatesOfExchangeResponse;
import com.davidconneely.purchase.exception.RateNotAvailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class RatesOfExchangeClient {
    private final PurchaseProperties properties;
    private final RestTemplate restTemplate;

    public RatesOfExchangeClient(PurchaseProperties properties, RestTemplateBuilder restTemplateBuilder) {
        this.properties = properties;
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * Download 'Rates of Exchange' data from the Treasury.gov FiscalData API.
     *
     * @param countryCurrencyDesc the country_currency_desc to request.
     * @param transactionDate     the date to search back from.
     * @return BigDecimal the exchange rate nearest to the transactionDate.
     * @throws RateNotAvailableException if there is no exchange rate within 6 months before the transactionDate.
     */
    public BigDecimal fetchRate(String countryCurrencyDesc, LocalDate transactionDate) {
        String baseURL = properties.getTreasuryFiscalData().getBaseURL();
        String ratesOfExchangeEndpoint = properties.getTreasuryFiscalData().getRatesOfExchangeEndpoint();
        LocalDate sixMonthsEarlier = transactionDate.minusMonths(6);

        URI uri = UriComponentsBuilder.fromUriString(baseURL)
                .path(ratesOfExchangeEndpoint)
                .queryParam("fields", "country_currency_desc,exchange_rate,effective_date")
                .queryParam("filter", String.format("country_currency_desc:in:(%s),effective_date:lte:%s,effective_date:gte:%s",
                        countryCurrencyDesc, formatLocalDate(transactionDate), formatLocalDate(sixMonthsEarlier)))
                .queryParam("format", "json")
                .queryParam("page[number]", "1")
                .queryParam("page[size]", "1")
                .queryParam("sort", "-effective_date")
                .build().toUri();

        log.info("#fetchRate(" + countryCurrencyDesc + ", " + transactionDate + "): uri is " + uri);
        ResponseEntity<RatesOfExchangeResponse> response = restTemplate.getForEntity(uri, RatesOfExchangeResponse.class);
        RatesOfExchangeResponse.Datum datum = getDatum(response);
        log.info("#fetchRate(" + countryCurrencyDesc + ", " + transactionDate + "): datum is " + datum);
        return datum.exchangeRate();
    }

    private static RatesOfExchangeResponse.Datum getDatum(ResponseEntity<RatesOfExchangeResponse> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.debug("#getDatum: status code is " + response.getStatusCode());
            throw new RateNotAvailableException("error status response from Rates of Exchange API");
        }
        RatesOfExchangeResponse body = response.getBody();
        if (body == null || body.data() == null || body.meta() == null) {
            log.debug("#getDatum: body is " + body);
            throw new RateNotAvailableException("could not parse response body from Rates of Exchange API");
        }
        if (body.data().isEmpty() || body.meta().count() == 0) {
            log.debug("#getDatum: empty data, meta is " + body.meta());
            throw new RateNotAvailableException("there was no available exchange rate within 6 months before the purchase transaction date");
        }
        return body.data().get(0);
    }

    private static String formatLocalDate(LocalDate date) {
        return DateTimeFormatter.ISO_LOCAL_DATE.format(date);
    }
}
