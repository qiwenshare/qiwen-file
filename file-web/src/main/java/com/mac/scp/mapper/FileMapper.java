package com.mac.scp.mapper;

import com.mac.scp.domain.*;
import org.apache.ibatis.annotations.Param;

import java.io.File;
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
