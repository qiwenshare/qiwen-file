package com.qiwenshare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.UserBean;

import java.util.List;

public interface IFileService  extends IService<FileBean> {


    void batchInsertFile(List<FileBean> fileBeanList, Long userId);
    //void updateFile(FileBean fileBean);
    List<FileBean> selectFileByNameAndPath(String fileName, String filePath);

    List<FileBean> selectFilePathTreeByUserId(FileBean fileBean);
    List<FileBean> selectFileListByPath(FileBean fileBean);
    void replaceFilePath(String filePath, String oldFilePath);


    List<FileBean> selectFileTreeListLikeFilePath(String filePath);
    void deleteFile(FileBean fileBean, UserBean sessionUserBean);

    void updateFilepathByFilepath(String oldfilePath, String newfilePath, String fileName, String extendName);
    List<FileBean> selectFileByExtendName(List<String> fileNameList, long userId);
    List<FileBean> selectFileNotInExtendNames(List<String> fileNameList, long userId);
}
