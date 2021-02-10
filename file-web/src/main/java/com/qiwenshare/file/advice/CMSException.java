package com.qiwenshare.file.advice;

import com.qiwenshare.common.cbb.ResultCodeEnum;
import lombok.Data;

/**
 * 自定义全局异常类
 */
@Data
public class CMSException extends RuntimeException {
    private Integer code;

    public CMSException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public CMSException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "CMSException{" + "code=" + code + ", message=" + this.getMessage() + '}';
    }
}