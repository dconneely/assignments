package com.davidconneely.purchase;

import com.davidconneely.purchase.dao.PurchaseEntity;
import com.davidconneely.purchase.dto.PurchaseRequest;
import com.davidconneely.purchase.dto.PurchaseResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class TestUtils {
    public static final UUID ID_GOOD = UUID.fromString("ce65943d-61eb-453e-bc9d-c958fcf70b79");
    public static final String DESCRIPTION_GOOD = "test description";
    public static final String DESCRIPTION_LONG = "this description text is too long to fit into 50 characters";
    public static final LocalDate TRANSACTION_DATE_GOOD = LocalDate.of(2015, 9, 21);
    public static final BigDecimal PURCHASE_AMOUNT_GOOD = BigDecimal.valueOf(10234, 2);
    public static final BigDecimal PURCHASE_AMOUNT_NEGV = BigDecimal.valueOf(-2999, 2);
    public static final BigDecimal EXCHANGE_RATE_GOOD = BigDecimal.valueOf(12864, 4);
    public static final BigDecimal CONVERTED_AMOUNT_GOOD = BigDecimal.valueOf(13165, 2); // PURCHASE_AMOUNT_GOOD.multiply(EXCHANGE_RATE_GOOD).setScale(2, RoundingMode.HALF_UP);

    public static PurchaseRequest newGoodRequest() {
        return new PurchaseRequest(DESCRIPTION_GOOD, TRANSACTION_DATE_GOOD, PURCHASE_AMOUNT_GOOD);
    }

    public static PurchaseResponse newGoodResponse() {
        return new PurchaseResponse(
                ID_GOOD, DESCRIPTION_GOOD, TRANSACTION_DATE_GOOD, PURCHASE_AMOUNT_GOOD,
                EXCHANGE_RATE_GOOD, CONVERTED_AMOUNT_GOOD);
    }

    public static PurchaseEntity newGoodEntity() {
        PurchaseEntity entity = new PurchaseEntity();
        // entity might not need its ID field to be set, so leave it to the test to decide.
        entity.setDescription(DESCRIPTION_GOOD);
        entity.setTransactionDate(TRANSACTION_DATE_GOOD);
        entity.setPurchaseAmount(PURCHASE_AMOUNT_GOOD);
        return entity;
    }
}
