package com.mac.common.cbb;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mac.common.domain.ProxyBean;
import com.mac.common.proxy.ProxyConstant;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class HttpRequest {

    public static List<ProxyBean> proxyBeans = new ArrayList<>();
    public HttpRequest(){
        String proxyRequest = getProxyRequestResult(ProxyConstant.SERVER_PROXY_COUNT);
        JSONObject resultObj = JSONObject.parseObject(proxyRequest);
        String requestJson = JSON.toJSONString(resultObj.getObject( "data", List.class));
        proxyBeans = JSONArray.parseArray(requestJson, ProxyBean.class);
    }

    public String sendGet(String url, Map<String, String> param){
        ProxyBean proxyBean = null;
        int currentRandomIndex = 0;
        if (proxyBeans.size() <= 1) {
            return "没有可用的代理";
        } else{
            currentRandomIndex = new Random().nextInt(proxyBeans.size());
            proxyBean = (ProxyBean) proxyBeans.get(currentRandomIndex);
        }

        boolean isRequestSuccess = false;
        Document doc = null;

        try {
            doc = Jsoup.connect(url)
                    .timeout(5000)
                    .proxy(proxyBean.getProxyip(), proxyBean.getProxyport())
                    .data(param)
                    .ignoreContentType(true)
                    .userAgent(ProxyConstant.userAgentArr[new Random().nextInt(ProxyConstant.userAgentArr.length)])
                    .header("referer","http://www.sse.com.cn/assortment/stock/list/share/")//这个来源记得换..
                    .get();
            isRequestSuccess = true;
        } catch (IOException e){
            isRequestSuccess = false;
            proxyBeans.remove(currentRandomIndex);
            System.out.println("此代理不通，正在重试。。" + JSON.toJSONString(proxyBean));
        }
        if (!isRequestSuccess){
            return sendGet(url, param);
        }

        return doc.text();
    }

    /**
     * 获取代理列表
     * @return 返回代理列表
     */
    public static String getProxyRequestResult(int count) {
        StringBuffer requestResult = new StringBuffer();
        BufferedReader in = null;

        try {
            URL realUrl = new URL("http://"+ProxyConstant.SERVER_IP+"/api/proxy/getproxylist?count="+count);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                requestResult.append(line);
            }
        }
        // 使用finally块来关闭输入流
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }


        return requestResult.toString();
    }


    public static void main(String[] args) {
        //初始化对象
        HttpRequest httpRequest = new HttpRequest();
        //参数
        Map<String, String> param = new HashMap<>();
        param.put("v", "3.1.9y");
        //发送请求
        String result = httpRequest.sendGet("http://www.sse.com.cn/home/public/querySearch/queryConfig/map.js", param);
        System.out.println(result);
    }
}