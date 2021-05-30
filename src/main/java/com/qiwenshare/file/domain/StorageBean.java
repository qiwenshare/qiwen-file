package com.qiwenshare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
    @Column(columnDefinition="bigint(20)")
    @TableId(type = IdType.AUTO)
    private Long storageId;

    @Column(columnDefinition="bigint(20)")
    private Long userId;

    @Column(columnDefinition="bigint(20)")
    private Long storageSize;

    @Column(columnDefinition="bigint(20)")
    private Long totalStorageSize;

    public StorageBean() {

    }

    public StorageBean(long userId) {
        this.userId = userId;
    }

}
