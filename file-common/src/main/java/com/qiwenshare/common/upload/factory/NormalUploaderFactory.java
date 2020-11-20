package com.qiwenshare.common.upload.factory;

import com.qiwenshare.common.domain.UploadFile;
import com.qiwenshare.common.upload.product.NormalUploader;
import com.qiwenshare.common.upload.Uploader;

public class NormalUploaderFactory implements UploaderFactory {

    @Override
    public Uploader getUploader() {
        return new NormalUploader();
    }

    @Override
    public Uploader getUploader(UploadFile uploadFile) {
        return new NormalUploader();
    }
}
