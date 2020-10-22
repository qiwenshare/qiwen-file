package com.qiwenshare.file.service;

import java.util.regex.Pattern;

import com.qiwenshare.file.util.DateUtils;
import com.qiwenshare.file.util.IDUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qiwenshare.file.domain.RestResult;
import com.qiwenshare.file.util.PasswordUtils;
import com.qiwenshare.file.controller.UserController;
import com.qiwenshare.file.dao.UserDao;
import com.qiwenshare.file.dao.UserRoleDao;
import com.qiwenshare.file.dao.entity.User;
import com.qiwenshare.file.dao.entity.UserRole;
import com.qiwenshare.file.domain.UserBean;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserRoleDao userRoleDao;

    /**
     * 检测用户名是否存在
     *
     * @param userBean
     */
    private Boolean isUserNameExit(UserBean userBean) {
        User result = userDao.getOneByUserName(userBean.getUsername());
        if (result != null) {
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
        User result = userDao.getOneByPhone(userBean.getTelephone());
        if (result != null) {
            return true;
        } else {
            return false;
        }
    }

    private Boolean isPhoneFormatRight(String phone) {
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
        User user = userDao.getOneByPhone(telephone);
        if (user == null) {
            return null;
        }
        UserBean userBean = new UserBean();
        BeanUtils.copyProperties(user, userBean);
        return userBean;
    }

    public UserBean selectUserByopenId(String openId) {
        User user = userDao.getOneByOpenId(openId);
        if (user == null) {
            return null;
        }
        UserBean userBean = new UserBean();
        BeanUtils.copyProperties(user, userBean);
        return userBean;
    }

    /**
     * 用户注册
     */
    public RestResult<String> registerUser(UserBean userBean) {
        RestResult<String> restResult = new RestResult<String>();
        // 判断验证码
        String telephone = userBean.getTelephone();
        UserController.verificationCodeMap.remove(telephone);
        if (userBean.getTelephone() == null || "".equals(userBean.getTelephone())) {
            restResult.setSuccess(false);
            restResult.setErrorMessage("用户名不能为空！");
            return restResult;
        }
        if (userBean.getPassword() == null || "".equals(userBean.getPassword())) {
            restResult.setSuccess(false);
            restResult.setErrorMessage("密码不能为空！");
            return restResult;
        }

        if (userBean.getUsername() == null || "".equals(userBean.getUsername())) {
            restResult.setSuccess(false);
            restResult.setErrorMessage("用户名不能为空！");
            return restResult;
        }
        if (isUserNameExit(userBean)) {
            restResult.setSuccess(false);
            restResult.setErrorMessage("用户名已存在！");
            return restResult;
        }
        if (!isPhoneFormatRight(userBean.getTelephone())) {
            restResult.setSuccess(false);
            restResult.setErrorMessage("手机号格式不正确！");
            return restResult;
        }
        if (isPhoneExit(userBean)) {
            restResult.setSuccess(false);
            restResult.setErrorMessage("手机号已存在！");
            return restResult;
        }

        String salt = PasswordUtils.getSaltValue();
        String newPassword = new SimpleHash("MD5", userBean.getPassword(), salt, 1024).toHex();

        userBean.setSalt(salt);

        userBean.setPassword(newPassword);
        userBean.setRegisterTime(DateUtils.getCurrentTime());
        int result = saveUserAndUserRole(userBean);
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

    public UserBean getUserInfoById(long userId) {
        User user = userDao.getOne(userId);
        if (user == null) {
            return null;
        }
        UserBean userBean = new UserBean();
        BeanUtils.copyProperties(user, userBean);
        return userBean;
    }

    private int saveUserAndUserRole(UserBean userBean) {
        User user = new User();
        Long userId = IDUtils.nextId();
        BeanUtils.copyProperties(userBean, user);
        user.setUserId(userId);
        int result = userDao.save(user);
        if (result < 0) {
            return result;
        }
        UserRole userRole = new UserRole(null, 2L);
        int save = userRoleDao.save(userRole);
        if (save < 0) {
            return save;
        }
        return 1;
    }
}
