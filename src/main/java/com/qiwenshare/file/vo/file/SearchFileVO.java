package com.qiwenshare.file.vo.file;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author MAC
 * @version 1.0
 * @description: TODO
 * @date 2022/4/10 11:04
 */
@Data
public class SearchFileVO {
    private String userFileId;
    private String fileName;
    private String filePath;
    private String extendName;
    private Long fileSize;
    private String fileUrl;
    private Map<String, List<String>> highLight;
    private Integer isDir;
}
