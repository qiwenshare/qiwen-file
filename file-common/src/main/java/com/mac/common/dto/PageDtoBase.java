package com.mac.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 分页基类
 *
 * @author admin
 */
@EqualsAndHashCode
@Accessors(chain = true)
@Data
public class PageDtoBase {
    /**
     * 每页行数
     */
    @Schema(description = "每页行数")
    protected Integer size = 10;

    /**
     * 页码
     */
    @Schema(description = "页码")
    protected Integer current = 1;

}
