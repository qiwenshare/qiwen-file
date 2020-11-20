package com.qiwenshare.common.upload.factory;

import com.qiwenshare.common.domain.UploadFile;
import com.qiwenshare.common.upload.product.AliyunOSSUploader;
import com.qiwenshare.common.upload.Uploader;


public class AliyunOSSUploaderFactory implements UploaderFactory {
    @Override
    public Uploader getUploader() {
        return new AliyunOSSUploader();
    }

    @Override
    public Uploader getUploader(UploadFile uploadFile) {
        return new AliyunOSSUploader(uploadFile);
    }
}
