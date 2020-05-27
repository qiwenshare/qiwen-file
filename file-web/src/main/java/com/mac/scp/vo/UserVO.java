package com.mac.scp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "用户VO")
public class UserVO {

	@Schema(description = "用户名")
	private String username;

	@Schema(description = "用户昵称，用于替换用户名显示")
	private String nickname;

	@Schema(description = "手机号码")
	private String phone;

	@Schema(description = "邮箱")
	private String email;

	@Schema(description = "性别")
	private String sex;

	@Schema(description = "生日")
	private LocalDate birthday;

	@Schema(description = "用户头像URL")
	private String avatarUrl;
}
