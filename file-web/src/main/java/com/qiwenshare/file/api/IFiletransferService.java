package com.qiwenshare.file.api;

import com.qiwenshare.file.domain.StorageBean;
import com.qiwenshare.file.dto.UploadFileDTO;

import javax.servlet.http.HttpServletRequest;

public interface IFiletransferService {





    /**
     * 上传文件
     * @param request 请求
     * @param UploadFileDto 文件信息
     */
    void uploadFile(HttpServletRequest request, UploadFileDTO UploadFileDto, Long userId);

    StorageBean selectStorageBean(StorageBean storageBean);

    void insertStorageBean(StorageBean storageBean);

    void updateStorageBean(StorageBean storageBean);

    StorageBean selectStorageByUser(StorageBean storageBean);
    Long selectStorageSizeByUserId(Long userId);
}
