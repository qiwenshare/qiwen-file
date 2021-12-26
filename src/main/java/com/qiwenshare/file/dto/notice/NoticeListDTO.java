package com.qiwenshare.file.dto.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "公告列表DTO")
public class NoticeListDTO {
    @Schema(description = "当前页，从1开始", required = true, example = "1")
    private int page;
    @Schema(description = "页大小", required = true, example = "10")
    private int pageSize;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "平台")
    private Integer platform;

    @Schema(description = "发布者")
    private Long publisher;

    @Schema(description = "开始发布时间")
    private String beginTime;
    @Schema(description = "开始发布时间")
    private String endTime;

}