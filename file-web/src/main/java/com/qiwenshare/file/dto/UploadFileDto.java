package com.qiwenshare.file.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.*;

@Data
@Schema(name = "上传文件DTO",required = true)
public class UploadFileDto {

    private Long fileId;



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
    private String filename;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 是否是目录
     */
    private Integer isDir;

    private Integer isOSS;

    private Integer pointCount;

    private String oldFilePath;

    private String oldFileName;

    private String files;

    private Integer fileType;

    private String taskId;

    private int chunkNumber;

    private long chunkSize;

    private int totalChunks;

    private long totalSize;

    private long currentChunkSize;

    private String identifier;

}
