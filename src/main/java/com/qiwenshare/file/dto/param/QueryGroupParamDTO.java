package com.qiwenshare.file.dto.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "获取组参数列表DTO")
public class QueryGroupParamDTO {

    @Schema(description = "组名")
    private String groupName;

}
