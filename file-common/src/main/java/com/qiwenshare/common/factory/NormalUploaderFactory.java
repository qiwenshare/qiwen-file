//package com.qiwenshare.common.factory;
//
//import com.github.tobato.fastdfs.service.AppendFileStorageClient;
//import com.qiwenshare.common.domain.UploadFile;
//import com.qiwenshare.common.upload.product.NormalUploader;
//import com.qiwenshare.common.upload.Uploader;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//
//@Component
//public class NormalUploaderFactory implements UploaderFactory {
//
//    @Override
//    public Uploader getUploader() {
//        return new NormalUploader();
//    }
//
//    @Override
//    public Uploader getUploader(UploadFile uploadFile) {
//        return new NormalUploader();
//    }
//
//}
