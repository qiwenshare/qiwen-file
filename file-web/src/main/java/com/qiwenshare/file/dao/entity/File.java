package com.qiwenshare.file.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @desc
 * @author dehui dou
 * @time 2020-10-21
 */
@TableName("file")
public class File {

    @TableId("fileId")
    private Long fileId;

    private String extendName;

    private String fileName;

    private String filePath;

    private Long fileSize;

    private String fileUrl;

    private Integer isDir;

    private Integer isOSS;

    private String timeStampName;

    private String uploadTime;

    private Long userId;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getExtendName() {
        return extendName;
    }

    public void setExtendName(String extendName) {
        this.extendName = extendName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Integer getIsDir() {
        return isDir;
    }

    public void setIsDir(Integer isDir) {
        this.isDir = isDir;
    }

    public Integer getIsOSS() {
        return isOSS;
    }

    public void setIsOSS(Integer isOSS) {
        this.isOSS = isOSS;
    }

    public String getTimeStampName() {
        return timeStampName;
    }

    public void setTimeStampName(String timeStampName) {
        this.timeStampName = timeStampName;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public File() {}

}
