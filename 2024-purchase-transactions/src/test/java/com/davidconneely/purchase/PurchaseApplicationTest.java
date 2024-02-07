package com.davidconneely.purchase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PurchaseApplicationTest {
    @Autowired
    private ApplicationContext context;

    @Test
    void testContextLoads() {
        // We can do better than just an empty method.
        assertNotNull(context);
    }

}
