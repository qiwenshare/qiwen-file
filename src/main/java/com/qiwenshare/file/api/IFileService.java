package com.qiwenshare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.domain.UserFile;

import java.util.List;

public interface IFileService  extends IService<FileBean> {

    void increaseFilePointCount(Long fileId);

    void decreaseFilePointCount(Long fileId);
    void unzipFile(long userFileId, int unzipMode, String filePath);





}
