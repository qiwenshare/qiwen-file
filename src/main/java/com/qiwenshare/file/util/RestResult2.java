package com.qiwenshare.file.util;

import com.qiwenshare.common.result.ResultCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class RestResult2<T> {
    @Schema(
            description = "请求是否成功",
            example = "true"
    )
    private Boolean success = true;
    @Schema(
            description = "返回码",
            example = "000000"
    )
    private Integer code = 0;
    @Schema(
            description = "返回信息",
            example = "成功"
    )
    private String message;
    @Schema(
            description = "返回数据"
    )
    private T data;
    @Schema(
            description = "返回数据列表"
    )
    private List<T> dataList;

    private long total;
    public static RestResult2 success() {
        RestResult2 r = new RestResult2();
        r.setSuccess(ResultCodeEnum.SUCCESS.getSuccess());
        r.setCode(ResultCodeEnum.SUCCESS.getCode());
        r.setMessage(ResultCodeEnum.SUCCESS.getMessage());
        return r;
    }

    public static RestResult2 fail() {
        RestResult2 r = new RestResult2();
        r.setSuccess(ResultCodeEnum.UNKNOWN_ERROR.getSuccess());
        r.setCode(ResultCodeEnum.UNKNOWN_ERROR.getCode());
        r.setMessage(ResultCodeEnum.UNKNOWN_ERROR.getMessage());
        return r;
    }

    public static RestResult2 setResult(ResultCodeEnum result) {
        RestResult2 r = new RestResult2();
        r.setSuccess(result.getSuccess());
        r.setCode(result.getCode());
        r.setMessage(result.getMessage());
        return r;
    }

    public RestResult2 data(T param) {
        this.setData(param);
        return this;
    }
    public RestResult2 dataList(List<T> param, long total) {
        this.setDataList(param);
        this.setTotal(total);
        return this;
    }
    public RestResult2 message(String message) {
        this.setMessage(message);
        return this;
    }

    public RestResult2 code(Integer code) {
        this.setCode(code);
        return this;
    }

    public RestResult2 success(Boolean success) {
        this.setSuccess(success);
        return this;
    }

}
