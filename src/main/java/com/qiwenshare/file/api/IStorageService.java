package com.qiwenshare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiwenshare.file.domain.StorageBean;

public interface IStorageService extends IService<StorageBean> {
    public Long getTotalStorageSize(Long userId);
    boolean checkStorage(Long userId, Long fileSize);
}
