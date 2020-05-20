package com.mac.scp.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 用户注册DTO
 *
 * @author WeiHongBin
 */
@Data
@Accessors(chain = true)
@ApiModel("用户注册DTO")
public class UserRegisterDTO {

	@ApiModelProperty(value = "用户名")
	@NotBlank
	@Size(min = 6, max = 20, message = "用户名最少6位，最多20位")
	private String username;

	@ApiModelProperty(value = "密码")
	@NotBlank
	@Size(min = 6, max = 20, message = "密码最少6位，最多20位")
	private String password;

	@ApiModelProperty(value = "手机号码")
	@NotBlank
	@Pattern(regexp = "^1[3456789]\\d{9}$", message = "手机号码输入有误")
	private String phone;
}
