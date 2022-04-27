package com.qiwenshare.file.constant;

public enum CommonFileTypeEnum {
    EVERYONE(0, "所有人"),
    PERSONAL(1, "个人");


    private int type;
    private String desc;
    CommonFileTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
