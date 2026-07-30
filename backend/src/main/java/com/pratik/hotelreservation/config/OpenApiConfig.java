package com.pratik.hotelreservation.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hotelReservationOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Hotel Reservation Engine API")
                        .description("Production-ready Hotel Reservation System built with Spring Boot")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Pratik Kedari")
                                .email("your-email@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project Documentation"));
    }
}