package com.qiwenshare.file.vo.share;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description="私密分享VO")
public class ShareSecretVO {
    @Schema(description="批次号")
    private String shareBatchNum;
}
