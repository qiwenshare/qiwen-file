package com.mac.common.controller;


import com.mac.common.exception.UnifiedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * 针对404 特殊处理
 *
 * @author WeiHongBin
 */

@ApiIgnore
@RestController
@RequestMapping("/error")
public class ErrorController {

	@Autowired
	private BasicErrorController basicErrorController;

	/**
	 * 指定 method 提升优先级 覆盖默认Error处理
	 *
	 * @param request request
	 */
	@RequestMapping(method = {
			RequestMethod.GET,
			RequestMethod.HEAD,
			RequestMethod.POST,
			RequestMethod.PUT,
			RequestMethod.PATCH,
			RequestMethod.DELETE,
			RequestMethod.OPTIONS,
			RequestMethod.TRACE
	})
	public void error(HttpServletRequest request) {
		ResponseEntity<Map<String, Object>> errorDetail = basicErrorController.error(request);
		HttpStatus httpStatus = errorDetail.getStatusCode();
		Object message = Objects.requireNonNull(errorDetail.getBody()).get("message");
		Object path = Objects.requireNonNull(errorDetail.getBody()).get("path");
		throw new UnifiedException(httpStatus, String.format("path: [%s] %s ", path, message.toString()));
	}
}
