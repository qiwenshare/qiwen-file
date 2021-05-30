package com.qiwenshare.file.vo.file;

import lombok.Data;

@Data
public class FileListVo {
    private Long fileId;

    private String timeStampName;

    private String fileUrl;

    private Long fileSize;

    @Deprecated
    private Integer isOSS;

    private Integer storageType;

    private Integer pointCount;

    private String identifier;

    private Long userFileId;

    private Long userId;


    private String fileName;

    private String filePath;

    private String extendName;

    private Integer isDir;

    private String uploadTime;

    private Integer deleteFlag;

    private String deleteTime;

    private String deleteBatchNum;
}
