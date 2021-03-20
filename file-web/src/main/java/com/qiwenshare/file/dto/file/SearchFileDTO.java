package com.qiwenshare.file.dto.file;

import lombok.Data;

@Data
public class SearchFileDTO {
    private String fileName;
    private long currentPage;
    private long pageCount;
    private String order;
    private String direction;
}
