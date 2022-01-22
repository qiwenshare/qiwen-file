package com.qiwenshare.file.vo.commonfile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author MAC
 * @version 1.0
 * @description: TODO
 * @date 2022/1/12 17:34
 */
@Data
public class CommonFileUser {
    @Schema(description = "用户id", example = "1")
    private long userId;
    @Schema(description = "用户名", example = "奇文网盘")
    private String username;
}
