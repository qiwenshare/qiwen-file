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
    /**
     * 文件id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long fileId;


    /**
     * 用户id
     */
    private Long userId;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 上传时间
     */
    private String uploadTime;

    /**
     * 时间戳名称
     */
    private String timeStampName;

    /**
     * 扩展名
     */
    private String extendName;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 是否是目录
     */
    private Integer isDir;

    private Integer isOSS;

    @Transient
    @TableField(exist = false)
    private String oldFilePath;

    @Transient
    @TableField(exist = false)
    private String oldFileName;

    @Transient
    @TableField(exist = false)
    private String files;

    @Transient
    @TableField(exist = false)
    private Integer fileType;


}
