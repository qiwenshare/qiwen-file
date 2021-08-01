package com.qiwenshare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
    private Integer storageType;

    @Column(columnDefinition="int(11)")
    private Integer pointCount;

    @Column(columnDefinition="varchar(32)")
    private String identifier;

}
