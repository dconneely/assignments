package com.davidconneely.purchase.dto;

import com.davidconneely.purchase.dao.PurchaseEntity;
import org.junit.jupiter.api.Test;

import static com.davidconneely.purchase.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PurchaseConverterTest {
    @Test
    public void testEntityFromRequest() {
        PurchaseRequest dto = newGoodRequest();
        PurchaseConverter converter = new PurchaseConverter();
        PurchaseEntity entity = converter.entityFromRequest(dto);
        assertEquals(DESCRIPTION_GOOD, entity.getDescription());
        assertEquals(TRANSACTION_DATE_GOOD, entity.getTransactionDate());
        assertEquals(PURCHASE_AMOUNT_GOOD, entity.getPurchaseAmount());
    }

    @Test
    public void testResponseFromEntity() {
        PurchaseEntity entity = newGoodEntity();
        PurchaseConverter converter = new PurchaseConverter();
        PurchaseResponse dto = converter.responseFromEntity(entity, EXCHANGE_RATE_GOOD);
        assertEquals(DESCRIPTION_GOOD, dto.description());
        assertEquals(TRANSACTION_DATE_GOOD, dto.transactionDate());
        assertEquals(PURCHASE_AMOUNT_GOOD, dto.purchaseAmount());
        assertEquals(EXCHANGE_RATE_GOOD, dto.exchangeRate());
        assertEquals(CONVERTED_AMOUNT_GOOD, dto.convertedAmount());
    }
}
