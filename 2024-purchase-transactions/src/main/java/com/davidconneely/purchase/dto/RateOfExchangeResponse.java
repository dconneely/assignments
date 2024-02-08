package com.davidconneely.purchase.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
        @NotBlank
        private String countryCurrencyDesc;

        @JsonProperty("exchange_rate")
        @NotNull @PositiveOrZero
        private BigDecimal exchangeRate;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @JsonProperty("effective_date")
        @NotNull
        private LocalDate effectiveDate;
    }

    private Record[] data;
}
