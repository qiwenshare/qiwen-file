package com.qiwenshare.common.factory;

import com.qiwenshare.common.download.Downloader;
import com.qiwenshare.common.download.product.AliyunOSSDownloader;
import com.qiwenshare.common.upload.product.AliyunOSSUploader;
import com.qiwenshare.common.upload.Uploader;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AliyunOSSOperationFactory implements FileOperationFactory {
    @Resource
    AliyunOSSUploader aliyunOSSUploader;
    @Resource
    AliyunOSSDownloader aliyunOSSDownloader;
    @Override
    public Uploader getUploader() {
        return aliyunOSSUploader;
    }

    @Override
    public Downloader getDownloader() {
        return aliyunOSSDownloader;
    }

}
