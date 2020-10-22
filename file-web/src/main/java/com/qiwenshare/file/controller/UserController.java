package com.qiwenshare.file.controller;

import com.alibaba.fastjson.JSON;
import com.qiwenshare.file.domain.RestResult;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dehui dou
 * @date 2020/10/22 14:25
 * @description 用户控制类
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController{

    @Autowired
    UserService userService;
    public static Map<String, String> verificationCodeMap = new HashMap<>();

    @RequestMapping(value = "/adduser", method = RequestMethod.POST)
    public RestResult<String> addUser(@RequestBody UserBean userBean) {
        return userService.registerUser(userBean);
    }

    /**
     * @author dehui dou
     * @description 用户登录
     * @param userBean
     * @return com.qiwenshare.common.cbb.RestResult<com.qiwenshare.file.domain.UserBean>
     */
    @RequestMapping("/userlogin")
    public RestResult<UserBean> userLogin(@RequestBody UserBean userBean) {
        RestResult<UserBean> restResult = new RestResult<UserBean>();
        restResult.setSuccess(true);
        try {
            SecurityUtils.getSubject().login(new UsernamePasswordToken(userBean.getUsername(), userBean.getPassword()));
        } catch (Exception e) {
            restResult.setSuccess(false);
            restResult.setErrorMessage("手机号或密码错误！");
        }
        UserBean sessionUserBean = getLoginUserInfo();
        if (sessionUserBean != null) {
            restResult.setData(sessionUserBean);
            restResult.setSuccess(true);
        } else {
            restResult.setSuccess(false);
            restResult.setErrorMessage("手机号或密码错误！");
        }
        return restResult;
    }

    /**
     * @author dehui dou
     * @description 用户注销
     * @param token
     * @return com.qiwenshare.common.cbb.RestResult<java.lang.String>
     */
    @RequestMapping(value = "/userlogout", method = RequestMethod.POST)
    public RestResult<String> userLogout(@RequestHeader("token") String token) {
        RestResult<String> restResult = new RestResult<String>();
        SecurityUtils.getSubject().logout();
        restResult.setSuccess(true);
        restResult.setData("注销登录成功！");
        return restResult;
    }

    /**
     * @author dehui dou
     * @description 检查用户登录信息
     * @param token
     * @return com.qiwenshare.common.cbb.RestResult<com.qiwenshare.file.domain.UserBean>
     */
    @GetMapping("/checkuserlogininfo")
    public RestResult<UserBean> checkUserLoginInfo(@RequestHeader("token") String token) {
        RestResult<UserBean> restResult = new RestResult<UserBean>();
        UserBean sessionUserBean = getLoginUserInfo();
        if (sessionUserBean != null) {
            UserBean userInfo = userService.getUserInfoById(sessionUserBean.getUserId());

            restResult.setData(userInfo);
            restResult.setSuccess(true);
        } else {
            restResult.setSuccess(false);
            restResult.setErrorMessage("用户暂未登录");
        }
        return restResult;
    }

    /**
     * @author dehui dou
     * @description 得到用户信息通过id
     * @param userId
     * @return java.lang.String
     */
    @RequestMapping("/getuserinfobyid")
    public String getUserInfoById(int userId) {
        RestResult<UserBean> restResult = new RestResult<UserBean>();
        UserBean userBean = userService.getUserInfoById(userId);
        if (userBean == null) {
            restResult.setSuccess(false);
            restResult.setErrorCode("100001");
            restResult.setErrorMessage("用户不存在！");
        } else {
            restResult.setSuccess(true);
            restResult.setData(userBean);
        }
        String resultJson = JSON.toJSONString(restResult);
        return resultJson;
    }

}
