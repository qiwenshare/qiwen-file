package com.mac.scp.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 用户头像实体类
 *
 * @author ma116
 */
@Table(name = "userimage")
@Entity
public class UserImageBean implements Serializable {
    /**
     * 序列id
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long imageid;
    @Column
    private long userid;
    @Column
    private String imageurl;
    @Column
    private String uploadtime;


    public String getUploadtime() {
        return uploadtime;
    }

    public void setUploadtime(String uploadtime) {
        this.uploadtime = uploadtime;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getImageid() {
        return imageid;
    }

    public void setImageid(long imageid) {
        this.imageid = imageid;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }


}
