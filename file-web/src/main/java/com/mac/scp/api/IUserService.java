package com.mac.scp.api;

import com.mac.common.cbb.RestResult;
import com.mac.scp.domain.UserBean;

public interface IUserService {

	/**
	 * 用户注册
	 *
	 * @param userBean 用户信息
	 * @return 结果
	 */
	RestResult<String> registerUser(UserBean userBean);


	/**
	 * 获取用户信息
	 *
	 * @param userId 用户id
	 * @return 用户信息
	 */
	UserBean getUserInfoById(long userId);

	/**
	 * 通過openid得到用戶信息
	 *
	 * @param openid
	 * @return
	 */
	UserBean selectUserByopenid(String openid);

	UserBean findUserInfoByTelephone(String telephone);
}
