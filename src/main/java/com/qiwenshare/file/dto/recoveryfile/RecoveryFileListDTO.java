package com.qiwenshare.file.dto.recoveryfile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "回收文件列表DTO",required = true)
public class RecoveryFileListDTO {
    @Schema(description = "当前页码")
    private Long currentPage;
    @Schema(description = "一页显示数量")
    private Long pageCount;
}
