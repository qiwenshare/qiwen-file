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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition="bigint(20) comment '存储id'")
    private Long storageId;

    @Column(columnDefinition="bigint(20) comment '用户id'")
    private Long userId;

    @Column(columnDefinition="bigint(20) comment '存储大小'")
    private Long storageSize;

    public StorageBean() {

    }

    public StorageBean(long userId) {
        this.userId = userId;
    }

}
