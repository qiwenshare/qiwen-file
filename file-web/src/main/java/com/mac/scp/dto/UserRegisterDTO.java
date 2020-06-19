package com.mac.scp.dto;

import com.mac.scp.constant.RegexConstant;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "用户注册DTO")
public class UserRegisterDTO {

	@Schema(description = "用户名")
	@NotBlank
	@Size(min = 6, max = 20, message = "用户名最少6位，最多20位")
	private String username;

	@Schema(description = "密码")
	@NotBlank
	@Pattern(regexp = RegexConstant.PASSWORD_REGEX, message = "密码长度6-20位,不允许中文")
	private String password;

	@Schema(description = "手机号码")
	@NotBlank
	@Pattern(regexp = RegexConstant.PHONE_REGEX, message = "手机号码输入有误")
	private String phone;
}
