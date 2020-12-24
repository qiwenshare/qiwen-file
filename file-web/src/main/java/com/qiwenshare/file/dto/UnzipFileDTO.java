package com.qiwenshare.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "解压缩文件DTO",required = true)
public class UnzipFileDTO {
    @Schema(description = "文件url")
    private String fileUrl;
    @Schema(description = "文件路径")
    private String filePath;
}
