package com.qiwenshare.file.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "删除回收文件DTO",required = true)
public class DeleteRecoveryFileDTO {
    @Schema(description = "回收文件id")
    private Long recoveryFileId;

}
