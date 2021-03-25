package com.qiwenshare.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "qiwen-file")
public class QiwenFileConfig {

    private boolean shareMode;

    private String storageType;
    private String cacheMode;
    private String localStoragePath;

    private AliyunConfig aliyun = new AliyunConfig();

}
