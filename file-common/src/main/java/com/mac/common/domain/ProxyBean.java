package com.mac.common.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 代理实体类
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ProxyBean {
    private long proxyid;
    private String proxyip;
    private int proxyport;
    private int testresult;
    private String testtime;


    public ProxyBean(String proxyip, int proxyport) {
        this.proxyip = proxyip;
        this.proxyport = proxyport;
    }

}
