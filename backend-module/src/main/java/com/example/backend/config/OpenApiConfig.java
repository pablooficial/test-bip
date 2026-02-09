package com.example.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger/OpenAPI
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Benefícios")
                        .version("1.0.0")
                        .description("API REST para gerenciamento de benefícios com integração EJB\n\n" +
                                "## Funcionalidades\n" +
                                "- CRUD completo de benefícios\n" +
                                "- Transferência de valores entre benefícios\n" +
                                "- Validações de saldo e integridade\n" +
                                "- Controle de concorrência com optimistic locking\n\n" +
                                "## Tecnologias\n" +
                                "- Spring Boot 3.2.5\n" +
                                "- JPA/Hibernate\n" +
                                "- H2 Database\n" +
                                "- Jakarta EE (EJB)")
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("dev@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
