package com.qiwenshare.file.dto.sharefile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "分享文件列表DTO",required = true)
public class ShareFileListDTO {

    @Schema(description="批次号")
    private String shareBatchNum;
    @Schema(description="分享文件路径")
    private String shareFilePath;



}