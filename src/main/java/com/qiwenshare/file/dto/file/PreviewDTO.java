package com.qiwenshare.file.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "预览文件DTO",required = true)
public class PreviewDTO {
    private Long userFileId;
    private String token;
    private String isMin;
}
