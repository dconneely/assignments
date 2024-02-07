package com.davidconneely.purchase.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PurchaseResponse(UUID id,
                               String description,
                               @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                               LocalDate transactionDate,
                               BigDecimal purchaseAmount,
                               BigDecimal exchangeRate,
                               BigDecimal convertedAmount) {
}
