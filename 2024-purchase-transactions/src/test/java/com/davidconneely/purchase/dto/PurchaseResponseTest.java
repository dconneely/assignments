package com.davidconneely.purchase.dto;

import static com.davidconneely.purchase.TestUtils.newGoodResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PurchaseResponseTest {
  @Test
  public void testEquals() {
    PurchaseResponse dto1 = newGoodResponse();
    PurchaseResponse dto2 = newGoodResponse();
    assertEquals(dto1, dto2);
  }
}
