package com.mac.scp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    /**
     * 登录后将重定向到这里
     *
     * @return
     */
    @RequestMapping("/index")
    //@ResponseBody
    public String index() {
        return "index.html";
    }

    @RequestMapping("/403")
    public String unauthorizedRole() {
        System.out.println("------没有权限-------");
        return "/common/403.html";
    }

}