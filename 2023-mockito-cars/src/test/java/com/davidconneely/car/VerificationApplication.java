package com.davidconneely.car;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class VerificationApplication {
    public static void main(String[] args) {
        SpringApplication.from(Application::main).with(VerificationApplication.class).run(args);
    }
}
