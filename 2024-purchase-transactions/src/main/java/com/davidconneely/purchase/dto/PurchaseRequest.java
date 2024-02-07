package com.davidconneely.purchase.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public record PurchaseRequest(String description,
                              @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                              LocalDate transactionDate,
                              BigDecimal purchaseAmount) {
    /**
     * From the requirements:
     * <ul>
     *   <li>Description: must not exceed 50 characters [so can be null]</li>
     *   <li>Transaction date: must be a valid date format [so cannot be null]</li>
     *   <li>Purchase amount: must be a valid positive amount rounded to the nearest cent [so cannot be null]</li>
     * </ul>
     * A couple of additional sanity checks (1789-09-02 <= date <= 9999-12-31) are done on the date.
     * Originally this code disallowed future dates, but there's nothing in the requirements to preclude this,
     * so they have been allowed again.
     *
     * @return Always returns true or throws an exception (i.e. never returns false).
     */
    public boolean isValid() {
        if (description != null && description.length() > 50) {
            // This doesn't take account of surrogate pairs, only BMP characters.
            throw new IllegalArgumentException("description has more than 50 characters");
        }
        if (transactionDate == null) {
            throw new IllegalArgumentException("missing transaction date");
        }
        if (transactionDate.isAfter(LocalDate.of(9999, 12, 31))) {
            // If this application is still in use in the year 10,000 AD, please fix this!
            // (Actually will also need to fix Jackson's JSON deserialization too).
            throw new IllegalArgumentException("transaction date is too far in the future");
        }
        if (transactionDate.isBefore(LocalDate.of(1789, 9, 2))) {
            // Before the founding date of the US Treasury!
            // (Actually there is no data before 2001-03-31 in the dataset).
            throw new IllegalArgumentException("transaction date is too far in the past");
        }
        if (purchaseAmount == null) {
            throw new IllegalArgumentException("missing purchase amount");
        }
        if (purchaseAmount.signum() != 1) {
            throw new IllegalArgumentException("purchase amount is not a positive number");
        }
        if (!purchaseAmount.equals(purchaseAmount.setScale(2, RoundingMode.HALF_UP))) {
            throw new IllegalArgumentException("purchase amount is not rounded to the nearest cent");
        }
        return true;
    }
}
