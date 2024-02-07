package com.davidconneely.purchase;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class TestUtils {
    public static final String DESCRIPTION_GOOD = "test description";
    public static final String DESCRIPTION_LONG = "this description text is too long to fit into 50 characters";
    public static final LocalDate TRANSACTION_DATE_GOOD = LocalDate.of(2015, 9, 21);
    public static final BigDecimal PURCHASE_AMOUNT_GOOD = BigDecimal.valueOf(10205, 2);
    public static final BigDecimal PURCHASE_AMOUNT_NEGV = BigDecimal.valueOf(-2999, 2);

    public static PurchaseDto newGoodDto() {
        return new PurchaseDto(DESCRIPTION_GOOD, TRANSACTION_DATE_GOOD, PURCHASE_AMOUNT_GOOD);
    }

    public static PurchaseEntity newGoodEntity() {
        PurchaseEntity entity = new PurchaseEntity();
        entity.setDescription(DESCRIPTION_GOOD);
        entity.setTransactionDate(TRANSACTION_DATE_GOOD);
        entity.setPurchaseAmount(PURCHASE_AMOUNT_GOOD);
        return entity;
    }
}
