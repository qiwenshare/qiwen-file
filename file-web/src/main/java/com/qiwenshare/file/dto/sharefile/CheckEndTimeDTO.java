package com.qiwenshare.file.dto.sharefile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "校验过期时间DTO",required = true)
public class CheckEndTimeDTO {
    @Schema(description="批次号")
    private String shareBatchNum;

}