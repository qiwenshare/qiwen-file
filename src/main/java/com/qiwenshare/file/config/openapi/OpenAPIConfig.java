package com.qiwenshare.file.config.openapi;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


@Configuration
public class OpenAPIConfig {

//    @Bean
//    public OpenAPI qiwenFileOpenAPI() {
//        return new OpenAPI()
//                .info(new Info().title("奇文网盘 API")
//                        .description("基于springboot + vue 框架开发的Web文件系统，旨在为用户提供一个简单、方便的文件存储方案，能够以完善的目录结构体系，对文件进行管理 。")
//                        .version("v0.0.1")
//                        .license(new License().name("MIT").url("http://springdoc.org")))
//                .externalDocs(new ExternalDocumentation()
//                        .description("奇文网盘gitee地址")
//                        .url("https://www.gitee.com/qiwen-cloud/qiwen-file"));
//    }
//
//    @Bean
//    public Docket demoApi() {
//        return new Docket(DocumentationType.OAS_30)
//                .select()
//                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.regex("(?!/error.*).*"))
//                .build();
//    }
    /**
     * 前台API分组
     *
     * @return
     */
    @Bean(value = "indexApi")
    public Docket indexApi() {
        return new Docket(DocumentationType.OAS_30)
                .groupName("网站前端接口分组")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.qiwenshare.file.controller"))
                .paths(PathSelectors.any())
                .build();
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("奇文网盘API")
                .description("基于springboot + vue 框架开发的Web文件系统，旨在为用户提供一个简单、方便的文件存储方案，能够以完善的目录结构体系，对文件进行管理 。")
//                .termsOfServiceUrl("http://www.qiwenshare.com:8762/")
//				.contact("developer@mail.com")
                .version("1.0.0")
                .build();
    }
}
