package com.qiwenshare.common.upload.product;

import com.aliyuncs.utils.StringUtils;
import com.qiwenshare.common.domain.UploadFile;
import com.qiwenshare.common.operation.ImageOperation;
import com.qiwenshare.common.upload.Uploader;
import com.qiwenshare.common.util.FileUtil;
import com.qiwenshare.common.util.PathUtil;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NormalUploader extends Uploader {
    private static final Logger logger = LoggerFactory.getLogger(NormalUploader.class);

    @Override
    public List<UploadFile> upload(HttpServletRequest httpServletRequest) {
        List<UploadFile> saveUploadFileList = new ArrayList<UploadFile>();
        this.request = (StandardMultipartHttpServletRequest) httpServletRequest;
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

        return saveUploadFileList;
    }

    private List<UploadFile> doUpload(String savePath, Iterator<String> iter) throws IOException {
        List<UploadFile> saveUploadFileList = new ArrayList<UploadFile>();
        UploadFile uploadFile = new UploadFile();
        MultipartFile multipartfile = this.request.getFile(iter.next());

        InputStream inputStream = multipartfile.getInputStream();
        String timeStampName = getTimeStampName();


        String originalName = multipartfile.getOriginalFilename();

        String fileName = getFileName(originalName);

        String fileType = FileUtil.getFileType(originalName);
        uploadFile.setFileName(fileName);
        uploadFile.setFileType(fileType);
        uploadFile.setTimeStampName(timeStampName);

        String saveFilePath = savePath + FILE_SEPARATOR + timeStampName;
        String minFilePath = savePath + FILE_SEPARATOR + timeStampName;
        if (StringUtils.isNotEmpty(fileType)) {
            saveFilePath += "." + fileType;
            minFilePath += "_min" + "." + fileType;
        }

        File file = new File(PathUtil.getStaticPath() + FILE_SEPARATOR + saveFilePath);
        File minFile = new File(PathUtil.getStaticPath() + FILE_SEPARATOR + minFilePath);

        uploadFile.setIsOSS(0);
        uploadFile.setUrl(saveFilePath);
        BufferedInputStream in = null;
        FileOutputStream out = null;
        BufferedOutputStream output = null;

        try {
            in = new BufferedInputStream(inputStream);
            out = new FileOutputStream(file);
            output = new BufferedOutputStream(out);
            Streams.copy(in, output, true);
            if (FileUtil.isImageFile(uploadFile.getFileType())){
                ImageOperation.thumbnailsImage(file, minFile, 300);
            }

        } catch (FileNotFoundException e) {
            logger.error("文件没有发现" + e);
        } catch (IOException e) {
            logger.error("文件读取失败" + e);
        } finally {

            closeStream(in, out, output);
        }



        uploadFile.setSuccess(1);
        uploadFile.setMessage("上传成功");
        uploadFile.setFileSize(request.getContentLengthLong());
        saveUploadFileList.add(uploadFile);
        return saveUploadFileList;
    }


    private void closeStream(BufferedInputStream in, FileOutputStream out,
                             BufferedOutputStream output) throws IOException {
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
        if (output != null) {
            output.close();
        }
    }
}
