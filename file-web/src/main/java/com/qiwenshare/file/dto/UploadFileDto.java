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

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 上传时间
     */
    private String uploadTime;

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

    private int chunkNumber;

    private long chunkSize;

    private int totalChunks;

    private long totalSize;

    private long currentChunkSize;

    private String identifier;

}
