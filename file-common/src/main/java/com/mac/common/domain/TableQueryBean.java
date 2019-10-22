package com.mac.common.domain;

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getBeginCount() {
        return beginCount;
    }

    public void setBeginCount(int beginCount) {
        this.beginCount = beginCount;
    }

}
