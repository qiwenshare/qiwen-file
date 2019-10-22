package com.mac.common.proxy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ProxyUtil {


    public static boolean connectTest(String ip, int port){
        boolean isTestSuccess = true;
        try {
            Document doc = Jsoup.connect("http://www.ityouknow.com/")
                    .timeout(5000)
                    .proxy(ip, port)
                    .get();
        } catch (IOException e) {
            isTestSuccess = false;
        }
        return isTestSuccess;
    }
}
