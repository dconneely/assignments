package com.davidconneely.purchase.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * This is a placeholder, not currently used.
 */
@Data
public class RateOfExchangeResponse {
    @Data
    static class Record {
        @JsonProperty("country_currency_desc")
        private String countryCurrencyDesc;

        @JsonProperty("exchange_rate")
        private BigDecimal exchangeRate;

        @JsonProperty("effective_date")
        private LocalDate effectiveDate;
    }

    private Record[] data;
}
