package com.billpayments.billpaymentsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI payEaseOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("PayEase Bill Payment System API")
                        .description("REST API for authentication, wallet funding, bill payments, notifications, profile management, and transaction history.")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Ezidiegwu Nnenna Favour")
                                .url("https://github.com/nne-nna")));
    }
}
