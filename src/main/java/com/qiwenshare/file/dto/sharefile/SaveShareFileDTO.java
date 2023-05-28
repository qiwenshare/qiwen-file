package com.qiwenshare.file.dto.sharefile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "保存分享文件DTO",required = true)
public class SaveShareFileDTO {
    @Schema(description="用户文件id集合", required = true)
    private String userFileIds;
    @Schema(description = "文件路径", required = true)
    private String filePath;
    @Schema(description = "分享批次号", required = true)
    private String shareBatchNum;
}
