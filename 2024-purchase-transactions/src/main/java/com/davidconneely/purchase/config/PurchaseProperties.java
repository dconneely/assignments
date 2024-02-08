package com.davidconneely.purchase.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.purchase")
public class PurchaseProperties {
    /**
     * Full URL to Treasury Reporting Rates of Exchange API.
     */
    @NotBlank
    private String ratesOfExchangeUrl;
}
