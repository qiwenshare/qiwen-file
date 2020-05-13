package com.mac.common.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TableQueryBean {
    //key, pageIndex, pageSize, sortField, sortOrder

    private int page;
    private int limit;
    private int beginCount;

    /**
     * 搜索关键词
     */
    private String key;

    /**
     * 排序字段
     */
    private String field;
    /**
     * 排序规则
     */
    private String order;

}
