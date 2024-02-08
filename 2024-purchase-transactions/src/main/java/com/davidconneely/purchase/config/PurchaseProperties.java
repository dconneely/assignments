package com.davidconneely.purchase.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.purchase")
@Data
public class PurchaseProperties {
    /**
     * Represent the URL to Treasury Reporting Rates of Exchange API.
     */
    @Data
    public static class TreasuryFiscalData {
        @NotBlank
        private String baseURL;
        @NotBlank
        private String ratesOfExchangeEndpoint;
    }

    @NotNull
    private TreasuryFiscalData treasuryFiscalData;
}
