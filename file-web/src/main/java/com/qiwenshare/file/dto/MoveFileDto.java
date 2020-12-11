package com.qiwenshare.file.dto;

import lombok.Data;

@Data
public class MoveFileDto {

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件名
     */
    private String fileName;
    private String oldFilePath;
    private String extendName;

}
