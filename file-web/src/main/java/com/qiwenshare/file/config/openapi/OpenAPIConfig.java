package com.qiwenshare.file.config.openapi;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI qiwenFileOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("qiwenFileOpenAPI")
                .title("qiwenfile")
                .description("desc")
                .version("v0.0.1")
                .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                .description("spring wiki")
                .url("http://www.qiwenshare.com"));
    }
}
