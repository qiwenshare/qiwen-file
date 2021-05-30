package com.qiwenshare.file.vo.share;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.Column;

@Schema(description="分享列表VO")
@Data
public class ShareListVO {
    private Long shareId;
    private Long userId;
    private String shareTime;
    private String endTime;
    private String extractionCode;
    private String shareBatchNum;
    private Integer shareType;//0公共，1私密，2好友
    private Integer shareStatus;//0正常，1已失效，2已撤销
    private Long shareFileId;
    private Long userFileId;
    private String shareFilePath;
    private Long fileId;
    private String fileName;
    private String filePath;
    private String extendName;
    private Integer isDir;
    private String uploadTime;
    private Integer deleteFlag;
    private String deleteTime;
    private String deleteBatchNum;
    private String timeStampName;
    private String fileUrl;
    private Long fileSize;
    private Integer storageType;
}
