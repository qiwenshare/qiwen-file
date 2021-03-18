package com.qiwenshare.common.upload.factory;

import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.qiwenshare.common.domain.UploadFile;
import com.qiwenshare.common.upload.product.AliyunOSSUploader;
import com.qiwenshare.common.upload.Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
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
