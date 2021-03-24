package com.qiwenshare.file.advice;

import com.qiwenshare.common.result.RestResult;
import com.qiwenshare.common.result.ResultCodeEnum;
import com.qiwenshare.common.exception.UploadGeneralException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 该注解为统一异常处理的核心
 *
 * 是一种作用于控制层的切面通知（Advice），该注解能够将通用的@ExceptionHandler、@InitBinder和@ModelAttributes方法收集到一个类型，并应用到所有控制器上
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandlerAdvice {

    /**-------- 通用异常处理方法 --------**/
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResult error(Exception e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);

        return RestResult.fail();    // 通用异常结果
    }

    /**-------- 指定异常处理方法 --------**/
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResult error(NullPointerException e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);
        return RestResult.setResult(ResultCodeEnum.NULL_POINT);
    }
    /**-------- 下标越界处理方法 --------**/
    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResult error(IndexOutOfBoundsException e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);
        return RestResult.setResult(ResultCodeEnum.INDEX_OUT_OF_BOUNDS);
    }

    @ExceptionHandler(UploadGeneralException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public RestResult error(UploadGeneralException e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);
        return RestResult.setResult(ResultCodeEnum.REQUEST_TIMEOUT);
    }

    /**-------- 自定义定异常处理方法 --------**/
    @ExceptionHandler(CMSException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResult error(CMSException e) {
        e.printStackTrace();
        log.error("全局异常捕获：" + e);
        return RestResult.fail().message(e.getMessage()).code(e.getCode());
    }
}