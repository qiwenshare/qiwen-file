package com.qiwenshare.file.vo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@Schema(name = "系统列表Vo")
public class SysParamVO {
    @Schema(description = "系统参数ID")
    private Long sysParamId;
    @Schema(description = "组名")
    private String groupName;
    @Schema(description = "名称")
    private String sysParamKey;
    @Schema(description = "内容")
    private String sysParamValue;
    @Schema(description = "排序")
    private String sysParamDesc;
    @Schema(description = "创建时间")
    private String createTime;
    @Schema(description = "修改时间")
    private String modifyTime;
}
