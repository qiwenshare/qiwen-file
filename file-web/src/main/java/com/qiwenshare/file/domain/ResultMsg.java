package com.qiwenshare.file.domain;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.qiwenshare.file.constants.CommonConstants.RepCodeEnum;

/**
 * @author dehui dou
 * @date 2020/8/25
 * @description 
 */
public class ResultMsg implements Serializable {

    private static final long serialVersionUID = 8445617032523881407L;

    private String repCode;

    private String repMsg;

    private Object repData;

    public ResultMsg() {
        this.repCode = RepCodeEnum.SUCCESS.getCode();
    }

    public ResultMsg(RepCodeEnum repCodeEnum) {
        this.setRepCodeEnum(repCodeEnum);
    }

    public static ResultMsg success() {
        return ResultMsg.successMsg("成功");
    }

    public static ResultMsg successMsg(String message) {
        ResultMsg ResultMsg = new ResultMsg();
        ResultMsg.setRepMsg(message);
        return ResultMsg;
    }

    public static ResultMsg successData(Object data) {
        ResultMsg ResultMsg = new ResultMsg();
        ResultMsg.setRepCode(RepCodeEnum.SUCCESS.getCode());
        ResultMsg.setRepData(data);
        return ResultMsg;
    }

    public static ResultMsg errorMsg(RepCodeEnum message) {
        ResultMsg ResultMsg = new ResultMsg();
        ResultMsg.setRepCodeEnum(message);
        return ResultMsg;
    }

    public static ResultMsg errorMsg(String message) {
        ResultMsg ResultMsg = new ResultMsg();
        ResultMsg.setRepCode(RepCodeEnum.ERROR.getCode());
        ResultMsg.setRepMsg(message);
        return ResultMsg;
    }

    public static ResultMsg errorMsg(RepCodeEnum repCodeEnum, String message) {
        ResultMsg ResultMsg = new ResultMsg();
        ResultMsg.setRepCode(repCodeEnum.getCode());
        ResultMsg.setRepMsg(message);
        return ResultMsg;
    }

    public static ResultMsg exceptionMsg(String message) {
        ResultMsg ResultMsg = new ResultMsg();
        ResultMsg.setRepCode(RepCodeEnum.EXCEPTION.getCode());
        ResultMsg.setRepMsg(RepCodeEnum.EXCEPTION.getDesc() + ": " + message);
        return ResultMsg;
    }

    public String toJsonString() {
        return JSONObject.toJSONString(this);
    }

    public boolean isError() {
        return !isSuccess();
    }

    public boolean isSuccess() {
        if (this == null) {
            return false;
        }
        return StringUtils.equals(this.repCode, RepCodeEnum.SUCCESS.getCode());
    }

    public String getRepCode() {
        return repCode;
    }

    public void setRepCode(String repCode) {
        this.repCode = repCode;
    }

    public void setRepCodeEnum(RepCodeEnum repCodeEnum) {
        this.repCode = repCodeEnum.getCode();
        this.repMsg = repCodeEnum.getDesc();
    }

    public String getRepMsg() {
        return repMsg;
    }

    public void setRepMsg(String repMsg) {
        this.repMsg = repMsg;
    }

    public Object getRepData() {
        return repData;
    }

    public void setRepData(Object repData) {
        this.repData = repData;
    }


}
