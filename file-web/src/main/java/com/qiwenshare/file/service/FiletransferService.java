package com.qiwenshare.file.service;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.qiwenshare.common.cbb.DateUtil;
import com.qiwenshare.common.domain.UploadFile;
import com.qiwenshare.common.upload.factory.AliyunOSSUploaderFactory;
import com.qiwenshare.common.upload.factory.ChunkUploaderFactory;
import com.qiwenshare.common.upload.Uploader;
import com.qiwenshare.file.api.IFiletransferService;

import com.qiwenshare.common.domain.AliyunOSS;
import com.qiwenshare.file.config.QiwenFileConfig;
import com.qiwenshare.file.mapper.FileMapper;
import com.qiwenshare.file.mapper.FiletransferMapper;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.StorageBean;
import com.qiwenshare.file.domain.UserBean;
import org.springframework.stereotype.Service;


@Service
public class FiletransferService implements IFiletransferService {

    @Resource
    FiletransferMapper filetransferMapper;
    @Resource
    FileMapper fileMapper;

    @Resource
    QiwenFileConfig qiwenFileConfig;

    @Override
    public void deleteUserImageByIds(List<Integer> imageidList) {
        filetransferMapper.deleteUserImageByIds(imageidList);
    }


    @Override
    public void uploadFile(HttpServletRequest request, FileBean fileBean, UserBean sessionUserBean) {
        AliyunOSS oss = qiwenFileConfig.getAliyun().getOss();
        request.setAttribute("oss", oss);
        Uploader uploader;
        UploadFile uploadFile = new UploadFile();
        uploadFile.setChunkNumber(fileBean.getChunkNumber());
        uploadFile.setChunkSize(fileBean.getChunkSize());
        uploadFile.setTotalChunks(fileBean.getTotalChunks());
        uploadFile.setIdentifier(fileBean.getIdentifier());
        uploadFile.setTotalSize(fileBean.getTotalSize());
        uploadFile.setCurrentChunkSize(fileBean.getCurrentChunkSize());
        if (oss.isEnabled()) {
            uploader = new AliyunOSSUploaderFactory().getUploader(uploadFile);
        } else {
            uploader = new ChunkUploaderFactory().getUploader(uploadFile);
        }

        List<UploadFile> uploadFileList = uploader.upload(request);
        for (int i = 0; i < uploadFileList.size(); i++){
            uploadFile = uploadFileList.get(i);
            fileBean.setTimeStampName(uploadFile.getTimeStampName());
            if (uploadFile.getSuccess() == 1){
                fileBean.setFileUrl(uploadFile.getUrl());
                fileBean.setFileSize(uploadFile.getFileSize());
                fileBean.setFileName(uploadFile.getFileName());
                fileBean.setExtendName(uploadFile.getFileType());
                fileBean.setUploadTime(DateUtil.getCurrentTime());
                fileBean.setIsOSS(uploadFile.getIsOSS());
                fileBean.setIsDir(0);
                fileBean.setPointCount(1);
                fileMapper.insert(fileBean);

            }


            synchronized (FiletransferService.class) {

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
