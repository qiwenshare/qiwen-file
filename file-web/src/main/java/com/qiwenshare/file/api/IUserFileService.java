package com.qiwenshare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.UserFile;

import java.util.List;

public interface IUserFileService extends IService<UserFile> {
    List<UserFile> selectUserFileByNameAndPath(String fileName, String filePath, Long userId);
    void replaceUserFilePath(String filePath, String oldFilePath, Long userId);
}
