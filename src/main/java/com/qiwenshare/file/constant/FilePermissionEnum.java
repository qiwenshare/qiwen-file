package com.qiwenshare.file.constant;

/**
 * @author MAC
 * @version 1.0
 * @description: TODO
 * @date 2022/1/12 15:44
 */
public enum FilePermissionEnum {
    READ(1, "读取"),
    READ_WRITE(2, "读取/写入");

    private int type;
    private String desc;
    FilePermissionEnum(int type, String desc) {
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
