package com.mac.scp.config.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 全局异常捕获
 */
@Slf4j
@ControllerAdvice
public class MyExceptionHandler {


	@ExceptionHandler(value = Exception.class)
	public void defaultExceptionHandler(HttpServletRequest req, HttpSession session, Exception e) {
		//String errorSource=  session.getAttribute("errorSource").toString();
		log.error("全局异常捕获：" + e);
	}


}