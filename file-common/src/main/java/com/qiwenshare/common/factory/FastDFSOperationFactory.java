package com.qiwenshare.common.factory;

import com.qiwenshare.common.operation.delete.Deleter;
import com.qiwenshare.common.operation.delete.product.FastDFSDeleter;
import com.qiwenshare.common.operation.download.Downloader;
import com.qiwenshare.common.operation.download.product.FastDFSDownloader;
import com.qiwenshare.common.operation.upload.Uploader;
import com.qiwenshare.common.operation.upload.product.FastDFSUploader;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class FastDFSOperationFactory implements FileOperationFactory {

    @Resource
    FastDFSUploader fastDFSUploader;
    @Resource
    FastDFSDownloader fastDFSDownloader;
    @Resource
    FastDFSDeleter fastDFSDeleter;
    @Override
    public Uploader getUploader() {
        return fastDFSUploader;
    }

    @Override
    public Downloader getDownloader() {
        return fastDFSDownloader;
    }

    @Override
    public Deleter getDeleter() {
        return fastDFSDeleter;
    }


}
