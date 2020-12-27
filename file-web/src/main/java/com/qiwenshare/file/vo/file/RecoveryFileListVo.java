package com.qiwenshare.file.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "回收文件列表Vo",required = true)
public class RecoveryFileListVo {
    @Schema(description = "回收文件id", example = "1")
    private Long recoveryFileId;
    @Schema(description = "id", example = "1")
    private Long userFileId;
    @Schema(description = "userId", example = "1")
    private Long userId;
    @Schema(description = "fileId", example = "1")
    private Long fileId;
    @Schema(description = "文件名", example = "图片")
    private String fileName;
    @Schema(description = "文件路径", example = "upload/bddd/caaa")
    private String filePath;
    @Schema(description = "文件扩展名", example = "zip")
    private String extendName;
    @Schema(description = "是否是目录，1-是，0-否", example = "1")
    private Integer isDir;
    @Schema(description = "上传时间", example = "2020-10-10 12:21:22")
    private String uploadTime;
    @Schema(description = "删除标志", example = "1")
    private Integer deleteFlag;
    @Schema(description = "删除时间", example = "2020-10-10 12:21:22")
    private String deleteTime;
    @Schema(description = "删除批次号", example = "1111-222-22")
    private String deleteBatchNum;
}
