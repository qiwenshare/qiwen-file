package com.qiwenshare.file.dto.sharefile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Schema(name = "分享文件DTO",required = true)
public class ShareFileDTO {
    @Schema(description="文件Id集合", required = true)
    @NotEmpty
    private String userFileIds;
    @Schema(description = "过期日期", example="2020-05-23 22:10:33")
    private String endTime;
    @Schema(description = "分享类型", example="0公共分享，1私密分享，2好友分享")
    private Integer shareType;
    @Schema(description = "备注", example="")
    private String remarks;




}