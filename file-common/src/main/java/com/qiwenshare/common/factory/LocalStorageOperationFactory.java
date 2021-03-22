package com.qiwenshare.common.factory;

import com.qiwenshare.common.download.Downloader;
import com.qiwenshare.common.download.product.LocalStorageDownloader;
import com.qiwenshare.common.upload.product.LocalStorageUploader;
import com.qiwenshare.common.upload.Uploader;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LocalStorageOperationFactory implements FileOperationFactory{

    @Resource
    LocalStorageUploader ChunkUploader;
    @Resource
    LocalStorageDownloader localStorageDownloader;
    @Override
    public Uploader getUploader() {
        return ChunkUploader;
    }

    @Override
    public Downloader getDownloader() {
        return localStorageDownloader;
    }


}
