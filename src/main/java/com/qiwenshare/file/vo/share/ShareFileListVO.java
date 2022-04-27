package com.qiwenshare.file.vo.share;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description="分享文件列表VO")
@Data
public class ShareFileListVO {
    @Schema(description="文件id")
    private String fileId;
    @Schema(description="文件时间戳姓名")
    private String timeStampName;
    @Schema(description="文件url")
    private String fileUrl;
    @Schema(description="文件大小")
    private Long fileSize;
    @Schema(description="是否sso存储")
    @Deprecated
    private Integer isOSS;
    @Schema(description="存储类型")
    private Integer storageType;
    @Schema(description="用户文件id")
    private String userFileId;
//
//    private Long userId;

    @Schema(description="文件名")
    private String fileName;
    @Schema(description="文件路径")
    private String filePath;
    @Schema(description="文件扩展名")
    private String extendName;
    @Schema(description="是否是目录 0-否， 1-是")
    private Integer isDir;
    @Schema(description="上传时间")
    private String uploadTime;
    @Schema(description="分享文件路径")
    private String shareFilePath;
//
//    private Long shareId;
//
//    private String shareTime;
//    private String endTime;
    private String extractionCode;
    private String shareBatchNum;
//    private Integer shareType;//0公共，1私密，2好友
//    private Integer shareStatus;//0正常，1已失效，2已撤销
}
