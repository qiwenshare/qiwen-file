package com.qiwenshare.file.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.qiwenshare.common.cbb.RestResult;
import com.qiwenshare.common.domain.TableData;
import com.qiwenshare.common.domain.TableQueryBean;
import com.qiwenshare.file.api.IFiletransferService;
import com.qiwenshare.file.api.IRemoteUserService;
import com.qiwenshare.file.api.IUserService;
import com.qiwenshare.file.config.QiwenFileConfig;
import com.qiwenshare.file.domain.UserBean;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制类
 *
 * @author ma116
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    IUserService userService;

    @Autowired
    IRemoteUserService remoteUserService;
    @Autowired
    QiwenFileConfig qiwenFileConfig;


    public static Map<String, String> verificationCodeMap = new HashMap<>();

    public static final int TEXT = 4;

    /**
     * 当前模块
     */
    public static final String CURRENT_MODULE = "用户管理";


    @RequestMapping(value = "/adduser", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> addUser(@RequestBody UserBean userBean) {
        RestResult<String> restResult = null;
        boolean isRemoteLogin = qiwenFileConfig.isRemoteLogin();
        if (isRemoteLogin) {
            restResult = remoteUserService.addUser(userBean);
        } else {
            restResult = userService.registerUser(userBean);
        }
        return restResult;
    }

    /**
     * 用户登录
     *
     * @param userBean
     * @return
     */
    @RequestMapping("/userlogin")
    @ResponseBody
    public RestResult<UserBean> userLogin(@RequestBody UserBean userBean) {
        RestResult<UserBean> restResult = new RestResult<UserBean>();
        boolean isRemoteLogin = qiwenFileConfig.isRemoteLogin();
        if (isRemoteLogin) {
            restResult = remoteUserService.userLogin(userBean);
        } else {
            restResult.setSuccess(true);
            try {
                SecurityUtils.getSubject().login(new UsernamePasswordToken(userBean.getUsername(), userBean.getPassword()));
            }catch (Exception e){
                restResult.setSuccess(false);
                restResult.setErrorMessage("手机号或密码错误！");
            }
            UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
            if (sessionUserBean != null) {
                restResult.setData(sessionUserBean);
                restResult.setSuccess(true);
            } else {
                restResult.setSuccess(false);
                restResult.setErrorMessage("手机号或密码错误！");
            }
        }

        return restResult;
    }

    /**
            * 用户注销
     *
             * @return
             */
    @RequestMapping(value = "/userlogout", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> userLogout(@RequestHeader("token") String token) {
        RestResult<String> restResult = new RestResult<String>();
        boolean isRemoteLogin = qiwenFileConfig.isRemoteLogin();
        if (isRemoteLogin) {
            restResult = remoteUserService.userLogout(token);
        } else {
            SecurityUtils.getSubject().logout();
            restResult.setSuccess(true);
            restResult.setData("注销登录成功！");
        }

        return restResult;
    }

     /* 检查用户登录信息
     *
     * @return
     */
    @GetMapping("/checkuserlogininfo")
    @ResponseBody
    public RestResult<UserBean> checkUserLoginInfo(@RequestHeader("token") String token) {
        RestResult<UserBean> restResult = new RestResult<UserBean>();
        boolean isRemoteLogin = qiwenFileConfig.isRemoteLogin();
        if (isRemoteLogin) {

            restResult = remoteUserService.checkUserLoginInfo(token);
        } else {
            UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
            if (sessionUserBean != null) {
                UserBean userInfo = userService.getUserInfoById(sessionUserBean.getUserId());
                restResult.setData(userInfo);
                restResult.setSuccess(true);
            } else {
                restResult.setSuccess(false);
                restResult.setErrorMessage("用户暂未登录");
            }
        }

        return restResult;
    }

    /**
     * 得到用户信息通过id
     *
     * @param userId
     * @return
     */
    @RequestMapping("/getuserinfobyid")
    @ResponseBody
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
