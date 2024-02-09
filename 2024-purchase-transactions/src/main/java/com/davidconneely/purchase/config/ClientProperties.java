package com.davidconneely.purchase.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.purchase.treasury-fiscaldata")
@Data
public class ClientProperties {
        private String baseURL = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service/";
        private String ratesOfExchangeEndpoint = "v1/accounting/od/rates_of_exchange";
        private boolean cacheEnabled = true;
}
