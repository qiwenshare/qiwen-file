package com.qiwenshare.file.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiwenshare.file.domain.FileBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileMapper extends BaseMapper<FileBean> {

    void insertFile(FileBean fileBean);
    void batchInsertFile(List<FileBean> fileBeanList);
    void updateFile(FileBean fileBean);
    List<FileBean> selectFileByNameAndPath(FileBean fileBean);
    FileBean selectFileById(FileBean fileBean);
    List<FileBean> selectFilePathTreeByUserId(FileBean fileBean);
    List<FileBean> selectFileList(FileBean fileBean);
    List<FileBean> selectFileTreeListLikeFilePath(FileBean fileBean);
    void deleteFileById(FileBean fileBean);
    void deleteFileByIds(List<Integer> fileIdList);
    List<FileBean> selectFileListByIds(List<Integer> fileIdList);
    void updateFilepathByFilepath(String oldfilePath, String newfilePath);
    void updateFilepathByPathAndName(String oldfilePath, String newfilePath, String fileName, String extendName);
    List<FileBean> selectFileByExtendName(@Param("fileNameList") List<String> fileNameList,
                                          @Param("userId") long userId);
}
