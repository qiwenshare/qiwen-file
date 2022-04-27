package com.qiwenshare.file.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "解压缩文件DTO",required = true)
public class UnzipFileDTO {
    @Schema(description = "文件url", required = true)
    private String userFileId;

    @Schema(description = "解压模式 0-解压到当前文件夹， 1-自动创建该文件名目录，并解压到目录里， 2-手动选择解压目录", required = true)
    private int unzipMode;

    @Schema(description = "解压目的文件目录，仅当 unzipMode 为 2 时必传")
    private String filePath;
}
