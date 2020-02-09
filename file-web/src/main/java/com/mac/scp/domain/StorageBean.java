package com.mac.scp.domain;

import javax.persistence.*;

/**
 * 存储信息类
 */
@Table(name = "storage")
@Entity
public class StorageBean {
    /**
     * 存储id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long storageid;

    /**
     * 用户id
     */
    private long userid;

    /**
     * 存储大小
     */
    private long storagesize;

    public StorageBean() {

    }

    public StorageBean(long userid) {
        this.userid = userid;
    }

    public long getStorageid() {
        return storageid;
    }

    public void setStorageid(long storageid) {
        this.storageid = storageid;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public long getStoragesize() {
        return storagesize;
    }

    public void setStoragesize(long storagesize) {
        this.storagesize = storagesize;
    }
}
