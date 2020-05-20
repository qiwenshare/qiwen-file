package com.mac.scp.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 用户VO
 *
 * @author WeiHongBin
 */
@Data
@Accessors(chain = true)
@ApiModel("用户VO")
public class UserVO {

	@ApiModelProperty(value = "用户名")
	private String username;

	@ApiModelProperty("用户昵称，用于替换用户名显示")
	private String nickname;

	@ApiModelProperty("手机号码")
	private String phone;

	@ApiModelProperty("邮箱")
	private String email;

	@ApiModelProperty("性别")
	private String sex;

	@ApiModelProperty("生日")
	private LocalDate birthday;

	@ApiModelProperty("用户头像URL")
	private String avatarUrl;
}
