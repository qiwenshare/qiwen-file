package com.mac.scp.config.exception;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 全局异常捕获
 */
//@ControllerAdvice
//public class MyExceptionHandler {
//    private static Logger logger = LoggerFactory.getLogger(MyExceptionHandler.class);
//    @ExceptionHandler(value = Exception.class)
//    public void defaultExceptionHandler(HttpServletRequest req, HttpSession session,Exception e){
//        //String errorSource=  session.getAttribute("errorSource").toString();
//        logger.error(e.getMessage());
//        //根据抓获的异常类型，做逻辑处理，并打印日志信息
//
//        e.printStackTrace();
//        System.out.println("全局异常捕获中");
//    }
//
//
//}