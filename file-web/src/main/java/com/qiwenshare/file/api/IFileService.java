package com.qiwenshare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.domain.UserFile;

import java.util.List;

public interface IFileService  extends IService<FileBean> {


//    void batchInsertFile(List<FileBean> fileBeanList, Long userId);
    //void updateFile(FileBean fileBean);

    void increaseFilePointCount(Long fileId);

    void decreaseFilePointCount(Long fileId);

//    List<FileBean> selectFileListByPath(FileBean fileBean);

    void deleteLocalFile(FileBean fileBean);





}
