package com.qiwenshare.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "用户注册DTO",required = true)
public class RegisterDTO {
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "手机号")
    private String telephone;
    @Schema(description = "密码")
    private String password;
}
