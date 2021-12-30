package com.qiwenshare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "uploadtaskdetail")
@Entity
@TableName("uploadtaskdetail")
public class UploadTaskDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition="bigint(20)")
    private Long uploadTaskDetailId;

    @Column(columnDefinition="varchar(500) comment '文件路径'")
    private String filePath;

    @Column(columnDefinition="varchar(100) comment '文件名称'")
    private String filename;

    @Column(columnDefinition="int(5) comment '当前分片数'")
    private int chunkNumber;

    @Column(columnDefinition="bigint(10) comment '当前分片大小'")
    private Integer chunkSize;
    @Column(columnDefinition="varchar(500) comment '文件相对路径'")
    private String relativePath;

    @Column(columnDefinition="int(5) comment '文件总分片数'")
    private Integer totalChunks;
    @Column(columnDefinition="bigint(10) comment '文件总大小'")
    private Integer totalSize;

    @Column(columnDefinition="varchar(32) comment '文件md5唯一标识'")
    private String identifier;
}
