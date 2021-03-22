package com.qiwenshare.file.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qiwenshare.common.cbb.DateUtil;
import com.qiwenshare.common.domain.DownloadFile;
import com.qiwenshare.common.domain.UploadFile;
//import com.qiwenshare.common.factory.FileOperationFactory;
import com.qiwenshare.common.download.Downloader;
import com.qiwenshare.common.factory.FileOperationFactory;
import com.qiwenshare.common.upload.Uploader;

import com.qiwenshare.file.api.IFiletransferService;

import com.qiwenshare.common.config.QiwenFileConfig;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.dto.DownloadFileDTO;
import com.qiwenshare.file.dto.UploadFileDTO;
import com.qiwenshare.file.mapper.FileMapper;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.StorageBean;
import com.qiwenshare.file.mapper.StorageMapper;
import com.qiwenshare.file.mapper.UserFileMapper;
import org.springframework.stereotype.Service;


@Service
public class FiletransferService implements IFiletransferService {

    @Resource
    StorageMapper storageMapper;
    @Resource
    FileMapper fileMapper;
    @Resource
    QiwenFileConfig qiwenFileConfig;

    @Resource
    UserFileMapper userFileMapper;

    @Resource
    FileOperationFactory fastDFSOperationFactory;
    @Resource
    FileOperationFactory aliyunOSSOperationFactory;
    @Resource
    FileOperationFactory localStorageOperationFactory;

    @Override
    public void uploadFile(HttpServletRequest request, UploadFileDTO UploadFileDto, Long userId) {

        Uploader uploader = null;
        UploadFile uploadFile = new UploadFile();
        uploadFile.setChunkNumber(UploadFileDto.getChunkNumber());
        uploadFile.setChunkSize(UploadFileDto.getChunkSize());
        uploadFile.setTotalChunks(UploadFileDto.getTotalChunks());
        uploadFile.setIdentifier(UploadFileDto.getIdentifier());
        uploadFile.setTotalSize(UploadFileDto.getTotalSize());
        uploadFile.setCurrentChunkSize(UploadFileDto.getCurrentChunkSize());
        synchronized (FiletransferService.class) {
            String storageType = qiwenFileConfig.getStorageType();
            if ("0".equals(storageType)) {
                uploader = localStorageOperationFactory.getUploader();
            } else if ("1".equals(storageType)) {
                uploader = aliyunOSSOperationFactory.getUploader();
            } else if ("2".equals(storageType)) {
                uploader = fastDFSOperationFactory.getUploader();
            }
        }

        List<UploadFile> uploadFileList = uploader.upload(request, uploadFile);
        for (int i = 0; i < uploadFileList.size(); i++){
            uploadFile = uploadFileList.get(i);
            FileBean fileBean = new FileBean();
            BeanUtil.copyProperties(UploadFileDto, fileBean);
            fileBean.setTimeStampName(uploadFile.getTimeStampName());
            if (uploadFile.getSuccess() == 1){
                fileBean.setFileUrl(uploadFile.getUrl());
                fileBean.setFileSize(uploadFile.getFileSize());
                //fileBean.setUploadTime(DateUtil.getCurrentTime());
                fileBean.setIsOSS(uploadFile.getIsOSS());
                fileBean.setStorageType(uploadFile.getStorageType());
                fileBean.setPointCount(1);
                fileMapper.insert(fileBean);
                UserFile userFile = new UserFile();
                userFile.setFileId(fileBean.getFileId());
                userFile.setExtendName(uploadFile.getFileType());
                userFile.setFileName(uploadFile.getFileName());
                userFile.setFilePath(UploadFileDto.getFilePath());
                userFile.setDeleteFlag(0);
                userFile.setUserId(userId);
                userFile.setIsDir(0);
                userFile.setUploadTime(DateUtil.getCurrentTime());
                userFileMapper.insert(userFile);

                synchronized (FiletransferService.class) {

                    StorageBean storageBean = selectStorageBean(new StorageBean(userId));
                    if (storageBean == null) {
                        StorageBean storage = new StorageBean(userId);
                        storage.setStorageSize(fileBean.getFileSize());
                        insertStorageBean(storage);
                    } else {
                        storageBean.setStorageSize(storageBean.getStorageSize() + uploadFile.getFileSize());
                        updateStorageBean(storageBean);
                    }
                }

            }

        }
    }

    @Override
    public void downloadFile(HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO) {
        UserFile userFile = userFileMapper.selectById(downloadFileDTO.getUserFileId());

        String fileName = userFile.getFileName() + "." + userFile.getExtendName();
        try {
            fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpServletResponse.setContentType("application/force-download");// 设置强制下载不打开
        httpServletResponse.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名


        FileBean fileBean = fileMapper.selectById(userFile.getFileId());
        Downloader downloader = null;
        if (fileBean.getIsOSS() != null && fileBean.getIsOSS() == 1) {
            downloader = aliyunOSSOperationFactory.getDownloader();
        } else if (fileBean.getStorageType() == 0) {
            downloader = localStorageOperationFactory.getDownloader();
        } else if (fileBean.getStorageType() == 1) {
            downloader = aliyunOSSOperationFactory.getDownloader();
        } else if (fileBean.getStorageType() == 2) {
            downloader = fastDFSOperationFactory.getDownloader();
        }
        DownloadFile uploadFile = new DownloadFile();
        uploadFile.setFileUrl(fileBean.getFileUrl());
        uploadFile.setTimeStampName(fileBean.getTimeStampName());
        downloader.download(httpServletResponse, uploadFile);
    }

    @Override
    public StorageBean selectStorageBean(StorageBean storageBean) {
        LambdaQueryWrapper<StorageBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StorageBean::getUserId, storageBean.getUserId());
        return storageMapper.selectOne(lambdaQueryWrapper);

    }

    @Override
    public void insertStorageBean(StorageBean storageBean) {
        storageMapper.insert(storageBean);
    }

    @Override
    public void updateStorageBean(StorageBean storageBean) {
        LambdaUpdateWrapper<StorageBean> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(StorageBean::getStorageSize, storageBean.getStorageSize())
                .eq(StorageBean::getStorageId, storageBean.getStorageId())
                .eq(StorageBean::getUserId, storageBean.getUserId());
        storageMapper.update(null, lambdaUpdateWrapper);
    }

    @Override
    public StorageBean selectStorageByUser(StorageBean storageBean) {
        LambdaQueryWrapper<StorageBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StorageBean::getUserId, storageBean.getUserId());
        return storageMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public Long selectStorageSizeByUserId(Long userId){
        return userFileMapper.selectStorageSizeByUserId(userId);
    }
}
