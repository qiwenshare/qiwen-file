package com.qiwenshare.common.upload.product;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.qiwenshare.common.domain.UploadFile;
import com.qiwenshare.common.upload.Uploader;
import com.qiwenshare.common.util.FileUtil;
import com.qiwenshare.common.util.PathUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class FastDFSUploader extends Uploader {
    @Resource
    AppendFileStorageClient defaultAppendFileStorageClient;

    UploadFile uploadFile;

    private static Map<String, Integer> CURRENT_UPLOAD_CHUNK_NUMBER = new HashMap<>();
    private static Map<String, Long> UPLOADED_SIZE = new HashMap<>();
    private static Map<String, String> STORE_PATH = new HashMap<>();
    private static Map<String, Boolean> LOCK_MAP = new HashMap<>();

    public FastDFSUploader() {

    }

    public FastDFSUploader(UploadFile uploadFile) {
        this.uploadFile = uploadFile;
    }


    @Override
    public List<UploadFile> upload(HttpServletRequest request) {
        log.info("开始上传upload");

        List<UploadFile> saveUploadFileList = new ArrayList<>();
        this.request = (StandardMultipartHttpServletRequest) request;

        boolean isMultipart = ServletFileUpload.isMultipartContent(this.request);
        if (!isMultipart) {
            UploadFile uploadFile = new UploadFile();
            uploadFile.setSuccess(0);
            uploadFile.setMessage("未包含文件上传域");
            saveUploadFileList.add(uploadFile);
            return saveUploadFileList;
        }
        DiskFileItemFactory dff = new DiskFileItemFactory();//1、创建工厂
        String savePath = getSaveFilePath();
        dff.setRepository(new File(savePath));

        try {
            ServletFileUpload sfu = new ServletFileUpload(dff);//2、创建文件上传解析器
            sfu.setSizeMax(this.maxSize * 1024L);
            sfu.setHeaderEncoding("utf-8");//3、解决文件名的中文乱码
            Iterator<String> iter = this.request.getFileNames();
            while (iter.hasNext()) {

                saveUploadFileList = doUpload(savePath, iter);
            }
        } catch (IOException e) {
            UploadFile uploadFile = new UploadFile();
            uploadFile.setSuccess(1);
            uploadFile.setMessage("未知错误");
            saveUploadFileList.add(uploadFile);
            e.printStackTrace();
        }

        log.info("结束上传");
        return saveUploadFileList;
    }



    private List<UploadFile> doUpload(String savePath, Iterator<String> iter) throws IOException{

        List<UploadFile> saveUploadFileList = new ArrayList<>();

        try {
            MultipartFile multipartfile = this.request.getFile(iter.next());
            boolean uploadResult = uploadFileChunk(multipartfile);
            if (!uploadResult) {

            }
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

                uploadFile.setUrl(STORE_PATH.get(uploadFile.getIdentifier()));
                uploadFile.setSuccess(1);
                uploadFile.setMessage("上传成功");
            } else {
                uploadFile.setSuccess(0);
                uploadFile.setMessage("未完成");
            }

        } catch (Exception e) {
            log.error("上传出错：" + e);
        }

        uploadFile.setIsOSS(1);

        uploadFile.setFileSize(uploadFile.getTotalSize());
        saveUploadFileList.add(uploadFile);
        return saveUploadFileList;
    }

    public boolean uploadFileChunk(MultipartFile multipartFile) {

        // 存储在fastdfs不带组的路径
//        String noGroupPath = "";
        log.info("当前文件的Md5:{}", uploadFile.getIdentifier());

        // 真正的拥有者
        boolean currOwner = false;

        try {

            Boolean lock = LOCK_MAP.get(uploadFile.getIdentifier());
//
//            if (lock == null){
//                log.info("请求块锁失败");
//                return false;
//            }
            if (lock != null && lock) {
                return false;
            }
            LOCK_MAP.put(uploadFile.getIdentifier(), true);
            // 写入锁的当前拥有者
            currOwner = true;

            // redis中记录当前应该传第几块(从0开始)
            Integer currentChunkInRedis =  CURRENT_UPLOAD_CHUNK_NUMBER.get(uploadFile.getIdentifier());

            log.info("当前块的大小:{}", uploadFile.getCurrentChunkSize());
            if (currentChunkInRedis == null) {
                currentChunkInRedis = 1;
            }

            //此段代码保证顺序，如果满足条件则返回失败
            if (uploadFile.getChunkNumber() < currentChunkInRedis) {
                log.info("当前文件块已上传");
                return false;
            } else if (uploadFile.getChunkNumber() > currentChunkInRedis) {
                log.info("当前文件块需要等待上传,稍后请重试");
                return false;
            }

            log.info("***********开始上传第{}块**********", uploadFile.getChunkNumber());
            StorePath storePath = null;

            try {

                if (uploadFile.getChunkNumber() <= 1) {
                    log.info("上传第一块");
                    CURRENT_UPLOAD_CHUNK_NUMBER.put(uploadFile.getIdentifier(), uploadFile.getChunkNumber() + 1);
                    try {
                        storePath = defaultAppendFileStorageClient.uploadAppenderFile("default_group", multipartFile.getInputStream(),
                                multipartFile.getSize(), FileUtil.getFileExtendName(multipartFile.getOriginalFilename()));
                        // 记录第一个分片上传的大小
                        UPLOADED_SIZE.put(uploadFile.getIdentifier(), uploadFile.getCurrentChunkSize());
                        log.info("第一块上传完成");
                        if (storePath == null) {
                            CURRENT_UPLOAD_CHUNK_NUMBER.put(uploadFile.getIdentifier(), uploadFile.getChunkNumber());
                            log.info("获取远程文件路径出错");
                            return false;
                        }
                    } catch (Exception e) {
                        CURRENT_UPLOAD_CHUNK_NUMBER.put(uploadFile.getIdentifier(), uploadFile.getChunkNumber());
                        log.error("初次上传远程文件出错", e);
                        return false;
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
                        return false;
                    }
                    try {
                        Long alreadySize = UPLOADED_SIZE.get(uploadFile.getIdentifier());
                        // 追加方式实际实用如果中途出错多次,可能会出现重复追加情况,这里改成修改模式,即时多次传来重复文件块,依然可以保证文件拼接正确
                        defaultAppendFileStorageClient.modifyFile("default_group", path, multipartFile.getInputStream(),
                                multipartFile.getSize(), alreadySize);
                        // 记录分片上传的大小
                        UPLOADED_SIZE.put(uploadFile.getIdentifier(), alreadySize + multipartFile.getSize());
                        log.info("第{}块更新完成", uploadFile.getChunkNumber());
                    } catch (Exception e) {
                        CURRENT_UPLOAD_CHUNK_NUMBER.put(uploadFile.getIdentifier(), uploadFile.getChunkNumber());
                        log.error("更新远程文件出错", e);
                        return false;
                    }
                }
            } catch (Exception e) {
                log.error("上传文件错误", e);
                return false;
            }
        } finally {
            // 锁的当前拥有者才能释放块上传锁
            if (currOwner) {
                LOCK_MAP.put(uploadFile.getIdentifier(), false);
                //JedisConfig.setString(chunkLockName, "0");
            }
        }
        log.info("***********第{}块上传成功**********", uploadFile.getChunkNumber());
        return true;
    }
}
