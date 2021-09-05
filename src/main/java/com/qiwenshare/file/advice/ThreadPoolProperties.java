package com.qiwenshare.file.advice;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 异步线程池配置文件
 */
@Data
@ConfigurationProperties(prefix = "spring.async-thread-pool")
public class ThreadPoolProperties {
    /**
     * 核心线程数,默认：Java虚拟机可用线程数
     */
    private Integer corePoolSize=8;
    /**
     * 线程池最大线程数,默认：40000
     */
    private Integer maxPoolSize=500;
    /**
     * 线程队列最大线程数,默认：80000
     */
    private Integer queueCapacity = 5;

    /**
     * 线程池中线程最大空闲时间，默认：60，单位：秒
     */
    private Integer keepAliveSeconds = 600;
    /**
     * 自定义线程名前缀，默认：Async-ThreadPool-
     */
    private String threadNamePrefix = "async-threadpool-";

}

