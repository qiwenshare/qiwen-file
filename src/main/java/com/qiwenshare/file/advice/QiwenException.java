package com.qiwenshare.file.advice;

import com.qiwenshare.common.result.ResultCodeEnum;
import lombok.Data;

/**
 * 自定义全局异常类
 */
@Data
public class QiwenException extends RuntimeException {
    private Integer code;

    public QiwenException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public QiwenException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "QiwenException{" + "code=" + code + ", message=" + this.getMessage() + '}';
    }
}