package com.qiwenshare.file.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.regex.Pattern;

@Data
@Schema(name = "批量删除文件DTO",required = true)
public class BatchDeleteFileDTO {
    @Schema(description="文件集合", required = true)
    private String files;

    public static void main(String[] args) {
        boolean ddd = Pattern.matches("(?!((^(con)$)|^(con)/..*|(^(prn)$)|^(prn)/..*|(^(aux)$)|^(aux)/..*|(^(nul)$)|^(nul)/..*|(^(com)[1-9]$)|^(com)[1-9]/..*|(^(lpt)[1-9]$)|^(lpt)[1-9]/..*)|^/s+|.*/s$)(^[^/////:/*/?/\"/</>/|]{1,255}$)", "con1");
        System.out.println(ddd);
    }

}
