package com.qiwenshare.file.dto.sharefile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "保存分享文件DTO",required = true)
public class SaveShareFileDTO {
    @Schema(description="文件集合", example = "[{\"userFileId\":12},{\"userFileId\":13}]")
    private String files;
    @Schema(description = "文件路径")
    private String filePath;
}
