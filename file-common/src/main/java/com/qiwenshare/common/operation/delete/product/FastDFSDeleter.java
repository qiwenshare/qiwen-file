package com.qiwenshare.common.operation.delete.product;

import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.qiwenshare.common.operation.delete.Deleter;
import com.qiwenshare.common.operation.delete.domain.DeleteFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FastDFSDeleter extends Deleter {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    @Override
    public void delete(DeleteFile deleteFile) {
        fastFileStorageClient.deleteFile(deleteFile.getFileUrl().replace("M00", "group1"));
    }
}
