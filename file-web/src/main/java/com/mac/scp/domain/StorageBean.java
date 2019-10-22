package com.mac.scp.domain;

import javax.persistence.*;

@Table(name = "storage")
@Entity
public class StorageBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long storageid;

    private long userid;

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
