package com.qiwenshare.common.factory;

import com.qiwenshare.common.operation.delete.Deleter;
import com.qiwenshare.common.operation.download.Downloader;
import com.qiwenshare.common.operation.upload.Uploader;

public interface FileOperationFactory {
    Uploader getUploader();
    Downloader getDownloader();
    Deleter getDeleter();
}
