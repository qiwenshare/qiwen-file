package com.qiwenshare.file.dto.sharefile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "分享类型DTO",required = true)
public class ShareTypeDTO {

    @Schema(description="批次号")
    private String shareBatchNum;


}