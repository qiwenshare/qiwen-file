package com.qiwenshare.common.upload.factory;

import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.qiwenshare.common.domain.UploadFile;
import com.qiwenshare.common.upload.product.ChunkUploader;
import com.qiwenshare.common.upload.Uploader;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ChunkUploaderFactory implements UploaderFactory {

    @Override
    public Uploader getUploader() {
        return new ChunkUploader();
    }

    @Override
    public Uploader getUploader(UploadFile uploadFile) {
        return new ChunkUploader(uploadFile);
    }


}
