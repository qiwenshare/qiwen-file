package com.qiwenshare.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "复制文件DTO",required = true)
public class CopyFileDTO {
    @Schema(description = "用户文件id", required = true)
    private long userFileId;

    @Schema(description = "文件路径", required = true)
    private String filePath;

    @Schema(description = "文件名", required = true)
    @Deprecated
    private String fileName;

    @Schema(description = "旧文件名", required = true)
    @Deprecated
    private String oldFilePath;
    @Schema(description = "扩展名", required = true)
    @Deprecated
    private String extendName;

}
