package com.qiwenshare.common.upload.factory;

import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.qiwenshare.common.domain.UploadFile;
import com.qiwenshare.common.upload.Uploader;

public interface UploaderFactory {
    Uploader getUploader();
    Uploader getUploader(UploadFile uploadFile);
}
