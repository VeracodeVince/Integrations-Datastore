package com.checkmarx.integrations.datastore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper result = new ModelMapper();

        // Without this setting, modelMapper may show unexpected behavior.
        // E.g. trying to map SCMCreateDto.clientId to Scm.id, which doesn't make sense.
        result.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return result;
    }

    @Bean
    public OpenAPI swaggerConfiguration() {
        return new OpenAPI()
                .info(new Info().title("CxIntegrations Datastore API")
                        .version("v0.0.1")
                        .description("CxIntegrations Datastore Endpoints"));
    }
}