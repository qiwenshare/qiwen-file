package com.qiwenshare.file.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiwenshare.common.result.RestResult;
import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.common.util.HashUtils;
import com.qiwenshare.common.util.PasswordUtil;
import com.qiwenshare.common.util.security.JwtUser;
import com.qiwenshare.file.api.IUserService;
import com.qiwenshare.file.component.JwtComp;
import com.qiwenshare.file.component.UserDealComp;
import com.qiwenshare.file.controller.UserController;
import com.qiwenshare.file.domain.user.Role;
import com.qiwenshare.file.domain.user.UserBean;
import com.qiwenshare.file.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class UserService extends ServiceImpl<UserMapper, UserBean> implements IUserService, UserDetailsService {

    @Resource
    UserMapper userMapper;
    @Resource
    UserDealComp userDealComp;
    @Resource
    JwtComp jwtComp;

    @Override
    public Long getUserIdByToken(String token) {
        Claims c = null;
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        token = token.replace("Bearer ", "");
        token = token.replace("Bearer%20", "");
        try {
            c = jwtComp.parseJWT(token);
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
        UserBean user = userMapper.selectById(tokenUserBean.getUserId());
        if (user != null) {
            return user.getUserId();
        }

        return null;
    }


    @Override
    public RestResult<String> registerUser(UserBean userBean) {

        //判断验证码
        String telephone = userBean.getTelephone();

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
        String newPassword = HashUtils.hashHex("MD5", userBean.getPassword(), salt, 1024);

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

    public UserBean findUserInfoByTelephone(String telephone) {
        LambdaQueryWrapper<UserBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserBean::getTelephone, telephone);
        return userMapper.selectOne(lambdaQueryWrapper);

    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserBean user = userMapper.selectById(Long.valueOf(s));
        if (user == null) {
            throw new UsernameNotFoundException(String.format("用户不存在"));
        }
        List<Role> roleList = selectRoleListByUserId(user.getUserId());
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roleList) {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_" + role.getRoleName());
            authorities.add(simpleGrantedAuthority);
        }

        JwtUser jwtUser = new JwtUser(user.getUserId(), user.getUsername(), user.getPassword(),
                user.getAvailable(), authorities);
        return jwtUser;
    }

    @Override
    public List<Role> selectRoleListByUserId(long userId) {
        return userMapper.selectRoleListByUserId(userId);
    }

    @Override
    public String getSaltByTelephone(String telephone) {

        return userMapper.selectSaltByTelephone(telephone);
    }
    @Override
    public UserBean selectUserByTelephoneAndPassword(String username, String password) {
        return userMapper.selectUserByTelephoneAndPassword(username, password);
    }

}
