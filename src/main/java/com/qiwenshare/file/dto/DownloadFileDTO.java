package com.qiwenshare.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "下载文件DTO",required = true)
public class DownloadFileDTO {
    private Long userFileId;
    private boolean isMin;
}
