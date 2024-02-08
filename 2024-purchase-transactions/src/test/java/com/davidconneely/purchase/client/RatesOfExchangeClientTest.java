package com.davidconneely.purchase.client;

import com.davidconneely.purchase.config.PurchaseProperties;
import com.davidconneely.purchase.exception.RateNotAvailableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.davidconneely.purchase.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest({RatesOfExchangeClient.class, PurchaseProperties.class})
public class RatesOfExchangeClientTest {
    @Autowired
    private RatesOfExchangeClient client;
    @Autowired
    private MockRestServiceServer server;

    @Test
    public void testFetchRateSuccess() {
        String json = utf8Resource("/RatesOfExchangeClient1.json");
        server.expect(method(HttpMethod.GET)).andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
        BigDecimal rate = client.fetchRate(COUNTRY_CURRENCY_DESC_UK2015, TRANSACTION_DATE_GOOD);
        assertEquals(EXCHANGE_RATE_GOOD, rate);
    }

    @Test
    public void testFetchRateFailure() {
        String json = utf8Resource("/RatesOfExchangeClient0.json");
        server.expect(method(HttpMethod.GET)).andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
        try {
            BigDecimal rate = client.fetchRate(COUNTRY_CURRENCY_DESC_UK2015, LocalDate.EPOCH);
            fail();
        } catch (RateNotAvailableException e) {
            assertTrue(e.getStatusCode().isError());
        }
    }
}
