package com.qiwenshare.file.domain;

/**
 * 文件实体类
 *
 * @author ma116
 */

public class FileBean {
    /**
     * 文件id
     */
    private Long fileId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 上传时间
     */
    private String uploadTime;

    /**
     * 时间戳名称
     */
    private String timeStampName;

    /**
     * 扩展名
     */
    private String extendName;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 是否是目录
     */
    private Integer isDir;

    private Integer isOSS;

    private String oldFilePath;

    private String oldFileName;

    private String files;

    private Integer fileType;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getTimeStampName() {
        return timeStampName;
    }

    public void setTimeStampName(String timeStampName) {
        this.timeStampName = timeStampName;
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

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
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

    public String getOldFilePath() {
        return oldFilePath;
    }

    public void setOldFilePath(String oldFilePath) {
        this.oldFilePath = oldFilePath;
    }

    public String getOldFileName() {
        return oldFileName;
    }

    public void setOldFileName(String oldFileName) {
        this.oldFileName = oldFileName;
    }

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }

    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }
}
