package com.mac.scp.domain;

import javax.persistence.*;

/**
 * 相册实体类
 *
 * @author ma116
 */
@Table(name = "file", uniqueConstraints = {
        @UniqueConstraint(name = "fileindex", columnNames = {"filename", "filepath", "extendname"})})
@Entity
public class FileBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long fileid;

    private long albumid;

    private long essayid;

    private long userid;

    private String fileurl;

    private String filepath;

    private String uploadtime;

    private String timestampname;

    private String extendname;

    private String filename;

    private long filesize;

    private int isdir;

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
