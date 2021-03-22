package com.qiwenshare.common.upload.factory;

import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.qiwenshare.common.domain.UploadFile;
import com.qiwenshare.common.upload.Uploader;
import com.qiwenshare.common.upload.product.FastDFSUploader;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class FastDFSUploaderFactory implements UploaderFactory {

//    @Resource
//    AppendFileStorageClient defaultAppendFileStorageClient;
    @Resource
    FastDFSUploader fastDFSUploader;
    @Override
    public Uploader getUploader() {
        return fastDFSUploader;
    }

//    @Override
//    public Uploader getUploader(UploadFile uploadFile) {
//        return new FastDFSUploader(uploadFile, defaultAppendFileStorageClient);
//    }

}
