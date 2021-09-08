package com.qiwenshare.file.dto.user;

import com.qiwenshare.common.constant.RegexConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Schema(name = "用户注册DTO",required = true)
public class RegisterDTO {

    @Schema(description = "用户名", required = true, example = "奇文网盘")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 1, max = 20, message = "用户名最少1位，最多20位")
    private String username;

    @Schema(description = "手机号", required = true, example = "13911112222")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = RegexConstant.PHONE_REGEX, message = "手机号码输入有误")
    private String telephone;

    @Schema(description = "密码", required = true, example = "password123")
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = RegexConstant.PASSWORD_REGEX, message = "密码长度6-20位,不允许中文")
    private String password;
}
