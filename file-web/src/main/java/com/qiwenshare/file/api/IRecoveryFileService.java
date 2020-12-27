package com.qiwenshare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiwenshare.file.domain.RecoveryFile;
import com.qiwenshare.file.domain.UserFile;

public interface IRecoveryFileService extends IService<RecoveryFile> {
    public void deleteRecoveryFile(UserFile userFile);
}
