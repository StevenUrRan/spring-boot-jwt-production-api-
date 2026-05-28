package com.sistema.productos.sistema_productos_jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .components(new Components()
                                                .addSecuritySchemes("bearerAuth",
                                                                new SecurityScheme()
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")
                                                                                .description("Ingresa el token JWT obtenido en /login")))
                                .info(new Info()
                                                .title("Sistema Productos JWT API")
                                                .version("1.0.0")
                                                .description("API REST para gestión de productos y facturas con autenticación JWT")
                                                .termsOfService("https://example.gidcom/terms")
                                                .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                                                .contact(new Contact()
                                                                .name("Steven Urieles Rangel")
                                                                .email("steven@example.com")))
                                .externalDocs(new ExternalDocumentation()
                                                .description("Repositorio del proyecto")
                                                .url("https://github.com/stevenurielesrangel/sistema-productos-jwt"));
        } 
}
