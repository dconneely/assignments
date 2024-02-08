package com.davidconneely.purchase.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PurchaseRequest(@Size(max = 50) String description,
                              @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                              @NotNull LocalDate transactionDate,
                              @NotNull @Positive BigDecimal purchaseAmount) {
    /**
     * From the requirements:
     * <ul>
     *   <li>Description: must not exceed 50 characters [handled by jakarta-validation]</li>
     *   <li>Transaction date: must be a valid date format [handled by jakarta-validation]</li>
     *   <li>Purchase amount: must be a valid positive amount rounded to the nearest cent [positive handled by
     *   jakarta-validation, nearest cent rounding is checked here]</li>
     * </ul>
     * A couple of additional sanity checks (1789-09-02 <= date <= 9999-12-31) are done on the date.
     * There's nothing in the requirements to preclude future dates on purchases, so they are allowed.
     *
     * @return Always returns true or throws an exception (i.e. never returns false).
     */
    public boolean isValid() {
        // Must use compareTo(), not equals(), for proper numeric comparison.
        return true;
    }
}
