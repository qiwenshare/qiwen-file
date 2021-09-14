package com.qiwenshare.file.component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qiwenshare.common.constant.RegexConstant;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.mapper.UserMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class UserDealComp {
    @Resource
    UserMapper userMapper;


    /**
     * 检测用户名是否存在
     *
     * @param userBean
     */
    public Boolean isUserNameExit(UserBean userBean) {
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
    public Boolean isPhoneExit(UserBean userBean) {

        LambdaQueryWrapper<UserBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserBean::getTelephone, userBean.getTelephone());
        List<UserBean> list = userMapper.selectList(lambdaQueryWrapper);
        if (list != null && !list.isEmpty()) {
            return true;
        } else {
            return false;
        }

    }

    public Boolean isPhoneFormatRight(String phone){
        boolean isRight = Pattern.matches(RegexConstant.PASSWORD_REGEX, phone);
        return isRight;
    }
}
