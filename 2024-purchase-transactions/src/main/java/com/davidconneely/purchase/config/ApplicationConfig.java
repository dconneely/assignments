package com.davidconneely.purchase.config;

import com.davidconneely.purchase.client.CachedRatesOfExchangeClient;
import com.davidconneely.purchase.client.LiveRatesOfExchangeClient;
import com.davidconneely.purchase.client.RatesOfExchangeClient;
import tools.jackson.databind.json.JsonMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public OpenAPI purchaseOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("purchase")
                .description("Store and retrieve purchase transactions")
                .version("1.0.0-SNAPSHOT"));
    }

    @Bean
    public RatesOfExchangeClient ratesOfExchangeClient(ClientProperties properties, JsonMapper jsonMapper, RestTemplateBuilder restTemplateBuilder) {
        if (properties.isCacheEnabled()) {
            return new CachedRatesOfExchangeClient(properties, jsonMapper, restTemplateBuilder);
        } else {
            return new LiveRatesOfExchangeClient(properties, restTemplateBuilder);
        }
    }
}
