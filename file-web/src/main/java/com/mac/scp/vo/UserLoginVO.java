package com.mac.scp.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("用户登录VO")
public class UserLoginVO extends UserVO {

	@ApiModelProperty("Token 接口访问凭证")
	private String token;
}
