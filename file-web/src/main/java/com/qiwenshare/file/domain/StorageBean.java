package com.qiwenshare.file.domain;

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
    private long storageId;

    /**
     * 用户id
     */
    private long userId;

    /**
     * 存储大小
     */
    private long storageSize;

    public StorageBean() {

    }

    public StorageBean(long userId) {
        this.userId = userId;
    }

    public long getStorageId() {
        return storageId;
    }

    public void setStorageId(long storageId) {
        this.storageId = storageId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getStorageSize() {
        return storageSize;
    }

    public void setStorageSize(long storageSize) {
        this.storageSize = storageSize;
    }
}
