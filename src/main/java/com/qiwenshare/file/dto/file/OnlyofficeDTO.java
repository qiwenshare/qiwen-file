package com.qiwenshare.file.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OnlyofficeDTO {
    private String fileId;

    private String fileName;

    private String fileUrl;

    private String extendName;
}
