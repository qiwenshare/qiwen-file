package com.qiwenshare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.domain.UserFile;

import java.util.List;
import java.util.Map;

public interface IUserFileService extends IService<UserFile> {
    List<UserFile> selectUserFileByNameAndPath(String fileName, String filePath, Long userId);
    void replaceUserFilePath(String filePath, String oldFilePath, Long userId);
    List<Map<String, Object>> userFileList(UserFile userFile, Long beginCount, Long pageCount);
    void updateFilepathByFilepath(String oldfilePath, String newfilePath, String fileName, String extendName);

    List<Map<String, Object>> selectFileByExtendName(List<String> fileNameList, long userId);
    List<Map<String, Object>> selectFileNotInExtendNames(List<String> fileNameList, long userId);
    List<UserFile> selectFileTreeListLikeFilePath(String filePath);
    List<UserFile> selectFilePathTreeByUserId(Long userId);
    void deleteUserFile(UserFile userFile, UserBean sessionUserBean);

}
