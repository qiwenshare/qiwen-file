package com.qiwenshare.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "重命名文件DTO",required = true)
public class RenameFileDTO {
    private Long userFileId;
    /**
     * 文件路径
     */
    @Schema(description = "文件路径")
    private String filePath;

    /**
     * 文件名
     */
    @Schema(description = "文件名")
    private String fileName;
    @Schema(description = "是否是目录")
    private Integer isDir;
    @Schema(description = "旧文件名")
    private String oldFileName;
    @Schema(description = "是否是OSS")
    private Integer isOSS;
}
