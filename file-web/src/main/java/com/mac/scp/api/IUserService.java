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


}
