package com.qiwenshare.common.upload.factory;

import com.qiwenshare.common.domain.UploadFile;
import com.qiwenshare.common.upload.Uploader;
import com.qiwenshare.common.upload.product.FastDFSUploader;

public class FastDFSUploaderFactory implements UploaderFactory {
    @Override
    public Uploader getUploader() {
        return new FastDFSUploader();
    }

    @Override
    public Uploader getUploader(UploadFile uploadFile) {
        return new FastDFSUploader(uploadFile);
    }
}
