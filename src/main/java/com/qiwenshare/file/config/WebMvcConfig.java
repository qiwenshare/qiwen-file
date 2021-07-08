package com.qiwenshare.file.config;


import com.qiwenshare.file.interceptor.AuthenticationInterceptor;
import joptsimple.util.PathProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Autowired
	private AuthenticationInterceptor authenticationInterceptor;

//	@Override
//	public void addCorsMappings(CorsRegistry registry) {
//		registry.addMapping("/**").allowedOriginPatterns("*").allowCredentials(true).maxAge(3600).allowedMethods("GET", "POST","OPTIONS");
//	}

//	@Override
//	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
//		//默认的资源映射需要填写，不然不能正常访问
//		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
//		//配置外部资源目录的映射，/image目录为前端访问的路径，后面配置静态资源的绝对路径
//		registry.addResourceHandler("/image/**").addResourceLocations("file:"+uploadFolder);
//		//调用基类的方法
//		super.addResourceHandlers(registry);
//	}

	/**
	 * app拦截器
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		List<String> list = new ArrayList<>();
		list.add("/file/**");
		list.add("/filetransfer/**");
		list.add("/recoveryfile/**");
		list.add("/share/sharefile");
		list.add("/share/savesharefile");
		list.add("/share/shareList");
		registry.addInterceptor(authenticationInterceptor)
			.addPathPatterns(list)
			.excludePathPatterns("/file",
					"/filetransfer/downloadfile",
					"/filetransfer/preview");
	}

}