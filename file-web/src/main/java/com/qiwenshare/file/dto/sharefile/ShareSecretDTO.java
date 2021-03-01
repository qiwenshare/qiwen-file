package com.qiwenshare.file.dto.sharefile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "私密分享DTO",required = true)
public class ShareSecretDTO {
    @Schema(description="文件集合")
    private String files;
    @Schema(description = "过期日期", example="2020-05-23 22:10:33")
    private String endTime;
    @Schema(description = "是否需要提取码", example="1-需要/0-不需要")
    private Integer isNeedExtractionCode;
    @Schema(description = "提取码", example="asFwe3")
    private Integer extractionCode;
    @Schema(description = "备注", example="")
    private String remarks;




}