package com.checkmarx.integrations.datastore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationSwaggerConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("CxIntegrations Datastore API")
                        .version("v0.0.1")
                        .description("CxIntegrations Datastore End-Points"));
    }

}