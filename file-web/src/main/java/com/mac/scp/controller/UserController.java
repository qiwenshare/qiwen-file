package com.mac.scp.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mac.common.annotations.PassToken;
import com.mac.common.exception.UnifiedException;
import com.mac.common.util.BCryptPasswordEncoder;
import com.mac.scp.dto.UserLoginDTO;
import com.mac.scp.dto.UserRegisterDTO;
import com.mac.scp.entity.User;
import com.mac.scp.session.SessionFactory;
import com.mac.scp.vo.UserLoginVO;
import com.mac.scp.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 用户 前端控制器
 *
 * @author ma116
 */
@Api(tags = "用户 前端控制器")
@RestController
@RequestMapping("/user")
public class UserController {

	@ApiOperation("用户注册")
	@PassToken
	@PostMapping("register")
	public boolean register(@Validated @RequestBody UserRegisterDTO dto) {
		User user;
		user = new User().selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
		if (Objects.nonNull(user)) {
			throw new UnifiedException("用户名已存在");
		}
		user = new User().selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone()));
		if (Objects.nonNull(user)) {
			throw new UnifiedException("手机号已存在");
		}
		user = new User();
		BeanUtil.copyProperties(dto, user);
		// 密码加密
		user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		return user.insert();
	}

	@ApiOperation("用户登录")
	@PassToken
	@PostMapping
	public UserLoginVO login(@Validated @RequestBody UserLoginDTO dto) {
		User user = new User().selectOne(new LambdaQueryWrapper<User>()
				.eq(User::getPhone, dto.getPhone()));
		if (Objects.isNull(user)) {
			throw new UnifiedException("该用户不存在");
		}
		if (!new BCryptPasswordEncoder().matches(dto.getPassword(), user.getPassword())) {
			throw new UnifiedException("密码错误");
		}
		UserLoginVO userVO = new UserLoginVO();
		BeanUtil.copyProperties(user, userVO);
		String token = IdUtil.fastSimpleUUID();
		SessionFactory.getSession().put(token, user.getId());
		userVO.setToken(token);
		return userVO;
	}

	@ApiOperation("退出登录")
	@PostMapping("/logout")
	public void logout(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
		SessionFactory.getSession().remove(token);
	}

	@ApiOperation("检查Token")
	@GetMapping("/check-token")
	public UserVO checkUserLoginInfo(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
		Long userId = SessionFactory.getSession().get(token);
		if (Objects.isNull(userId)) {
			throw new UnifiedException(HttpStatus.UNAUTHORIZED, "token错误");
		}
		User user = new User().selectById(userId);
		if (Objects.isNull(user)) {
			throw new UnifiedException(HttpStatus.UNAUTHORIZED, "token 异常");
		}
		UserVO userVO = new UserVO();
		BeanUtil.copyProperties(user, userVO);
		return userVO;
	}

}
