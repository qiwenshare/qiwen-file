package com.mac.scp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用户登录VO
 *
 * @author WeiHongBin
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Schema(name = "用户登录VO")
public class UserLoginVO extends UserVO {

	@Schema(description = "Token 接口访问凭证")
	private String token;
}
