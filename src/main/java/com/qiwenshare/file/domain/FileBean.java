package com.qiwenshare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiwenshare.common.util.DateUtil;
import lombok.Data;

import javax.persistence.*;

/**
 * 文件实体类
 *
 * @author ma116
 */
@Data
@Table(name = "file")
@Entity
@TableName("file")
public class FileBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition="bigint(20)")
    private Long fileId;

    @Column(columnDefinition="varchar(500)")
    private String fileUrl;

    @Column(columnDefinition="bigint(10)")
    private Long fileSize;

    @Column(columnDefinition="int(1)")
    private Integer fileStatus;

    @Column(columnDefinition="int(1)")
    private Integer storageType;

    @Column(columnDefinition="varchar(32)")
    private String identifier;

    @Column(columnDefinition="varchar(25)")
    private String createTime;

    @Column(columnDefinition="bigint(20)")
    private Long createUserId;

    @Column(columnDefinition="varchar(25)")
    private String modifyTime;

    @Column(columnDefinition="bigint(20)")
    private Long modifyUserId;

    public FileBean(){

    }

    public FileBean(String fileUrl, Long fileSize, Integer storageType, String identifier, Long userId) {
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.fileStatus = 1;
        this.storageType = storageType;
        this.identifier = identifier;
        this.createTime = DateUtil.getCurrentTime();
        this.createUserId = userId;

    }

}
