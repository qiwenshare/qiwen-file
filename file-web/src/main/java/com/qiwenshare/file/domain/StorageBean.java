package com.qiwenshare.file.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * 存储信息类
 */
@Data
@Table(name = "storage")
@Entity
@TableName("storage")
public class StorageBean {
    /**
     * 存储id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

}
