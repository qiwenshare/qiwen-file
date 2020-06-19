package com.mac.scp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

/**
 * SwaggerUI 配置
 *
 * @author WeiHongBin
 */

@Configuration
public class SwaggerConfiguration {

	@Bean
	public GroupedOpenApi userApi() {
		return GroupedOpenApi.builder().setGroup("奇文网盘")
				.pathsToMatch("/**")
				.build();
	}

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.components(new Components()
						.addSecuritySchemes("token", new SecurityScheme()
								.type(SecurityScheme.Type.APIKEY)
								.in(SecurityScheme.In.HEADER)
								.name(HttpHeaders.AUTHORIZATION)
						)
				)
				.info(new Info()
						.title("奇文网盘API")
						.version("1.0")
						.description("奇文网盘 接口文档"));
	}


}