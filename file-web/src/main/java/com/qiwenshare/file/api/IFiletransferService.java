package com.qiwenshare.file.api;

import com.qiwenshare.common.cbb.RestResult;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.StorageBean;
import com.qiwenshare.file.domain.UserBean;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

public interface IFiletransferService {





    /**
     * 上传文件
     * @param request 请求
     * @param fileBean 文件信息
     */
    void uploadFile(HttpServletRequest request, FileBean fileBean, UserBean sessionUserBean);

    StorageBean selectStorageBean(StorageBean storageBean);

    void insertStorageBean(StorageBean storageBean);

    void updateStorageBean(StorageBean storageBean);

    StorageBean selectStorageByUser(StorageBean storageBean);
}
