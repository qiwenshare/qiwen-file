package com.qiwenshare.file.config.threadpool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 异步线程池配置文件
 */
@Data
@ConfigurationProperties(prefix = "spring.async-thread-pool")
public class AsyncThreadPoolProperties  {
    /**
     * 是否启动异步线程池，默认 false
     */
    private boolean enable;
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
    /**
     * 核心线程是否允许超时，默认false
     */
    private boolean allowCoreThreadTimeOut;
    /**
     * IOC容器关闭时是否阻塞等待剩余的任务执行完成，默认:false（必须设置setAwaitTerminationSeconds）
     */
    private boolean waitForTasksToCompleteOnShutdown;
    /**
     * 阻塞IOC容器关闭的时间，默认：10秒（必须设置setWaitForTasksToCompleteOnShutdown）
     */
    private int awaitTerminationSeconds = 10;

}

