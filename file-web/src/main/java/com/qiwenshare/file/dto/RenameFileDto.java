package com.qiwenshare.file.dto;

import lombok.Data;

@Data
public class RenameFileDto {
    private Long userFileId;
    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件名
     */
    private String fileName;

    private Integer isDir;

    private String oldFileName;
    private Integer isOSS;
}
