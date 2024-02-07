package com.davidconneely.purchase.dto;

import org.junit.jupiter.api.Test;

import static com.davidconneely.purchase.TestUtils.newGoodRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class PurchaseRequestTest {
    @Test
    public void testEquals() {
        PurchaseRequest dto1 = newGoodRequest();
        PurchaseRequest dto2 = newGoodRequest();
        assertEquals(dto1, dto2);
    }
}
