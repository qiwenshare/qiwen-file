package com.qiwenshare.common.factory;


import com.qiwenshare.common.operation.delete.Deleter;
import com.qiwenshare.common.operation.delete.product.AliyunOSSDeleter;

import com.qiwenshare.common.operation.download.Downloader;
import com.qiwenshare.common.operation.download.product.AliyunOSSDownloader;
import com.qiwenshare.common.operation.upload.Uploader;
import com.qiwenshare.common.operation.upload.product.AliyunOSSUploader;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AliyunOSSOperationFactory implements FileOperationFactory {
    @Resource
    AliyunOSSUploader aliyunOSSUploader;
    @Resource
    AliyunOSSDownloader aliyunOSSDownloader;
    @Resource
    AliyunOSSDeleter aliyunOSSDeleter;
    @Override
    public Uploader getUploader() {
        return aliyunOSSUploader;
    }

    @Override
    public Downloader getDownloader() {
        return aliyunOSSDownloader;
    }

    @Override
    public Deleter getDeleter() {
        return aliyunOSSDeleter;
    }

}
