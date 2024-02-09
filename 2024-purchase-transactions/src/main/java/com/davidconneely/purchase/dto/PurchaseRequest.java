package com.davidconneely.purchase.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PurchaseRequest(@Schema(description = "Description of the purchase (max 50 characters)", example = "Ring binders (for stationery cupboard)") @Size(max = 50) String description,
                              @Schema(description = "Purchase transaction date (in ISO yyyy-MM-dd format)", example = "2022-11-19") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") @NotNull LocalDate transactionDate,
                              @Schema(description = "Purchase amount in US dollars (positive, up to two decimal places)", example = "123.45") @NotNull @Positive BigDecimal purchaseAmount) {
}
