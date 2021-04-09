package com.qiwenshare.common.operation.upload.product;

import com.qiwenshare.common.exception.NotSameFileExpection;
import com.qiwenshare.common.operation.upload.domain.UploadFile;
import com.qiwenshare.common.exception.UploadGeneralException;
import com.qiwenshare.common.operation.ImageOperation;
import com.qiwenshare.common.operation.upload.Uploader;
import com.qiwenshare.common.util.FileUtil;
import com.qiwenshare.common.util.PathUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
@Component
public class LocalStorageUploader extends Uploader {

    @Override
    public List<UploadFile> upload(HttpServletRequest httpServletRequest,UploadFile uploadFile) {
        List<UploadFile> saveUploadFileList = new ArrayList<UploadFile>();
        StandardMultipartHttpServletRequest standardMultipartHttpServletRequest = (StandardMultipartHttpServletRequest) httpServletRequest;
        boolean isMultipart = ServletFileUpload.isMultipartContent(standardMultipartHttpServletRequest);
        if (!isMultipart) {
            throw new UploadGeneralException("未包含文件上传域");
        }

        try {

            Iterator<String> iter = standardMultipartHttpServletRequest.getFileNames();
            while (iter.hasNext()) {
                saveUploadFileList = doUpload(standardMultipartHttpServletRequest, iter, uploadFile);
            }
        } catch (IOException e) {
            throw new UploadGeneralException("未包含文件上传域");
        } catch (NotSameFileExpection notSameFileExpection) {
            notSameFileExpection.printStackTrace();
        }
        return saveUploadFileList;
    }

    private List<UploadFile> doUpload(StandardMultipartHttpServletRequest standardMultipartHttpServletRequest,  Iterator<String> iter, UploadFile uploadFile) throws IOException, NotSameFileExpection {
        String savePath = getLocalFileSavePath();
        List<UploadFile> saveUploadFileList = new ArrayList<UploadFile>();
        MultipartFile multipartfile = standardMultipartHttpServletRequest.getFile(iter.next());

        String timeStampName = uploadFile.getIdentifier();

        String originalName = multipartfile.getOriginalFilename();

        String fileName = getFileName(originalName);
        String fileType = FileUtil.getFileExtendName(originalName);
        uploadFile.setFileName(fileName);
        uploadFile.setFileType(fileType);
        uploadFile.setTimeStampName(timeStampName);

        String saveFilePath = savePath + FILE_SEPARATOR + timeStampName + "." + fileType;
        String tempFilePath = savePath + FILE_SEPARATOR + timeStampName + "." + fileType + "_tmp";
        String minFilePath = savePath + FILE_SEPARATOR + timeStampName + "_min" + "." + fileType;
        String confFilePath = savePath + FILE_SEPARATOR + timeStampName + "." + "conf";
        File file = new File(PathUtil.getStaticPath() + FILE_SEPARATOR + saveFilePath);
        File tempFile = new File(PathUtil.getStaticPath() + FILE_SEPARATOR + tempFilePath);
        File minFile = new File(PathUtil.getStaticPath() + FILE_SEPARATOR + minFilePath);
        File confFile = new File(PathUtil.getStaticPath() + FILE_SEPARATOR + confFilePath);
        uploadFile.setIsOSS(0);
        uploadFile.setStorageType(0);
        uploadFile.setUrl(saveFilePath);

        if (StringUtils.isEmpty(uploadFile.getTaskId())) {
            uploadFile.setTaskId(UUID.randomUUID().toString());
        }

        //第一步 打开将要写入的文件
        RandomAccessFile raf = new RandomAccessFile(tempFile, "rw");
        //第二步 打开通道
        FileChannel fileChannel = raf.getChannel();
        //第三步 计算偏移量
        long position = (uploadFile.getChunkNumber() - 1) * uploadFile.getChunkSize();
        //第四步 获取分片数据
        byte[] fileData = multipartfile.getBytes();
        //第五步 写入数据
        fileChannel.position(position);
        fileChannel.write(ByteBuffer.wrap(fileData));
        fileChannel.force(true);
        fileChannel.close();
        raf.close();
        //判断是否完成文件的传输并进行校验与重命名
        boolean isComplete = checkUploadStatus(uploadFile, confFile);
        if (isComplete) {
            FileInputStream fileInputStream = new FileInputStream(tempFile.getPath());
            String md5 = DigestUtils.md5Hex(fileInputStream);
            fileInputStream.close();
            if (StringUtils.isNotBlank(md5) && !md5.equals(uploadFile.getIdentifier())) {
                throw new NotSameFileExpection();
            }
            tempFile.renameTo(file);
            if (FileUtil.isImageFile(uploadFile.getFileType())){
                ImageOperation.thumbnailsImage(file, minFile, 300);
            }

            uploadFile.setSuccess(1);
            uploadFile.setMessage("上传成功");
        } else {
            uploadFile.setSuccess(0);
            uploadFile.setMessage("未完成");
        }
        uploadFile.setFileSize(uploadFile.getTotalSize());
        saveUploadFileList.add(uploadFile);

        return saveUploadFileList;
    }

}
