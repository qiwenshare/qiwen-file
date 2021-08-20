package com.qiwenshare.file.dto.recoveryfile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "批量删除回收文件DTO",required = true)
public class BatchDeleteRecoveryFileDTO {
    @Schema(description="恢复文件集合")
    private String recoveryFileIds;
}
