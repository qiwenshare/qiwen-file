package com.qiwenshare.file.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.qiwenshare.common.cbb.RestResult;
import com.qiwenshare.common.domain.AliyunOSS;
import com.qiwenshare.common.util.JjwtUtil;
import com.qiwenshare.file.api.IUserService;
import com.qiwenshare.file.config.QiwenFileConfig;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.vo.user.UserLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制类
 *
 * @author ma116
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Resource
    IUserService userService;

    @Autowired
    QiwenFileConfig qiwenFileConfig;


    public static Map<String, String> verificationCodeMap = new HashMap<>();

    public static final int TEXT = 4;

    /**
     * 当前模块
     */
    public static final String CURRENT_MODULE = "用户管理";

    @Operation(summary = "用户注册")
    @RequestMapping(value = "/adduser", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> addUser(@RequestBody UserBean userBean) {
        RestResult<String> restResult = null;

        restResult = userService.registerUser(userBean);

        return restResult;
    }

    @Operation(summary = "用户登录")
    @RequestMapping("/userlogin")
    @ResponseBody
    public RestResult<UserLoginVo> userLogin(@RequestBody UserBean userBean) {
        RestResult<UserLoginVo> restResult = new RestResult<UserLoginVo>();
        UserBean saveUserBean = userService.findUserInfoByTelephone(userBean.getUsername());

        String jwt = "";
        try {
            jwt = JjwtUtil.createJWT("qiwenshare", "qiwen", JSON.toJSONString(saveUserBean));
        } catch (Exception e) {
            log.info("登录失败：{}", e);
            restResult.setSuccess(false);
            restResult.setErrorMessage("登录失败！");
            return restResult;
        }

        String password = new SimpleHash("MD5", userBean.getPassword(), saveUserBean.getSalt(), 1024).toHex();
        if (password.equals(saveUserBean.getPassword())) {

            UserLoginVo userLoginVo = new UserLoginVo();
            BeanUtil.copyProperties(userBean, userLoginVo);
            userLoginVo.setToken(jwt);
            restResult.setData(userLoginVo);
            restResult.setSuccess(true);
        } else {
            restResult.setSuccess(false);
            restResult.setErrorMessage("手机号或密码错误！");
        }

        return restResult;
    }

    @Operation(summary = "检查用户登录信息")
    @GetMapping("/checkuserlogininfo")
    @ResponseBody
    public RestResult<UserBean> checkUserLoginInfo(@RequestHeader("token") String token) {
        RestResult<UserBean> restResult = new RestResult<UserBean>();

        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        if (sessionUserBean != null) {

            restResult.setData(sessionUserBean);
            restResult.setSuccess(true);
            AliyunOSS oss = qiwenFileConfig.getAliyun().getOss();
            String domain = oss.getDomain();
            restResult.getData().setViewDomain(domain);
            String bucketName = oss.getBucketName();
            String endPoint = oss.getEndpoint();
            restResult.getData().setDownloadDomain(bucketName + "." + endPoint);
        } else {
            restResult.setSuccess(false);
            restResult.setErrorMessage("用户暂未登录");
        }

        return restResult;
    }

    @Operation(summary = "得到用户信息通过id")
    @RequestMapping("/getuserinfobyid")
    @ResponseBody
    public String getUserInfoById(int userId) {
        RestResult<UserBean> restResult = new RestResult<UserBean>();

        UserBean userBean = userService.getById(userId);
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
