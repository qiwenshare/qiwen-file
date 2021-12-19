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
//
//    @Bean
//    public Docket demoApi() {
//        return new Docket(DocumentationType.OAS_30)
//                .select()
//                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.regex("(?!/error.*).*"))
//                .build();
//    }

//    @Bean
//    public GroupedOpenApi publicApi() {
//        return GroupedOpenApi.builder()
//                .group("springshop-public")
//                .pathsToMatch("/**")
//                .build();
//    }
//    @Bean
//    public GroupedOpenApi adminApi() {
//        return GroupedOpenApi.builder()
//                .group("springshop-admin")
//                .pathsToMatch("/**")
//                .build();
//    }
    /**
     * 前台API分组
     *
     * @return
     */
//    @Bean(value = "indexApi")
//    public Docket indexApi() {
//        return new Docket(DocumentationType.OAS_30)
//                .groupName("网站前端接口分组")
//                .apiInfo(apiInfo())
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.qiwenshare.file.controller"))
//                .paths(PathSelectors.any())
//                .build();
//    }
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("奇文网盘API")
//                .description("基于springboot + vue 框架开发的Web文件系统，旨在为用户提供一个简单、方便的文件存储方案，能够以完善的目录结构体系，对文件进行管理 。")
////                .termsOfServiceUrl("http://www.qiwenshare.com:8762/")
////				.contact("developer@mail.com")
//                .version("1.0.0")
//                .build();
//    }
}
