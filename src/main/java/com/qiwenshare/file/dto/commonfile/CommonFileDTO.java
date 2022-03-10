package com.qiwenshare.file.dto.commonfile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author MAC
 * @version 1.0
 * @description: TODO
 * @date 2022/1/12 15:03
 */
@Data
@Schema(name = "共享文件DTO",required = true)
public class CommonFileDTO {
    @Schema(name = "用户文件id")
    private Long userFileId;
    @Schema(name = "共享文件类型")
    private Integer commonFileType;
    @Schema(name = "共享用户id集合")
    private String commonUserIds;
    @Schema(name = "权限码")
    private Integer permissionCode;
}
