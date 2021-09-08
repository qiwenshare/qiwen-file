package com.qiwenshare.file.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiwenshare.common.exception.NotLoginException;
import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.common.result.RestResult;
import com.qiwenshare.common.util.JjwtUtil;
import com.qiwenshare.common.util.PasswordUtil;
import com.qiwenshare.file.api.IUserService;
import com.qiwenshare.file.component.UserDealComp;
import com.qiwenshare.file.controller.UserController;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class UserService extends ServiceImpl<UserMapper, UserBean> implements IUserService {

    @Resource
    UserMapper userMapper;
    @Resource
    UserDealComp userDealComp;

    @Override
    public UserBean getUserBeanByToken(String token){
        Claims c = null;
        if (StringUtils.isEmpty(token)) {
            return null;
        }
//        if (!token.startsWith("Bearer ")) {
//            throw new NotLoginException("token格式错误");
//        }
        token = token.replace("Bearer ", "");
        try {
            c = JjwtUtil.parseJWT(token);
        } catch (Exception e) {
            log.error("解码异常:" + e);
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
            if (saveUserBean == null) {
                return null;
            }
            tokenPassword = tokenUserBean.getPassword();
            savePassword = saveUserBean.getPassword();
        } else if (StringUtils.isNotEmpty(tokenUserBean.getQqPassword())) {
            saveUserBean = selectUserByopenid(tokenUserBean.getOpenId());
            if (saveUserBean == null) {
                return null;
            }
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
        //RestResult<String> restResult = new RestResult<String>();
        //判断验证码
        String telephone = userBean.getTelephone();
//        String saveVerificationCode = UserController.verificationCodeMap.get(telephone);
//        if (!saveVerificationCode.equals(userBean.getVerificationcode())){
//            restResult.setSuccess(false);
//            restResult.setErrorMessage("验证码错误！");
//            return restResult;
//        }
        UserController.verificationCodeMap.remove(telephone);

        if (userDealComp.isUserNameExit(userBean)) {
            return RestResult.fail().message("用户名已存在！");
        }
        if (!userDealComp.isPhoneFormatRight(userBean.getTelephone())){
            return RestResult.fail().message("手机号格式不正确！");
        }
        if (userDealComp.isPhoneExit(userBean)) {
            return RestResult.fail().message("手机号已存在！");
        }


        String salt = PasswordUtil.getSaltValue();
        String newPassword = new SimpleHash("MD5", userBean.getPassword(), salt, 1024).toHex();

        userBean.setSalt(salt);

        userBean.setPassword(newPassword);
        userBean.setRegisterTime(DateUtil.getCurrentTime());
        int result = userMapper.insertUser(userBean);
        userMapper.insertUserRole(userBean.getUserId(), 2);
        if (result == 1) {
            return RestResult.success();
        } else {
            return RestResult.fail().message("注册用户失败，请检查输入信息！");
        }
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
