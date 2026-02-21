package com.davidconneely.purchase.dao;

import static com.davidconneely.purchase.TestUtils.newGoodEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PurchaseEntityTest {
  @Test
  public void testEquals() {
    PurchaseEntity entity1 = newGoodEntity();
    PurchaseEntity entity2 = newGoodEntity();
    assertEquals(entity1, entity2);
  }
}
