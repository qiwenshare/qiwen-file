package com.qiwenshare.common.factory;

import com.qiwenshare.common.download.Downloader;
import com.qiwenshare.common.download.product.FastDFSDownloader;
import com.qiwenshare.common.upload.Uploader;
import com.qiwenshare.common.upload.product.FastDFSUploader;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class FastDFSOperationFactory implements FileOperationFactory {

//    @Resource
//    AppendFileStorageClient defaultAppendFileStorageClient;
    @Resource
    FastDFSUploader fastDFSUploader;
    @Resource
    FastDFSDownloader fastDFSDownloader;
    @Override
    public Uploader getUploader() {
        return fastDFSUploader;
    }

    @Override
    public Downloader getDownloader() {
        return fastDFSDownloader;
    }

//    @Override
//    public Uploader getUploader(UploadFile uploadFile) {
//        return new FastDFSUploader(uploadFile, defaultAppendFileStorageClient);
//    }

}
