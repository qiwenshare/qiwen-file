package com.qiwenshare.file.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "文件列表DTO",required = true)
public class FileListDTO {
    @Schema(description = "文件路径", required = true)
    private String filePath;
    @Schema(description = "当前页码", required = true)
    private Long currentPage;
    @Schema(description = "一页显示数量", required = true)
    private Long pageCount;
}
