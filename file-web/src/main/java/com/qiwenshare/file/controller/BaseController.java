package com.qiwenshare.file.controller;

import com.qiwenshare.file.domain.UserBean;
import org.apache.shiro.SecurityUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author dehui dou
 * @date 2020/10/22 14:36
 * @description
 */
public class BaseController {

    /**
     * @author dehui dou
     * @description 获取当前登陆人
     * @param
     * @return com.qiwenshare.file.domain.UserBean
     */
    protected UserBean getLoginUserInfo() {
        return (UserBean)SecurityUtils.getSubject().getPrincipal();
    }

    protected void printWriter(String data, HttpServletResponse response) throws IOException {
        response.setContentType("text/json;charset=utf-8");
        write(data, response);
    }

    private void write(String data, HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        printWriter.write(data);
        printWriter.flush();
        printWriter.close();
    }
}
