package com.qiwenshare.file.dto;

import lombok.Data;

@Data
public class FileListDto {
    private String filePath;

    private Long currentPage;
    private Long pageCount;
}
