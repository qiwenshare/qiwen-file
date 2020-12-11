package com.qiwenshare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
@Table(name = "file", uniqueConstraints = {
        @UniqueConstraint(name = "fileindex", columnNames = {"fileName", "filePath", "extendName"})})
@Entity
@TableName("file")
public class FileBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition="bigint(20) comment '文件id'")
    private Long fileId;

    @Column(columnDefinition="bigint(20) comment '用户id'")
    private Long userId;

    @Column(columnDefinition="varchar(500) comment '文件url'")
    private String fileUrl;

    @Column(columnDefinition="varchar(500) comment '文件路径'")
    private String filePath;

    @Column(columnDefinition="varchar(25) comment '上传时间'")
    private String uploadTime;

    @Column(columnDefinition="varchar(50) comment '时间戳名称'")
    private String timeStampName;

    @Column(columnDefinition="varchar(10) comment '扩展名'")
    private String extendName;

    @Column(columnDefinition="varchar(100) comment '文件名'")
    private String fileName;

    @Column(columnDefinition="bigint(10) comment '文件大小'")
    private Long fileSize;

    @Column(columnDefinition="int(1) comment '是否是目录 0-否, 1-是'")
    private Integer isDir;

    @Column(columnDefinition="int(1) comment '是否是OSS云存储 0-否, 1-是'")
    private Integer isOSS;

    @Column(columnDefinition="int(11) comment '文件引用数量'")
    private Integer pointCount;

    @Column(columnDefinition="int(11) comment '文件删除标志 0/null-正常, 1-删除'")
    private Integer deleteFlag;

    @Column(columnDefinition="varchar(25) comment '删除时间'")
    private String deleteTime;

    @Column(columnDefinition="varchar(32) comment 'md5标识'")
    private String identifier;

//    @Transient
//    @TableField(exist = false)
//    private String oldFilePath;
//
//    @Transient
//    @TableField(exist = false)
//    private String oldFileName;

//    @Transient
//    @TableField(exist = false)
//    private String files;

//    @Transient
//    @TableField(exist = false)
//    private Integer fileType;

    //切片上传相关参数
//    @Transient
//    @TableField(exist = false)
//    private String taskId;
//    @Transient
//    @TableField(exist = false)
//    private int chunkNumber;
//    @Transient
//    @TableField(exist = false)
//    private long chunkSize;
//    @Transient
//    @TableField(exist = false)
//    private int totalChunks;
//    @Transient
//    @TableField(exist = false)
//    private long totalSize;
//    @Transient
//    @TableField(exist = false)
//    private long currentChunkSize;


}
