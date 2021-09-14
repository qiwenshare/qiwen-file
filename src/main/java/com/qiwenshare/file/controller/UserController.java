package com.qiwenshare.file.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.qiwenshare.common.anno.MyLog;
import com.qiwenshare.common.result.RestResult;
import com.qiwenshare.common.util.JjwtUtil;
import com.qiwenshare.file.api.IUserService;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.dto.user.RegisterDTO;
import com.qiwenshare.file.vo.user.UserLoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;


@Tag(name = "user", description = "该接口为用户接口，主要做用户登录，注册和校验token")
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Resource
    IUserService userService;



    public static Map<String, String> verificationCodeMap = new HashMap<>();


    public static final String CURRENT_MODULE = "用户管理";

    @Operation(summary = "用户注册", description = "注册账号", tags = {"user"})
    @PostMapping(value = "/register")
    @MyLog(operation = "用户注册", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<String> addUser(@Valid @RequestBody RegisterDTO registerDTO) {
        RestResult<String> restResult = null;
        UserBean userBean = new UserBean();
        BeanUtil.copyProperties(registerDTO, userBean);
        restResult = userService.registerUser(userBean);

        return restResult;
    }

    @Operation(summary = "用户登录", description = "用户登录认证后才能进入系统", tags = {"user"})
    @GetMapping("/login")
    @MyLog(operation = "用户登录", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<UserLoginVo> userLogin(
            @Parameter(description = "登录手机号") String telephone,
            @Parameter(description = "登录密码") String password) {
        UserBean saveUserBean = userService.findUserInfoByTelephone(telephone);

        if (saveUserBean == null) {
            return RestResult.fail().message("手机号或密码错误！");
        }
        String jwt = "";
        try {
            UserBean sessionUserBean = new UserBean();
            sessionUserBean.setPassword(saveUserBean.getPassword());
            sessionUserBean.setQqPassword(saveUserBean.getQqPassword());
            sessionUserBean.setTelephone(saveUserBean.getTelephone());
            sessionUserBean.setOpenId(saveUserBean.getOpenId());
            jwt = JjwtUtil.createJWT("qiwenshare", "qiwen", JSON.toJSONString(sessionUserBean));
        } catch (Exception e) {
            log.info("登录失败：{}", e);
            return RestResult.fail().message("创建token失败！");
        }

        String passwordHash = new SimpleHash("MD5", password, saveUserBean.getSalt(), 1024).toHex();
        if (passwordHash.equals(saveUserBean.getPassword())) {

            UserLoginVo userLoginVo = new UserLoginVo();
            BeanUtil.copyProperties(saveUserBean, userLoginVo);
            userLoginVo.setToken("Bearer " + jwt);
            return RestResult.success().data(userLoginVo);
        } else {
            return RestResult.fail().message("手机号或密码错误！");
        }

    }

    @Operation(summary = "检查用户登录信息", description = "验证token的有效性", tags = {"user"})
    @GetMapping("/checkuserlogininfo")
    @ResponseBody
    public RestResult<UserBean> checkUserLoginInfo(@RequestHeader("token") String token) {

        if ("undefined".equals(token) || StringUtils.isEmpty(token)) {
            return RestResult.fail().message("用户暂未登录");
        }
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        if (sessionUserBean != null) {
            return RestResult.success().data(sessionUserBean);

        } else {
            return RestResult.fail().message("用户暂未登录");
        }

    }

}
