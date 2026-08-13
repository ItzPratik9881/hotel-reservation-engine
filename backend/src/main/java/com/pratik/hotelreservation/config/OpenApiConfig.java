package com.pratik.hotelreservation.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Hotel Reservation Engine API",
                version = "1.0.0",
                description = "Enterprise Hotel Reservation Engine REST API"
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI hotelReservationOpenAPI() {

        final String securitySchemeName = "bearerAuth";

        io.swagger.v3.oas.models.info.Info apiInfo =
                new io.swagger.v3.oas.models.info.Info()
                        .title("Hotel Reservation Engine API")
                        .description(
                                "REST API for managing hotels, rooms, " +
                                "users, reservations, payments and analytics."
                        )
                        .version("1.0.0")
                        .contact(
                                new io.swagger.v3.oas.models.info.Contact()
                                        .name("Pratik Kedari")
                        );

        io.swagger.v3.oas.models.security.SecurityScheme jwtScheme =
                new io.swagger.v3.oas.models.security.SecurityScheme()
                        .name(securitySchemeName)
                        .type(
                                io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP
                        )
                        .scheme("bearer")
                        .bearerFormat("JWT");

        return new OpenAPI()
                .info(apiInfo)
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(securitySchemeName)
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        securitySchemeName,
                                        jwtScheme
                                )
                );
    }
}