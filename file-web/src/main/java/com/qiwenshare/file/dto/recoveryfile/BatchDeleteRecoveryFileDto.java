package com.qiwenshare.file.dto.recoveryfile;

import io.swagger.v3.oas.annotations.media.Schema;

public class BatchDeleteRecoveryFileDto {
    @Schema(description="恢复文件集合")
    private String recoveryFiles;
}
