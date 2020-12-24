package com.qiwenshare.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "移动文件DTO",required = true)
public class MoveFileDTO {

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

    @Schema(description = "旧文件名")
    private String oldFilePath;
    @Schema(description = "扩展名")
    private String extendName;

}
