package com.mac.common.exception;


import com.mac.common.util.JsonUtil;
import com.mac.common.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 统一结果
 * TODO 在 supports 方法返回 true 即可修改响应体
 *
 * @author WeiHongBin
 */
@Slf4j
@RestControllerAdvice
public class UnifiedResult implements ResponseBodyAdvice {

	@Autowired
	private JsonUtil jsonUtil;

	@Override
	public boolean supports(MethodParameter returnType, Class converterType) {
		Method method = returnType.getMethod();
		assert method != null;
		Class<?> declaringClass = method.getDeclaringClass();
		Package declaringClassPackage = declaringClass.getPackage();
		String simpleMethodName = method.getName();
		String declaringClassName = declaringClass.getName();
		String classPackageName = declaringClassPackage.getName();
		log.info("UnifiedResult:[ {}#{} ]", declaringClassName, simpleMethodName);
		return false;
	}

	/**
	 * 在返回内容之前
	 *
	 * @param body                  内容
	 * @param returnType            返回类型
	 * @param selectedContentType   选择的内容类型
	 * @param selectedConverterType 选择转换器类型
	 * @param request               请求
	 * @param response              响应
	 * @return 统一封装后的结果集
	 */
	@Override
	public Object beforeBodyWrite(Object body,
	                              MethodParameter returnType,
	                              MediaType selectedContentType,
	                              Class selectedConverterType,
	                              ServerHttpRequest request,
	                              ServerHttpResponse response) {
		if (Objects.isNull(body)) {
			return new Result<>();
		}
		if (body instanceof String) {
			// 响应 String 类型在底层并不会json转换，必须手动json转换,否则会触发类型转换异常
			log.debug("统一返回,封装数据-String:{}", jsonUtil.writeValueAsString(body));
			return jsonUtil.writeValueAsString(new Result<>(body));
		}
		if (!StringUtils.isEmpty(body)) {
			log.debug("统一返回,封装数据:{}", jsonUtil.writeValueAsString(body));
		}
		return new Result<>(body);
	}
}
