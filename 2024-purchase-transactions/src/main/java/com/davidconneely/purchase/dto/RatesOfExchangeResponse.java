package com.davidconneely.purchase.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Useful subset of the API response.
 */
public record RatesOfExchangeResponse(
        @NotNull List<Datum> data,
        @NotNull Meta meta) {
    public record Datum(
            @JsonProperty("country_currency_desc")
            @NotBlank String countryCurrencyDesc,
            @JsonProperty("exchange_rate")
            @NotNull @PositiveOrZero BigDecimal exchangeRate,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            @JsonProperty("effective_date")
            @NotNull LocalDate effectiveDate) {
    }

    public record Meta(
            @NotNull @PositiveOrZero int count,
            @JsonProperty("total-count")
            @NotNull @PositiveOrZero int totalCount,
            @JsonProperty("total-pages")
            @NotNull @PositiveOrZero int totalPages) {
    }
}
