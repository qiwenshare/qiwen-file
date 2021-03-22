package com.qiwenshare.common.factory;

import com.qiwenshare.common.download.Downloader;
import com.qiwenshare.common.upload.Uploader;

public interface FileOperationFactory {
    Uploader getUploader();
    Downloader getDownloader();
//    Uploader getUploader(UploadFile uploadFile);
}
