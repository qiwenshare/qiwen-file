package com.qiwenshare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.UserBean;

import java.util.List;

public interface IFileService  extends IService<FileBean> {

    void insertFile(FileBean fileBean);
    void batchInsertFile(List<FileBean> fileBeanList);
    void updateFile(FileBean fileBean);
    List<FileBean> selectFileByNameAndPath(FileBean fileBean);
    FileBean selectFileById(FileBean fileBean);
    List<FileBean> selectFilePathTreeByUserId(FileBean fileBean);
    List<FileBean> selectFileList(FileBean fileBean);
    List<FileBean> selectFileListByIds(List<Integer> fileIdList);

    List<FileBean> selectFileTreeListLikeFilePath(String filePath);
    void deleteFile(FileBean fileBean, UserBean sessionUserBean);
    void deleteFileByIds(List<Integer> fileIdList);
    void updateFilepathByFilepath(String oldfilePath, String newfilePath, String fileName, String extendName);
    List<FileBean> selectFileByExtendName(List<String> fileNameList, long userId);
}
