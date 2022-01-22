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
    private Long userFileId;
    private Integer commonFileType;
    private String commonUserIds;
    private Integer permissionCode;
}
