package com.qiwenshare.file.dto.recoveryfile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "回收文件DTO",required = true)
public class RestoreFileDTO {
    @Schema(description="删除批次号")
    private String deleteBatchNum;
    @Schema(description="文件路径")
    private String filePath;
}
