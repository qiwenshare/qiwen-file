package com.qiwenshare.file.config.exception;

import com.qiwenshare.common.cbb.RestResult;
import com.qiwenshare.file.constants.CommonConstants.RepCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常捕获
 */
@RestControllerAdvice
public class ServiceControllerHandler {
    private static Logger logger = LoggerFactory.getLogger(ServiceControllerHandler.class);

    @ExceptionHandler(Exception.class)
    public RestResult processException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        logger.error(String.format("请求URI:%s,异常:%s", request.getRequestURI(), e.getMessage()), e);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return RepCodeEnum.EXCEPTION.parseError();
    }
}