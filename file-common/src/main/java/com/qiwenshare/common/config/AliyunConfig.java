package com.qiwenshare.common.config;

import com.qiwenshare.common.domain.AliyunOSS;
import lombok.Data;

@Data
public class  AliyunConfig {
    private AliyunOSS oss = new AliyunOSS();


}
