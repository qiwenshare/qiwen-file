package com.qiwenshare.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "qiwen-file")
public class QiwenFileConfig {
    private boolean remoteLogin;

    private boolean shareMode;

    private AliyunConfig aliyun = new AliyunConfig();

    public boolean isRemoteLogin() {
        return remoteLogin;
    }

    public void setRemoteLogin(boolean remoteLogin) {
        this.remoteLogin = remoteLogin;
    }

    public boolean isShareMode() {
        return shareMode;
    }

    public void setShareMode(boolean shareMode) {
        this.shareMode = shareMode;
    }

    public AliyunConfig getAliyun() {
        return aliyun;
    }

    public void setAliyun(AliyunConfig aliyun) {
        this.aliyun = aliyun;
    }
}
