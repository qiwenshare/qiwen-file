package com.qiwenshare.file.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "复制文件DTO",required = true)
public class CopyFileDTO {
    @Schema(description = "用户文件id", required = true)
    private long userFileId;

    @Schema(description = "文件路径", required = true)
    private String filePath;

}
