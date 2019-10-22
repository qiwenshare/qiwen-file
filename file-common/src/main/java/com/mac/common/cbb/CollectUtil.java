package com.mac.common.cbb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 通信工具类
 *
 * @author ma116
 */
public class CollectUtil {

    private static final Logger LOG = LoggerFactory.getLogger(CollectUtil.class);

    /**
     * java 后台获取访问客户端ip地址
     * @param request HttpServletRequest请求
     * @return IP地址
     */
    public String getClientIpAddress(HttpServletRequest request) {
        String clientIp = request.getHeader("x-forwarded-for");
        if (clientIp == null || clientIp.length() == 0
                || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.length() == 0
                || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.length() == 0
                || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }

    /**
     * 获取本地IP
     * @return IP地址
     */
    public String getLocalIp() {
        InetAddress addr = null;
        String ip = "";
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            LOG.error("获取本地IP失败");
        }
        if (addr != null) {
            ip = addr.getHostAddress().toString();
        }
        return ip;
    }

}
