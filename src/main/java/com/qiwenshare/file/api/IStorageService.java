package com.qiwenshare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiwenshare.file.domain.StorageBean;

public interface IStorageService extends IService<StorageBean> {
    Long getTotalStorageSize(String userId);
    boolean checkStorage(String userId, Long fileSize);
}
