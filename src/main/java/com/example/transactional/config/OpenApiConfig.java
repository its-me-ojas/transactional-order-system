package com.example.transactional.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI transactionalOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Transactional Order & Payment API")
                        .description(
                                "High-concurrency payment processing service with Idempotency and Pessimistic Locking protection.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Ojas")
                                .url("https://github.com/its-me-ojas/transactional-order-system")));
    }
}
