package com.qiwenshare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiwenshare.file.domain.RecoveryFile;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.vo.file.RecoveryFileListVo;

import java.util.List;

public interface IRecoveryFileService extends IService<RecoveryFile> {
    void deleteRecoveryFile(UserFile userFile);
    void restorefile(String deleteBatchNum, String filePath, Long sessionUserId);
    List<RecoveryFileListVo> selectRecoveryFileList(Long userId);
}
