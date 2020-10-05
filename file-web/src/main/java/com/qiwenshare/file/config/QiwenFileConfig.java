package com.qiwenshare.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "qiwen-file")
public class QiwenFileConfig {
    private boolean remoteLogin;

    public boolean isRemoteLogin() {
        return remoteLogin;
    }

    public void setRemoteLogin(boolean remoteLogin) {
        this.remoteLogin = remoteLogin;
    }
}
