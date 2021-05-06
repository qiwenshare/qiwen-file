package com.qiwenshare.common.operation.upload.product;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.qiwenshare.common.exception.UploadGeneralException;
import com.qiwenshare.common.operation.upload.Uploader;
import com.qiwenshare.common.operation.upload.domain.UploadFile;
import com.qiwenshare.common.util.FileUtil;
import com.qiwenshare.common.util.PathUtil;
import com.qiwenshare.common.util.concurrent.locks.RedisLock;
import com.qiwenshare.common.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class FastDFSUploader extends Uploader {

    @Resource
    AppendFileStorageClient defaultAppendFileStorageClient;
    @Resource
    RedisLock redisLock;
    @Resource
    RedisUtil redisUtil;

    @Override
    public List<UploadFile> upload(HttpServletRequest request, UploadFile uploadFile) {

        List<UploadFile> saveUploadFileList = new ArrayList<>();
        StandardMultipartHttpServletRequest standardMultipartHttpServletRequest = (StandardMultipartHttpServletRequest) request;

        boolean isMultipart = ServletFileUpload.isMultipartContent(standardMultipartHttpServletRequest);
        if (!isMultipart) {
            throw new UploadGeneralException("未包含文件上传域");
        }

        String savePath = getLocalFileSavePath();

        try {

            Iterator<String> iter = standardMultipartHttpServletRequest.getFileNames();
            while (iter.hasNext()) {
                saveUploadFileList = doUpload(standardMultipartHttpServletRequest, savePath, iter, uploadFile);
            }
        } catch (Exception e) {
            throw new UploadGeneralException(e);
        }

        log.info("结束上传");
        return saveUploadFileList;
    }


    private List<UploadFile> doUpload(StandardMultipartHttpServletRequest standardMultipartHttpServletRequest, String savePath, Iterator<String> iter, UploadFile uploadFile) {

        List<UploadFile> saveUploadFileList = new ArrayList<>();

        try {
            MultipartFile multipartfile = standardMultipartHttpServletRequest.getFile(iter.next());

            uploadFileChunk(multipartfile, uploadFile);

            String timeStampName = getTimeStampName();
            String originalName = multipartfile.getOriginalFilename();

            String fileName = getFileName(originalName);

            String fileType = FileUtil.getFileExtendName(originalName);
            uploadFile.setFileName(fileName);
            uploadFile.setFileType(fileType);
            uploadFile.setTimeStampName(timeStampName);


            String confFilePath = savePath + FILE_SEPARATOR + uploadFile.getIdentifier() + "." + "conf";
            File confFile = new File(PathUtil.getStaticPath() + FILE_SEPARATOR + confFilePath);


            boolean isComplete = checkUploadStatus(uploadFile, confFile);
            if (isComplete) {
                log.info("分片上传完成");
                String path = redisUtil.getObject(uploadFile.getIdentifier() + "_storage_path");
                uploadFile.setUrl(path);
                uploadFile.setSuccess(1);
                uploadFile.setMessage("上传成功");
            } else {
                uploadFile.setSuccess(0);
                uploadFile.setMessage("未完成");
            }

        } catch (Exception e) {
            throw new UploadGeneralException(e);
        }

        uploadFile.setIsOSS(0);
        uploadFile.setStorageType(2);
        uploadFile.setFileSize(uploadFile.getTotalSize());
        saveUploadFileList.add(uploadFile);
        return saveUploadFileList;
    }

    public void uploadFileChunk(MultipartFile multipartFile, UploadFile uploadFile) {
        redisLock.lock(uploadFile.getIdentifier());
        try {

            if (redisUtil.getObject(uploadFile.getIdentifier() + "_current_upload_chunk_number") == null) {
                redisUtil.set(uploadFile.getIdentifier() + "_current_upload_chunk_number", 1, 1000 * 60 * 60);
            }

            String currentUploadChunkNumber = redisUtil.getObject(uploadFile.getIdentifier() + "_current_upload_chunk_number");
            if (uploadFile.getChunkNumber() != Integer.parseInt(currentUploadChunkNumber)) {
                redisLock.unlock(uploadFile.getIdentifier());
                while (redisLock.tryLock(uploadFile.getIdentifier(), 300, TimeUnit.SECONDS)) {
                    if (uploadFile.getChunkNumber() == Integer.parseInt(redisUtil.getObject(uploadFile.getIdentifier() + "_current_upload_chunk_number"))) {
                        break;
                    } else {
                        redisLock.unlock(uploadFile.getIdentifier());
                    }
                }
            }

            log.info("***********开始上传第{}块**********", uploadFile.getChunkNumber());
            StorePath storePath = null;
            redisUtil.getIncr(uploadFile.getIdentifier() + "_current_upload_chunk_number");

            if (uploadFile.getChunkNumber() <= 1) {
                log.info("上传第一块");

                storePath = defaultAppendFileStorageClient.uploadAppenderFile("group1", multipartFile.getInputStream(),
                        multipartFile.getSize(), FileUtil.getFileExtendName(multipartFile.getOriginalFilename()));
                // 记录第一个分片上传的大小
                redisUtil.set(uploadFile.getIdentifier() + "_uploaded_size", uploadFile.getCurrentChunkSize(), 1000 * 60 * 60);

                log.info("第一块上传完成");
                if (storePath == null) {
                    redisUtil.set(uploadFile.getIdentifier() + "_current_upload_chunk_number", uploadFile.getChunkNumber(), 1000 * 60 * 60);

                    log.info("获取远程文件路径出错");
                    throw new UploadGeneralException("获取远程文件路径出错");
                }

                redisUtil.set(uploadFile.getIdentifier() + "_storage_path", storePath.getPath(), 1000 * 60 * 60);

                log.info("上传文件 result = {}", storePath.getPath());
            } else {
                log.info("正在上传第{}块：" , uploadFile.getChunkNumber());

                String path = redisUtil.getObject(uploadFile.getIdentifier() + "_storage_path");

                if (path == null) {
                    log.error("无法获取已上传服务器文件地址");
                    throw new UploadGeneralException("无法获取已上传服务器文件地址");
                }

                String uploadedSizeStr = redisUtil.getObject(uploadFile.getIdentifier() + "_uploaded_size");
                Long alreadySize = Long.parseLong(uploadedSizeStr);

                // 追加方式实际实用如果中途出错多次,可能会出现重复追加情况,这里改成修改模式,即时多次传来重复文件块,依然可以保证文件拼接正确
                defaultAppendFileStorageClient.modifyFile("group1", path, multipartFile.getInputStream(),
                        multipartFile.getSize(), alreadySize);
                // 记录分片上传的大小
                redisUtil.set(uploadFile.getIdentifier() + "_uploaded_size", alreadySize + multipartFile.getSize(), 1000 * 60 * 60);

            }
            log.info("***********第{}块上传成功**********", uploadFile.getChunkNumber());
        } catch (Exception e) {
            log.error("***********第{}块上传失败，自动重试**********", uploadFile.getChunkNumber());
            redisUtil.set(uploadFile.getIdentifier() + "_current_upload_chunk_number", uploadFile.getChunkNumber(), 1000 * 60 * 60);
            throw new UploadGeneralException("更新远程文件出错", e);
        } finally {
            redisLock.unlock(uploadFile.getIdentifier());
        }

    }
}
