package com.checkmarx.integrations.datastore.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class ApplicationSwaggerConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Integration Datastore API")
                        .version("v0.0.1")
                        .description("Integration Datastore End-Points"));
    }

}