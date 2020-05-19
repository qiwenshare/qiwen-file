package com.mac.scp.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mac.common.annotations.PassToken;
import com.mac.common.cbb.RestResult;
import com.mac.common.exception.UnifiedException;
import com.mac.common.util.BCryptPasswordEncoder;
import com.mac.scp.api.IUserService;
import com.mac.scp.domain.UserBean;
import com.mac.scp.mapper.UserMapper;
import com.mac.scp.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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


	@Autowired
	@SuppressWarnings("all")
	private UserMapper userMapper;

	/**
	 * 用户注册
	 *
	 * @param userBean
	 * @return
	 */
	@PassToken
	@PostMapping("/adduser")
	@ResponseBody
	public RestResult<String> addUser(@RequestBody UserBean userBean) {
		return userService.registerUser(userBean);
	}


	/**
	 * 用户登录
	 *
	 * @param userBean JSON 格式  {"username":"15999522810","password":"123456"}
	 * @return
	 */
	@PassToken
	@PostMapping("/userlogin")
	@ResponseBody
	public RestResult userLogin(@RequestBody UserBean userBean) {
		RestResult restResult = new RestResult<>();
		restResult.setSuccess(true);
		UserBean user = userMapper.selectOne(new LambdaQueryWrapper<UserBean>()
				.eq(UserBean::getTelephone, userBean.getUsername()));
		if (Objects.isNull(user)) {
			throw new UnifiedException("该用户不存在");
		}

		if (!new BCryptPasswordEncoder().matches(userBean.getPassword(), user.getPassword())) {
			throw new UnifiedException("密码错误");
		}
		String token = IdUtil.fastSimpleUUID();
		SessionFactory.getSession().put(token, user.getUserId());
		restResult.setData(JSONUtil.parseObj(user).put("token", token));
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
	public RestResult<String> userLogout(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
		RestResult<String> restResult = new RestResult<String>();
		SessionFactory.getSession().remove(token);
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
	@GetMapping("/checkuserlogininfo")
	@ResponseBody
	public RestResult<UserBean> checkUserLoginInfo(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
		RestResult<UserBean> restResult = new RestResult<UserBean>();
		Long s = SessionFactory.getSession().get(token);
		if (Objects.isNull(s)) {
			throw new UnifiedException(HttpStatus.UNAUTHORIZED, "token错误");
		}
		UserBean userBean = userMapper.selectById(s);
		if (Objects.isNull(userBean)) {
			throw new UnifiedException(HttpStatus.UNAUTHORIZED, "token 异常");
		}
		restResult.setData(userBean);
		return restResult;
	}


}
