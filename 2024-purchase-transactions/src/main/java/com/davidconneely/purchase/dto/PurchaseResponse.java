package com.davidconneely.purchase.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PurchaseResponse(@NotNull UUID id,
                               @Size(max = 50) String description,
                               @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                               @NotNull LocalDate transactionDate,
                               @NotNull @Positive BigDecimal purchaseAmount,
                               @NotNull @PositiveOrZero BigDecimal exchangeRate,
                               @NotNull @PositiveOrZero BigDecimal convertedAmount) {
}
