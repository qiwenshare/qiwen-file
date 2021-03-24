package com.qiwenshare.common.factory;

import com.qiwenshare.common.operation.delete.Deleter;
import com.qiwenshare.common.operation.delete.product.LocalStorageDeleter;
import com.qiwenshare.common.operation.download.Downloader;
import com.qiwenshare.common.operation.download.product.LocalStorageDownloader;
import com.qiwenshare.common.operation.upload.product.LocalStorageUploader;
import com.qiwenshare.common.operation.upload.Uploader;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LocalStorageOperationFactory implements FileOperationFactory{

    @Resource
    LocalStorageUploader localStorageUploader;
    @Resource
    LocalStorageDownloader localStorageDownloader;
    @Resource
    LocalStorageDeleter localStorageDeleter;
    @Override
    public Uploader getUploader() {
        return localStorageUploader;
    }

    @Override
    public Downloader getDownloader() {
        return localStorageDownloader;
    }

    @Override
    public Deleter getDeleter() {
        return localStorageDeleter;
    }


}
