package com.mac.common.domain;

import javax.persistence.*;

/**
 * 代理实体类
 */

public class ProxyBean {
    private long proxyid;
    private String proxyip;
    private int proxyport;
    private int testresult;
    private String testtime;

    public ProxyBean(){

    }

    public ProxyBean(String proxyip, int proxyport) {
        this.proxyip = proxyip;
        this.proxyport = proxyport;
    }

    public String getProxyip() {
        return proxyip;
    }

    public void setProxyip(String proxyip) {
        this.proxyip = proxyip;
    }

    public long getProxyid() {
        return proxyid;
    }

    public void setProxyid(long proxyid) {
        this.proxyid = proxyid;
    }

    public int getProxyport() {
        return proxyport;
    }

    public void setProxyport(int proxyport) {
        this.proxyport = proxyport;
    }

    public int getTestresult() {
        return testresult;
    }

    public void setTestresult(int testresult) {
        this.testresult = testresult;
    }

    public String getTesttime() {
        return testtime;
    }

    public void setTesttime(String testtime) {
        this.testtime = testtime;
    }
}
