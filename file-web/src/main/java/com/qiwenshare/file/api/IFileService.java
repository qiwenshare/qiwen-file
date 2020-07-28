package com.qiwenshare.file.api;

import com.qiwenshare.file.domain.FileBean;

import java.util.List;

public interface IFileService {

    void insertFile(FileBean fileBean);
    void batchInsertFile(List<FileBean> fileBeanList);
    void updateFile(FileBean fileBean);
    FileBean selectFileById(FileBean fileBean);
    List<FileBean> selectFilePathTreeByUserid(FileBean fileBean);
    List<FileBean> selectFileList(FileBean fileBean);
    List<FileBean> selectFileListByIds(List<Integer> fileidList);

    List<FileBean> selectFileTreeListLikeFilePath(String filePath);
    void deleteFile(FileBean fileBean);
    void deleteFileByIds(List<Integer> fileidList);
    void updateFilepathByFilepath(String oldfilepath, String newfilepath, String filename, String extendname);
    List<FileBean> selectFileByExtendName(List<String> filenameList, long userid);
}
