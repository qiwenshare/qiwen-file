package com.qiwenshare.common.operation.upload.product;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.qiwenshare.common.operation.upload.domain.UploadFile;
import com.qiwenshare.common.exception.UploadGeneralException;
import com.qiwenshare.common.operation.upload.Uploader;
import com.qiwenshare.common.util.FileUtil;
import com.qiwenshare.common.util.PathUtil;
import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.fileupload.disk.DiskFileItemFactory;
//import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;
@Component
@Slf4j
public class FastDFSUploader extends Uploader {
    public static Object lock = new Object();
    @Resource
    AppendFileStorageClient defaultAppendFileStorageClient;

    private static Map<String, Integer> CURRENT_UPLOAD_CHUNK_NUMBER = new HashMap<>();
    private static Map<String, Long> UPLOADED_SIZE = new HashMap<>();
    private static Map<String, String> STORE_PATH = new HashMap<>();
    private static Map<String, Object> LOCK_MAP = new HashMap<>();

    @Override
    public List<UploadFile> upload(HttpServletRequest request, UploadFile uploadFile) {
        log.info("开始上传upload");

        List<UploadFile> saveUploadFileList = new ArrayList<>();
        StandardMultipartHttpServletRequest standardMultipartHttpServletRequest = (StandardMultipartHttpServletRequest) request;

        boolean isMultipart = ServletFileUpload.isMultipartContent(standardMultipartHttpServletRequest);
        if (!isMultipart) {
            throw new UploadGeneralException("未包含文件上传域");
        }
        DiskFileItemFactory dff = new DiskFileItemFactory();//1、创建工厂
        String savePath = getLocalFileSavePath();
        dff.setRepository(new File(savePath));

        try {
            ServletFileUpload sfu = new ServletFileUpload(dff);//2、创建文件上传解析器
            sfu.setSizeMax(this.maxSize * 1024L);
            sfu.setHeaderEncoding("utf-8");//3、解决文件名的中文乱码
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
            synchronized (lock) {
                if (LOCK_MAP.get(uploadFile.getIdentifier()) == null) {
                    LOCK_MAP.put(uploadFile.getIdentifier(), new Object());
                }
            }
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
                LOCK_MAP.remove(uploadFile.getIdentifier());
                uploadFile.setUrl(STORE_PATH.get(uploadFile.getIdentifier()));
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

        synchronized (LOCK_MAP.get(uploadFile.getIdentifier())) {
            // 存储在fastdfs不带组的路径

            log.info("当前文件的Md5:{}", uploadFile.getIdentifier());

            log.info("当前块的大小:{}", uploadFile.getCurrentChunkSize());
            if (CURRENT_UPLOAD_CHUNK_NUMBER.get(uploadFile.getIdentifier()) == null) {
                CURRENT_UPLOAD_CHUNK_NUMBER.put(uploadFile.getIdentifier(), 1);
            }

            while (uploadFile.getChunkNumber() != CURRENT_UPLOAD_CHUNK_NUMBER.get(uploadFile.getIdentifier())) {
                try {
                    LOCK_MAP.get(uploadFile.getIdentifier()).wait();
                } catch (InterruptedException e) {
                    log.error("--------InterruptedException-------");
                    e.printStackTrace();
                }
            }

            log.info("***********开始上传第{}块**********", uploadFile.getChunkNumber());
            StorePath storePath = null;

            try {

                if (uploadFile.getChunkNumber() <= 1) {
                    log.info("上传第一块");
                    CURRENT_UPLOAD_CHUNK_NUMBER.put(uploadFile.getIdentifier(), uploadFile.getChunkNumber() + 1);
                    try {
                        storePath = defaultAppendFileStorageClient.uploadAppenderFile("group1", multipartFile.getInputStream(),
                                multipartFile.getSize(), FileUtil.getFileExtendName(multipartFile.getOriginalFilename()));
                        // 记录第一个分片上传的大小
                        UPLOADED_SIZE.put(uploadFile.getIdentifier(), uploadFile.getCurrentChunkSize());
                        log.info("第一块上传完成");
                        if (storePath == null) {
                            CURRENT_UPLOAD_CHUNK_NUMBER.put(uploadFile.getIdentifier(), uploadFile.getChunkNumber());
                            log.info("获取远程文件路径出错");
                            throw new UploadGeneralException("获取远程文件路径出错");
                        }
                    } catch (Exception e) {
                        CURRENT_UPLOAD_CHUNK_NUMBER.put(uploadFile.getIdentifier(), uploadFile.getChunkNumber());
                        log.error("初次上传远程文件出错", e);
                        throw new UploadGeneralException("初次上传远程文件出错", e);
                    }

                    STORE_PATH.put(uploadFile.getIdentifier(), storePath.getPath());
                    log.info("上传文件 result = {}", storePath.getPath());
                } else {
                    log.info("上传第{}块：" + uploadFile.getChunkNumber());
                    CURRENT_UPLOAD_CHUNK_NUMBER.put(uploadFile.getIdentifier(), uploadFile.getChunkNumber() + 1);
                    String path = STORE_PATH.get(uploadFile.getIdentifier());
                    if (path == null) {
                        log.error("无法获取已上传服务器文件地址");
                        CURRENT_UPLOAD_CHUNK_NUMBER.put(uploadFile.getIdentifier(), uploadFile.getChunkNumber());
                        throw new UploadGeneralException("无法获取已上传服务器文件地址");
                    }
                    try {
                        Long alreadySize = UPLOADED_SIZE.get(uploadFile.getIdentifier());
                        // 追加方式实际实用如果中途出错多次,可能会出现重复追加情况,这里改成修改模式,即时多次传来重复文件块,依然可以保证文件拼接正确
                        defaultAppendFileStorageClient.modifyFile("group1", path, multipartFile.getInputStream(),
                                multipartFile.getSize(), alreadySize);
                        // 记录分片上传的大小
                        UPLOADED_SIZE.put(uploadFile.getIdentifier(), alreadySize + multipartFile.getSize());
                        log.info("第{}块更新完成", uploadFile.getChunkNumber());
                    } catch (Exception e) {
                        CURRENT_UPLOAD_CHUNK_NUMBER.put(uploadFile.getIdentifier(), uploadFile.getChunkNumber());
                        log.error("更新远程文件出错", e);
                        throw new UploadGeneralException("更新远程文件出错", e);
                    }
                }
            } catch (Exception e) {
                log.error("上传文件错误", e);
                throw new UploadGeneralException("上传文件错误", e);
            }

            log.info("***********第{}块上传成功**********", uploadFile.getChunkNumber());

            LOCK_MAP.get(uploadFile.getIdentifier()).notifyAll();
        }
    }
}
