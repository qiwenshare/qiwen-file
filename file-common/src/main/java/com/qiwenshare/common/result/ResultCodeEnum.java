package com.qiwenshare.common.result;

import lombok.Getter;

/**
 * 结果类枚举
 */
@Getter
public enum ResultCodeEnum {
    SUCCESS(true,20000,"成功"),
    UNKNOWN_ERROR(false,20001,"未知错误"),
    PARAM_ERROR(false,20002,"参数错误"),
    NULL_POINT(false, 20003, "空指针异常"),
    INDEX_OUT_OF_BOUNDS(false, 20004, "下标越界异常"),
    REQUEST_TIMEOUT(false, 20005, "请求超时"),
    NOT_LOGIN_ERROR(false, 20006, "未登录异常"),
    ;

    // 响应是否成功
    private Boolean success;
    // 响应状态码
    private Integer code;
    // 响应信息
    private String message;

    ResultCodeEnum(boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }
}