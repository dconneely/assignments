package com.davidconneely.purchase.dto;

import static com.davidconneely.purchase.TestUtils.newGoodRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public final class PurchaseRequestTest {
  @Test
  public void testEquals() {
    PurchaseRequest dto1 = newGoodRequest();
    PurchaseRequest dto2 = newGoodRequest();
    assertEquals(dto1, dto2);
  }
}
