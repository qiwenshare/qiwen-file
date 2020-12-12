package com.qiwenshare.file.dto;

import lombok.Data;

@Data
public class DeleteFileDto {
    private Long userFileId;
    private String filePath;
    private String fileName;
    private Integer isDir;
}
