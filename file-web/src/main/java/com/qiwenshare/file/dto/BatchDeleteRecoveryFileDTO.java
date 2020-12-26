package com.qiwenshare.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "批量删除回收文件DTO",required = true)
public class BatchDeleteRecoveryFileDTO {
    @Schema(description = "回收用户文件id")
    private Long recoveryFileIds;
    
}
