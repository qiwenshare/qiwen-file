package com.mac.scp.controller;

import com.mac.common.cbb.RestResult;
import com.mac.scp.api.IUserService;
import com.mac.scp.domain.UserBean;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制类
 *
 * @author ma116
 */
@RestController
@RequestMapping("/user")
public class UserController {
	public static final int TEXT = 4;
	/**
	 * 当前模块
	 */
	public static final String CURRENT_MODULE = "用户管理";
	public static Map<String, String> verificationCodeMap = new HashMap<>();
	@Resource
	IUserService userService;


	// TODO 用户注册
	@PostMapping("/adduser")
	@ResponseBody
	public RestResult<String> addUser(@RequestBody UserBean userBean) {
		return userService.registerUser(userBean);
	}


	/**
	 * /**
	 * 用户登录
	 *
	 * @param userBean
	 * @return
	 */
	// TODO 用户登录
	@RequestMapping("/userlogin")
	@ResponseBody
	public RestResult<UserBean> userLogin(@RequestBody UserBean userBean) {
		RestResult<UserBean> restResult = new RestResult<UserBean>();
		restResult.setSuccess(true);
		try {
			SecurityUtils.getSubject().login(new UsernamePasswordToken(userBean.getUsername(), userBean.getPassword()));
		} catch (Exception e) {
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

		return restResult;
	}

	/**
	 * 用户注销
	 *
	 * @return
	 */
	// TODO 退出登录
	@PostMapping("/userlogout")
	@ResponseBody
	public RestResult<String> userLogout() {
		RestResult<String> restResult = new RestResult<String>();

		SecurityUtils.getSubject().logout();
		restResult.setSuccess(true);
		restResult.setData("注销登录成功！");

		return restResult;
	}

	/**
	 * 检查用户登录信息
	 *
	 * @return
	 */
	// TODO 检查用户登录信息
	@RequestMapping("/checkuserlogininfo")
	@ResponseBody
	public RestResult<UserBean> checkUserLoginInfo(HttpServletRequest request) {
		RestResult<UserBean> restResult = new RestResult<UserBean>();
		UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
		if (sessionUserBean != null) {
			UserBean userInfo = userService.getUserInfoById(sessionUserBean.getUserId());
			restResult.setData(userInfo);
			restResult.setSuccess(true);
		} else {
			restResult.setSuccess(false);
			restResult.setErrorMessage("用户暂未登录");
		}
		return restResult;
	}


}
