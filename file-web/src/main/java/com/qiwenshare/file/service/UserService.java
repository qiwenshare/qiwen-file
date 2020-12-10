package com.qiwenshare.file.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiwenshare.common.cbb.DateUtil;
import com.qiwenshare.common.cbb.RestResult;
import com.qiwenshare.common.util.JjwtUtil;
import com.qiwenshare.common.util.PasswordUtil;
import com.qiwenshare.file.api.IUserService;
import com.qiwenshare.file.controller.UserController;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
public class UserService extends ServiceImpl<UserMapper, UserBean> implements IUserService {

    @Resource
    UserMapper userMapper;

    @Override
    public UserBean getUserBeanByToken(String token){
        Claims c = null;
        try {
            log.debug("token:" + token);
            c = JjwtUtil.parseJWT(token);
        } catch (Exception e) {
            log.error("解码异常");
            e.printStackTrace();
            return null;
        }
        if (c == null) {
            log.info("解码为空");
            return null;
        }
        String subject = c.getSubject();
        log.debug("解析结果：" + subject);
        UserBean tokenUserBean = JSON.parseObject(subject, UserBean.class);

        UserBean saveUserBean = new UserBean();
        String tokenPassword = "";
        String savePassword = "";
        if (StringUtils.isNotEmpty(tokenUserBean.getPassword())) {
            saveUserBean = findUserInfoByTelephone(tokenUserBean.getTelephone());
            tokenPassword = tokenUserBean.getPassword();
            savePassword = saveUserBean.getPassword();
        } else if (StringUtils.isNotEmpty(tokenUserBean.getQqPassword())) {
            saveUserBean = selectUserByopenid(tokenUserBean.getOpenId());
            tokenPassword = tokenUserBean.getQqPassword();
            savePassword = saveUserBean.getQqPassword();
        }
        if (StringUtils.isEmpty(tokenPassword) || StringUtils.isEmpty(savePassword)) {
            return null;
        }
        if (tokenPassword.equals(savePassword)) {

            return saveUserBean;
        } else {
            return null;
        }
    }


    @Override
    public UserBean selectUserByopenid(String openid) {
        LambdaQueryWrapper<UserBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserBean::getOpenId, openid);
        return userMapper.selectOne(lambdaQueryWrapper);

    }
    /**
     * 用户注册
     */
    @Override
    public RestResult<String> registerUser(UserBean userBean) {
        RestResult<String> restResult = new RestResult<String>();
        //判断验证码
        String telephone = userBean.getTelephone();
//        String saveVerificationCode = UserController.verificationCodeMap.get(telephone);
//        if (!saveVerificationCode.equals(userBean.getVerificationcode())){
//            restResult.setSuccess(false);
//            restResult.setErrorMessage("验证码错误！");
//            return restResult;
//        }
        UserController.verificationCodeMap.remove(telephone);
        if (userBean.getTelephone() == null || "".equals(userBean.getTelephone())){
            restResult.setSuccess(false);
            restResult.setErrorMessage("用户名不能为空！");
            return restResult;
        }
        if (userBean.getPassword() == null || "".equals(userBean.getPassword())){
            restResult.setSuccess(false);
            restResult.setErrorMessage("密码不能为空！");
            return restResult;
        }

        if (userBean.getUsername() == null || "".equals(userBean.getUsername())){
            restResult.setSuccess(false);
            restResult.setErrorMessage("用户名不能为空！");
            return restResult;
        }
        if (isUserNameExit(userBean)) {
            restResult.setSuccess(false);
            restResult.setErrorMessage("用户名已存在！");
            return restResult;
        }
        if (!isPhoneFormatRight(userBean.getTelephone())){
            restResult.setSuccess(false);
            restResult.setErrorMessage("手机号格式不正确！");
            return restResult;
        }
        if (isPhoneExit(userBean)) {
            restResult.setSuccess(false);
            restResult.setErrorMessage("手机号已存在！");
            return restResult;
        }


        String salt = PasswordUtil.getSaltValue();
        String newPassword = new SimpleHash("MD5", userBean.getPassword(), salt, 1024).toHex();

        userBean.setSalt(salt);

        userBean.setPassword(newPassword);
        userBean.setRegisterTime(DateUtil.getCurrentTime());
        int result = userMapper.insertUser(userBean);
        userMapper.insertUserRole(userBean.getUserId(), 2);
//        UserImageBean userImageBean = new UserImageBean();
//        userImageBean.setImageUrl("");
//        userImageBean.setUserId(userBean.getUserId());
        if (result == 1) {
            restResult.setSuccess(true);
            return restResult;
        } else {
            restResult.setSuccess(false);
            restResult.setErrorCode("100000");
            restResult.setErrorMessage("注册用户失败，请检查输入信息！");
            return restResult;
        }
    }



  /**
     * 检测用户名是否存在
     *
     * @param userBean
     */
    private Boolean isUserNameExit(UserBean userBean) {
        LambdaQueryWrapper<UserBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserBean::getUsername, userBean.getUsername());
        List<UserBean> list = userMapper.selectList(lambdaQueryWrapper);
        if (list != null && !list.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检测手机号是否存在
     *
     * @param userBean
     * @return
     */
    private Boolean isPhoneExit(UserBean userBean) {

        LambdaQueryWrapper<UserBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserBean::getTelephone, userBean.getTelephone());
        List<UserBean> list = userMapper.selectList(lambdaQueryWrapper);
        if (list != null && !list.isEmpty()) {
            return true;
        } else {
            return false;
        }

    }

    private Boolean isPhoneFormatRight(String phone){
        String regex = "^1\\d{10}";
        boolean isRight = Pattern.matches(regex, phone);
        return isRight;
    }


    /**
     * 通过手机号获取用户信息
     *
     * @param telephone
     * @return
     */
    public UserBean findUserInfoByTelephone(String telephone) {
        LambdaQueryWrapper<UserBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserBean::getTelephone, telephone);
        return userMapper.selectOne(lambdaQueryWrapper);

    }



}
