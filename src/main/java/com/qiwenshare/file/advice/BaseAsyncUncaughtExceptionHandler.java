package com.qiwenshare.file.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import reactor.util.annotation.NonNullApi;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
public class BaseAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {
    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
        log.error("捕获线程异常method[{}] params{}", method, Arrays.toString(objects));
        log.error("线程异常");
    }
}
