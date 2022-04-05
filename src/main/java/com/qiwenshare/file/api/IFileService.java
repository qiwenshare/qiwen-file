package com.qiwenshare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiwenshare.file.domain.FileBean;

public interface IFileService  extends IService<FileBean> {

    Long getFilePointCount(Long fileId);
    void unzipFile(long userFileId, int unzipMode, String filePath);

    public void updateFileDetail(long userFileId, String identifier, long fileSize, long modifyUserId);



}
