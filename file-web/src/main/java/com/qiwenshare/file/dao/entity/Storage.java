package com.qiwenshare.file.dao.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @desc
 * @author dehui dou
 * @time 2020-10-21
 */
@TableName("storage")
public class Storage {

    @TableId("storageId")
    private Long storageId;

    private Long storageSize;

    private Long userId;

    public Storage() {}

    public Long getStorageId() {
        return storageId;
    }

    public void setStorageId(Long storageId) {
        this.storageId = storageId;
    }

    public Long getStorageSize() {
        return storageSize;
    }

    public void setStorageSize(Long storageSize) {
        this.storageSize = storageSize;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
