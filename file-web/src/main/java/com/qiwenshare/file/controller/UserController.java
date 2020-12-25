package com.qiwenshare.file.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.qiwenshare.common.cbb.RestResult;
import com.qiwenshare.common.domain.AliyunOSS;
import com.qiwenshare.common.util.JjwtUtil;
import com.qiwenshare.file.api.IUserService;
import com.qiwenshare.file.config.QiwenFileConfig;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.dto.RegisterDTO;
import com.qiwenshare.file.vo.user.UserLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "user", description = "该接口为用户接口，主要做用户登录，注册和校验token")
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


    @Operation(summary = "用户注册", description = "注册账号", tags = {"user"})
    @PostMapping(value = "/register")
    @ResponseBody
    public RestResult<String> addUser(@RequestBody RegisterDTO registerDTO) {
        RestResult<String> restResult = null;
        UserBean userBean = new UserBean();
        BeanUtil.copyProperties(registerDTO, userBean);
        restResult = userService.registerUser(userBean);

        return restResult;
    }

    @Operation(summary = "用户登录", description = "用户登录认证后才能进入系统", tags = {"user"})
    @GetMapping("/login")
    @ResponseBody
    public RestResult<UserLoginVo> userLogin(
            @Parameter(description = "登录用户名") String username,
            @Parameter(description = "登录密码") String password) {
        RestResult<UserLoginVo> restResult = new RestResult<UserLoginVo>();
        UserBean saveUserBean = userService.findUserInfoByTelephone(username);

        String jwt = "";
        try {
            jwt = JjwtUtil.createJWT("qiwenshare", "qiwen", JSON.toJSONString(saveUserBean));
        } catch (Exception e) {
            log.info("登录失败：{}", e);
            restResult.setSuccess(false);
            restResult.setErrorMessage("登录失败！");
            return restResult;
        }

        String passwordHash = new SimpleHash("MD5", password, saveUserBean.getSalt(), 1024).toHex();
        if (passwordHash.equals(saveUserBean.getPassword())) {

            UserLoginVo userLoginVo = new UserLoginVo();
            BeanUtil.copyProperties(saveUserBean, userLoginVo);
            userLoginVo.setToken(jwt);
            restResult.setData(userLoginVo);
            restResult.setSuccess(true);
        } else {
            restResult.setSuccess(false);
            restResult.setErrorMessage("手机号或密码错误！");
        }

        return restResult;
    }

    @Operation(summary = "检查用户登录信息", description = "验证token的有效性", tags = {"user"})
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

}
