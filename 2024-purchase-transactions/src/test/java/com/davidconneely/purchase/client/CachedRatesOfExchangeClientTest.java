package com.davidconneely.purchase.client;

import com.davidconneely.purchase.config.ApplicationConfig;
import com.davidconneely.purchase.config.ClientProperties;
import com.davidconneely.purchase.exception.RateNotAvailableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.davidconneely.purchase.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;

@RestClientTest(components = {ApplicationConfig.class, ClientProperties.class},
        properties = {"app.purchase.treasury-fiscalData.cache-enabled=true"})
public class CachedRatesOfExchangeClientTest {
    @Autowired
    private RatesOfExchangeClient client;
    @Autowired
    private MockRestServiceServer server;

    @Test
    public void testGetSingleRateSuccess() {
        server.expect(method(HttpMethod.GET)).andRespond(withServerError()); // shouldn't hit the server
        assertInstanceOf(CachedRatesOfExchangeClient.class, client);
        BigDecimal exchangeRate = client.getSingleRate(COUNTRY_CURRENCY_DESC_UK2015, TRANSACTION_DATE_GOOD);
        assertEquals(EXCHANGE_RATE_GOOD, exchangeRate);
    }

    @Test
    public void testGetSingleRateFailure() {
        server.expect(method(HttpMethod.GET)).andRespond(withServerError()); // shouldn't hit the server
        assertInstanceOf(CachedRatesOfExchangeClient.class, client);
        try {
            BigDecimal exchangeRate = client.getSingleRate(COUNTRY_CURRENCY_DESC_UK2015, LocalDate.EPOCH);
            fail();
        } catch (RateNotAvailableException e) {
            assertTrue(e.getStatusCode().isError());
        }
    }
}
