package com.mac.scp.interceptor;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.mac.common.annotations.PassToken;
import com.mac.common.exception.UnifiedException;
import com.mac.common.util.UrlUtil;
import com.mac.scp.session.SessionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

/**
 * @author WeiHongBin
 */
@Slf4j
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

	/**
	 * 拦截器白名单
	 */
	private final List<String> urls = Lists.newArrayList(
			".*?/doc\\.html.*",
			".*?/v2/api-docs.*",
			".*?/v3/api-docs/swagger-config.*",
			".*?/v3/api-docs/.*",
			".*?/v2/api-docs-ext.*",
			".*?/swagger-resources.*",
			".*?/swagger-ui\\.html.*",
			".*?/swagger-resources/configuration/ui.*",
			".*?/swagger-resources/configuration/security.*",
			".*?/webjars.*",
			".*?/error.*",
			".*?/actuator.*"
	);

	@Value("${print-header:false}")
	private boolean printHeader;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		printHeader(request);
		String servletPath = request.getServletPath();
		// 白名单或者没有映射到方法直接  return true;
		if (UrlUtil.match(servletPath, urls) || (!(handler instanceof HandlerMethod))) {
			return true;
		}
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();
		log.info("ServletPath: [ {} ] Bean:[ {} ] Method:[ {} ]", servletPath, handlerMethod.getBean(), method.getName());
		if (method.isAnnotationPresent(PassToken.class)) {
			PassToken passToken = method.getAnnotation(PassToken.class);
			if (passToken.required()) {
				return true;
			}
		}
		String token = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (StrUtil.isBlank(token)) {
			throw new UnifiedException(HttpStatus.UNAUTHORIZED, "无token，请获取授权信息");
		}
		Long userId = SessionFactory.getSession().get(token);
		if (Objects.isNull(userId)) {
			throw new UnifiedException(HttpStatus.UNAUTHORIZED, "该Token不存在");
		}
		return true;
	}

	/**
	 * 打印请求头
	 *
	 * @param request 请求
	 */
	private void printHeader(HttpServletRequest request) {
		if (printHeader) {
			Enumeration<String> headerNames = request.getHeaderNames();
			StringBuilder headerStr = new StringBuilder(request.getServletPath());
			headerStr.append("\r\n**********Header Start**********");
			while (headerNames.hasMoreElements()) {
				String nextElement = headerNames.nextElement();
				String header = request.getHeader(nextElement);
				headerStr.append("\r\n").append(nextElement).append(" : ").append(header);
			}
			headerStr.append("\r\n**********Header End**********");
			log.info(headerStr.toString());
		}
	}
}
