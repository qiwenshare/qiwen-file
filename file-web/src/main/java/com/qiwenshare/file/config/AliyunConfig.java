package com.qiwenshare.file.config;

import com.qiwenshare.common.domain.AliyunOSS;

public class AliyunConfig {
    private AliyunOSS oss = new AliyunOSS();

    public AliyunOSS getOss() {
        return oss;
    }

    public void setOss(AliyunOSS oss) {
        this.oss = oss;
    }
}
