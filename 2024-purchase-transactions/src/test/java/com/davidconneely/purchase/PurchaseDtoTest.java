package com.davidconneely.purchase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import static com.davidconneely.purchase.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class PurchaseDtoTest {

    @Test
    public void testDtoEquals() {
        PurchaseDto dto1 = newGoodDto();
        PurchaseDto dto2 = newGoodDto();
        assertEquals(dto1, dto2);
    }

    @Test
    public void testCopyFromEntity() {
        PurchaseEntity entity = newGoodEntity();
        PurchaseDto dto = new PurchaseDto(entity.getDescription(), entity.getTransactionDate(), entity.getPurchaseAmount());
        assertEquals(DESCRIPTION_GOOD, dto.getDescription());
        assertEquals(TRANSACTION_DATE_GOOD, dto.getTransactionDate());
        assertEquals(PURCHASE_AMOUNT_GOOD, dto.getPurchaseAmount());
    }

    @Test
    public void testEntityEquals() {
        PurchaseEntity entity1 = newGoodEntity();
        PurchaseEntity entity2 = newGoodEntity();
        assertEquals(entity1, entity2);
    }

    @Test
    public void testCopyToEntity() {
        PurchaseDto dto = newGoodDto();
        PurchaseEntity entity = new PurchaseEntity();
        BeanUtils.copyProperties(dto, entity);
        assertEquals(DESCRIPTION_GOOD, entity.getDescription());
        assertEquals(TRANSACTION_DATE_GOOD, entity.getTransactionDate());
        assertEquals(PURCHASE_AMOUNT_GOOD, entity.getPurchaseAmount());
    }

}
