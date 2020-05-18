package com.mac.scp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mac.common.cbb.DateUtil;
import com.mac.common.cbb.RestResult;
import com.mac.common.util.PasswordUtil;
import com.mac.scp.api.IUserService;
import com.mac.scp.controller.UserController;
import com.mac.scp.domain.UserBean;
import com.mac.scp.domain.UserImageBean;
import com.mac.scp.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements IUserService {
	@Resource
	UserMapper userMapper;

	/**
	 * 用户注册
	 */
	@Override
	public RestResult<String> registerUser(UserBean userBean) {
		RestResult<String> restResult = new RestResult<String>();
		//判断验证码
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


		String salt = PasswordUtil.getSaltValue();

		userBean.setSalt(salt);

		userBean.setPassword(userBean.getPassword());
		userBean.setRegistertime(DateUtil.getCurrentTime());
		int result = userMapper.insertUser(userBean);
		userMapper.insertUserRole(userBean.getUserId(), 2);
		UserImageBean userImageBean = new UserImageBean();
		userImageBean.setImageurl("");
		userImageBean.setUserid(userBean.getUserId());
		if (result == 1) {
			restResult.setSuccess(true);
		} else {
			restResult.setSuccess(false);
			restResult.setErrorCode("100000");
			restResult.setErrorMessage("注册用户失败，请检查输入信息！");
		}
		return restResult;
	}


	/**
	 * 检测用户名是否存在
	 *
	 * @param userBean
	 */
	private Boolean isUserNameExit(UserBean userBean) {
		UserBean result = userMapper.selectUserByUserName(userBean);
		return result != null;
	}

	/**
	 * 检测手机号是否存在
	 *
	 * @param userBean
	 * @return
	 */
	private Boolean isPhoneExit(UserBean userBean) {
		UserBean result = userMapper.selectUserByTelephone(userBean);
		return result != null;
	}

	private Boolean isPhoneFormatRight(String phone) {
		String regex = "^1\\d{10}";
		return Pattern.matches(regex, phone);
	}

	/**
	 * 通过手机号获取用户信息
	 *
	 * @param telephone
	 * @return
	 */
	@Override
	public UserBean findUserInfoByTelephone(String telephone) {
		return userMapper.selectOne(new LambdaQueryWrapper<UserBean>().eq(UserBean::getTelephone, telephone));

	}


	@Override
	public UserBean getUserInfoById(long userId) {
		return userMapper.selectUserById(userId);
	}

	@Override
	public UserBean selectUserByopenid(String openid) {
		return userMapper.selectUserByopenid(openid);
	}


}
