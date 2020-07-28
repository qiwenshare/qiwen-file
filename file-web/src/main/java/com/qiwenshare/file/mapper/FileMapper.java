package com.qiwenshare.file.mapper;


import com.qiwenshare.file.domain.FileBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileMapper {

    void insertFile(FileBean fileBean);
    void batchInsertFile(List<FileBean> fileBeanList);
    void updateFile(FileBean fileBean);
    FileBean selectFileById(FileBean fileBean);
    List<FileBean> selectFilePathTreeByUserid(FileBean fileBean);
    List<FileBean> selectFileList(FileBean fileBean);
    List<FileBean> selectFileTreeListLikeFilePath(FileBean fileBean);
    void deleteFileById(FileBean fileBean);
    void deleteFileByIds(List<Integer> fileidList);
    List<FileBean> selectFileListByIds(List<Integer> fileidList);
    void updateFilepathByFilepath(String oldfilepath, String newfilepath);
    void updateFilepathByPathAndName(String oldfilepath, String newfilepath, String filename, String extendname);
    List<FileBean> selectFileByExtendName(@Param("filenameList") List<String> filenameList,
                                          @Param("userid") long userid);
}
