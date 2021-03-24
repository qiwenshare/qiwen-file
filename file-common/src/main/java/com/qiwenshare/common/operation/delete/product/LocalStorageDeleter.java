package com.qiwenshare.common.operation.delete.product;

import com.qiwenshare.common.operation.delete.Deleter;
import com.qiwenshare.common.domain.DeleteFile;
import com.qiwenshare.common.operation.FileOperation;
import com.qiwenshare.common.util.FileUtil;
import com.qiwenshare.common.util.PathUtil;
import org.springframework.stereotype.Component;

@Component
public class LocalStorageDeleter extends Deleter {
    @Override
    public void delete(DeleteFile deleteFile) {
        FileOperation.deleteFile(PathUtil.getStaticPath() + deleteFile.getFileUrl());
        if (FileUtil.isImageFile(FileUtil.getFileExtendName(deleteFile.getFileUrl()))) {
            FileOperation.deleteFile(PathUtil.getStaticPath() + deleteFile.getFileUrl().replace(deleteFile.getTimeStampName(), deleteFile.getTimeStampName() + "_min"));
        }
    }
}
