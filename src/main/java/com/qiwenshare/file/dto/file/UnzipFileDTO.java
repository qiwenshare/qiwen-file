package com.qiwenshare.file.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "解压缩文件DTO",required = true)
public class UnzipFileDTO {
    @Schema(description = "文件url", required = true)
    private long userFileId;

    @Schema(description = "文件url", required = true)
    @Deprecated
    private String fileUrl;
    @Schema(description = "文件路径", required = true)
    @Deprecated
    private String filePath;
}
