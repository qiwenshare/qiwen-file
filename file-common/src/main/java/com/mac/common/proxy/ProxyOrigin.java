package com.mac.common.proxy;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ProxyOrigin {
    public static List<String> getConnectProxyList(){

        final WebClient webClient = new WebClient(BrowserVersion.CHROME);//新建一个模拟谷歌Chrome浏览器的浏览器客户端对象

        webClient.getOptions().setThrowExceptionOnScriptError(false);//当JS执行出错的时候是否抛出异常, 这里选择不需要
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);//当HTTP的状态非200时是否抛出异常, 这里选择不需要
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setCssEnabled(false);//是否启用CSS, 因为不需要展现页面, 所以不需要启用
        webClient.getOptions().setJavaScriptEnabled(true); //很重要，启用JS
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());//很重要，设置支持AJAX

        HtmlPage page = null;
        try {
            page = webClient.getPage("http://www.goubanjia.com/");//尝试加载上面图片例子给出的网页
            webClient.waitForBackgroundJavaScript(1000);//异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            webClient.close();
        }
        String pageXml = page.asXml();//直接将加载完成的页面转换成xml格式的字符串

        pageXml = pageXml.replaceAll("<[a-z]+ style=\"display *: *none;\">[^<>]*</[a-z]+>", " ");

        Document document = Jsoup.parse(pageXml);//获取html文档
        Elements ipElements = document.getElementsByClass("ip");
        List<String> proxyList = new ArrayList<String>();
        for (int i = 0; i < ipElements.size(); i++){
            proxyList.add(ipElements.get(i).text().replaceAll(" ", ""));

            System.out.println(ipElements.get(i).text().replaceAll(" ", ""));
        }

        return proxyList;
    }
}
