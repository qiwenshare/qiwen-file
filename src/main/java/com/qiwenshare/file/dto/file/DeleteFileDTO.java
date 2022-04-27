package com.qiwenshare.file.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "删除文件DTO",required = true)
public class DeleteFileDTO {
    @Schema(description = "用户文件id", required = true)
    private String userFileId;


}
