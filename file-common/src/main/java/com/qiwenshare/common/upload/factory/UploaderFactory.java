package com.qiwenshare.common.upload.factory;

import com.qiwenshare.common.domain.UploadFile;
import com.qiwenshare.common.upload.Uploader;

public interface UploaderFactory {
    public Uploader getUploader();
    public Uploader getUploader(UploadFile uploadFile);
}
