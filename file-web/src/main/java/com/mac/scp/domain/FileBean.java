package com.mac.scp.domain;

import javax.persistence.*;

/**
 * 文件实体类
 *
 * @author ma116
 */
@Table(name = "file", uniqueConstraints = {
        @UniqueConstraint(name = "fileindex", columnNames = {"filename", "filepath", "extendname"})})
@Entity
public class FileBean {
    /**
     * 文件id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long fileid;

    /**
     * 相册id
     */
    private long albumid;

    /**
     * 文章id
     */
    private long essayid;

    /**
     * 用户id
     */
    private long userid;

    /**
     * 文件URL
     */
    private String fileurl;

    /**
     * 文件路径
     */
    private String filepath;

    /**
     * 上传时间
     */
    private String uploadtime;

    /**
     * 时间戳名称
     */
    private String timestampname;

    /**
     * 扩展名
     */
    private String extendname;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件大小
     */
    private long filesize;

    /**
     * 是否是目录
     */
    private int isdir;

    @Transient
    private String oldfilepath;
    @Transient
    private String newfilepath;
    @Transient
    private String files;

    public String getOldfilepath() {
        return oldfilepath;
    }

    public void setOldfilepath(String oldfilepath) {
        this.oldfilepath = oldfilepath;
    }

    public String getNewfilepath() {
        return newfilepath;
    }

    public void setNewfilepath(String newfilepath) {
        this.newfilepath = newfilepath;
    }

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }

    public long getAlbumid() {
        return albumid;
    }

    public void setAlbumid(long albumid) {
        this.albumid = albumid;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public int getIsdir() {
        return isdir;
    }

    public void setIsdir(int isdir) {
        this.isdir = isdir;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public long getEssayid() {
        return essayid;
    }

    public void setEssayid(long essayid) {
        this.essayid = essayid;
    }

    public long getFileid() {
        return fileid;
    }

    public void setFileid(long fileid) {
        this.fileid = fileid;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public String getUploadtime() {
        return uploadtime;
    }

    public void setUploadtime(String uploadtime) {
        this.uploadtime = uploadtime;
    }

    public String getTimestampname() {
        return timestampname;
    }

    public void setTimestampname(String timestampname) {
        this.timestampname = timestampname;
    }

    public String getExtendname() {
        return extendname;
    }

    public void setExtendname(String extendname) {
        this.extendname = extendname;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }
}
