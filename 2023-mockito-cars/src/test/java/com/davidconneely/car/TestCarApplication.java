package com.davidconneely.car;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestCarApplication {
	public static void main(String[] args) {
		SpringApplication.from(CarApplication::main).with(TestCarApplication.class).run(args);
	}
}
