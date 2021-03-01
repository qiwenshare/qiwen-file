package com.qiwenshare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiwenshare.file.domain.RecoveryFile;
import com.qiwenshare.file.domain.Share;
import com.qiwenshare.file.domain.ShareFile;

import java.util.List;

public interface IShareService  extends IService<Share> {
    void batchInsertShareFile(List<ShareFile> shareFiles);
}
