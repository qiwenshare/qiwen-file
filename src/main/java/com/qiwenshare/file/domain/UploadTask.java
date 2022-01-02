package com.qiwenshare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "uploadtask")
@Entity
@TableName("uploadtask")
public class UploadTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition = "bigint(20)")
    private Long uploadTaskId;

    @Column(columnDefinition = "bigint(20) comment '用户id'")
    private Long userId;

    @Column(columnDefinition="varchar(32) comment 'md5唯一标识'")
    private String identifier;

    @Column(columnDefinition="varchar(100) comment '文件名称'")
    private String fileName;

    @Column(columnDefinition="varchar(500) comment '文件路径'")
    private String filePath;

    @Column(columnDefinition="varchar(100) comment '扩展名'")
    private String extendName;

    @Column(columnDefinition="varchar(25) comment '上传时间'")
    private String uploadTime;

    @Column(columnDefinition="int(1) comment '上传状态(1-成功,0-失败或未完成)'")
    private Integer uploadStatus;
}
