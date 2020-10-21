package com.qiwenshare.file.constants;


import java.text.MessageFormat;

import com.qiwenshare.common.cbb.RestResult;
/**
 * @author dehui dou
 * @date 2020/8/25
 * @description
 */
public interface CommonConstants {

    /**
     * @author dehui dou
     * @date 2020/8/25
     * @description 返回应答码
     */
    public enum RepCodeEnum {

        SUCCESS("0000", "成功"),
        ERROR("0001", "操作失败"),
        EXCEPTION("9999", "服务器内部异常"),

        BLANK_ERROR("0011", "{0}不能为空"),
        NOT_NULL_ERROR("0012", "{0}必须为空"),
        NOT_EXIST_ERROR("0013", "{0}数据库中不存在"),
        EXIST_ERROR("0014", "{0}数据库中已存在"),
        PARAM_TYPE_ERROR("0015", "{0}类型错误"),
        PARAM_FORMAT_ERROR("0016", "{0}格式错误"),
        ;
        private String code;
        private String desc;

        RepCodeEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public String getName() {
            return this.name();
        }

        /**
         * 将入参fieldNames与this.desc组合成错误信息
         * {fieldName}不能为空
         *
         * @param fieldNames
         * @return
         */
        public RestResult parseError(Object... fieldNames) {
            RestResult errorMessage = new RestResult();
            String newDesc = MessageFormat.format(this.desc, fieldNames);
            errorMessage.setErrorCode(this.code);
            errorMessage.setErrorMessage(newDesc);
            return errorMessage;
        }

    }

}
