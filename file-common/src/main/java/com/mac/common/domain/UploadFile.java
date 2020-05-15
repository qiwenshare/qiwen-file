package com.mac.common.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * md-edit插件上传图片返回数据实体类
 *
 * @author ma116
 */
@Data
@Accessors(chain = true)
public class UploadFile {
    private String fileName;
    private String fileType;
    private long fileSize;
    private String timeStampName;
    private int success;
    private String message;
    private String url;

}
