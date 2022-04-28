package com.qiwenshare.file.vo.file;

import com.qiwenshare.file.domain.Image;
import com.qiwenshare.file.domain.Music;
import lombok.Data;

/**
 * @author MAC
 * @version 1.0
 * @description: TODO
 * @date 2022/4/28 23:45
 */
@Data
public class FileDetailVO {
    private String fileId;

    private String timeStampName;

    private String fileUrl;

    private Long fileSize;

    private Integer storageType;

    private Integer pointCount;

    private String identifier;

    private String userFileId;

    private Long userId;


    private String fileName;

    private String filePath;

    private String extendName;

    private Integer isDir;

    private String uploadTime;

    private Integer deleteFlag;

    private String deleteTime;

    private String deleteBatchNum;

    private Image image;

    private Music music;
}
