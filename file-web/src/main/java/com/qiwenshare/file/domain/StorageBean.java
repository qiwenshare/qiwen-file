package com.qiwenshare.file.domain;

/**
 * 存储信息类
 */
public class StorageBean {
    /**
     * 存储id
     */
    private Long storageId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 存储大小
     */
    private Long storageSize;

    public StorageBean() {

    }

    public StorageBean(long userId) {
        this.userId = userId;
    }

    public Long getStorageId() {
        return storageId;
    }

    public void setStorageId(Long storageId) {
        this.storageId = storageId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStorageSize() {
        return storageSize;
    }

    public void setStorageSize(Long storageSize) {
        this.storageSize = storageSize;
    }

}
