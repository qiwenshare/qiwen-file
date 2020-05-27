package com.mac.scp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
				.info(new Info()
						.title("奇文网盘API")
						.version("1.0")
						.description("奇文网盘 接口文档"));
	}


}