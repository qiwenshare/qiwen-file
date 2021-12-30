package com.qiwenshare.file.config.openapi;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI qiwenFileOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("奇文网盘 API")
                        .description("基于Spring Boot 2 + VUE CLI@3框架开发的分布式文件管理系统。")
                        .version("v1.1.2")
                        .license(new License().name("MIT").url("http://springdoc.org")))
                        .externalDocs(new ExternalDocumentation()
                        .description("奇文网盘说明文档")
                        .url("https://pan.qiwenshare.com/docs/"));
    }

}
