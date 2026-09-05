package com.davidconneely.purchase;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class PurchaseApplicationTest {
  @Autowired private ApplicationContext context;

  @Test
  void testContextLoads() {
    // We can do better than just an empty method.
    assertNotNull(context);
  }
}
