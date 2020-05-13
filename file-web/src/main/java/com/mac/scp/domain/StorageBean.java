package com.mac.scp.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * 存储信息类
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
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

    public StorageBean(long userid) {
        this.userid = userid;
    }
}
