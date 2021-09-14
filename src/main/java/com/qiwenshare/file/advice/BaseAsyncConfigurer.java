package com.qiwenshare.file.advice;


import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

/**
 * 异步线程池配置 AsyncConfigurer在applicationContext早期初始化，如果需要依赖于其它的bean，尽可能的将它们声明为lazy
 */
@Slf4j
@Component
@EnableConfigurationProperties(ThreadPoolProperties.class)
public class BaseAsyncConfigurer implements AsyncConfigurer {

    @Autowired
    private ThreadPoolProperties threadPoolProperties;

    /**
     * 定义线程池
     * @return Executor
     */
    @Bean("asyncTaskExecutor")
    @Override
    public Executor getAsyncExecutor() {
        //Java虚拟机可用的处理器数
        int processors = Runtime.getRuntime().availableProcessors();
        //定义线程池
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //核心线程数
        taskExecutor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        //线程池最大线程数,默认：40000
        taskExecutor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        //线程池中线程最大空闲时间，默认：60，单位：秒
        taskExecutor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());

        taskExecutor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        taskExecutor.setThreadNamePrefix(threadPoolProperties.getThreadNamePrefix());

        //初始化
        taskExecutor.initialize();

        return taskExecutor;
    }

    /**
     * 异步方法执行的过程中抛出的异常捕获
     *
     * @return AsyncUncaughtExceptionHandler
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new BaseAsyncUncaughtExceptionHandler();
    }
}

