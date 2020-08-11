package com.qiwenshare.file.service;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.qiwenshare.common.cbb.DateUtil;
import com.qiwenshare.common.cbb.RestResult;
import com.qiwenshare.common.cbb.Uploader;
import com.qiwenshare.common.domain.UploadFile;
import com.qiwenshare.file.api.IFiletransferService;

import com.qiwenshare.file.mapper.FileMapper;
import com.qiwenshare.file.mapper.FiletransferMapper;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.StorageBean;
import com.qiwenshare.file.domain.UserBean;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;


@Service
public class FiletransferService implements IFiletransferService {

    @Resource
    FiletransferMapper filetransferMapper;
    @Resource
    FileMapper fileMapper;




    @Override
    public void deleteUserImageByIds(List<Integer> imageidList) {
        filetransferMapper.deleteUserImageByIds(imageidList);
    }




//    public void insertFile(FileBean fileBean){
//        filetransferMapper.insertFile(fileBean);
//    }

    @Override
    public void uploadFile(HttpServletRequest request, FileBean fileBean) {
        Uploader uploader = new Uploader(request);
        List<UploadFile> uploadFileList = uploader.upload();
        for (int i = 0; i < uploadFileList.size(); i++){
            UploadFile uploadFile = uploadFileList.get(i);
            if (uploadFile.getSuccess() == 1){
                fileBean.setFileUrl(uploadFile.getUrl());
                fileBean.setFileSize(uploadFile.getFileSize());
                fileBean.setFileName(uploadFile.getFileName());
                fileBean.setExtendName(uploadFile.getFileType());
                fileBean.setTimeStampName(uploadFile.getTimeStampName());
                fileBean.setUploadTime(DateUtil.getCurrentTime());

                fileMapper.insertFile(fileBean);
            }


            synchronized (FiletransferService.class) {
                UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();

                long sessionUserId = sessionUserBean.getUserId();
                StorageBean storageBean = selectStorageBean(new StorageBean(sessionUserId));
                if (storageBean == null) {
                    StorageBean storage = new StorageBean(sessionUserId);
                    storage.setStorageSize(fileBean.getFileSize());
                    insertStorageBean(storage);
                } else {
                    storageBean.setStorageSize(storageBean.getStorageSize() + uploadFile.getFileSize());
                    updateStorageBean(storageBean);
                }
            }

        }
    }

    @Override
    public StorageBean selectStorageBean(StorageBean storageBean) {
        return filetransferMapper.selectStorageBean(storageBean);
    }

    @Override
    public void insertStorageBean(StorageBean storageBean) {
        filetransferMapper.insertStorageBean(storageBean);
    }

    @Override
    public void updateStorageBean(StorageBean storageBean) {
        filetransferMapper.updateStorageBean(storageBean);
    }

    @Override
    public StorageBean selectStorageByUser(StorageBean storageBean) {
        return filetransferMapper.selectStorageByUser(storageBean);
    }
}
