package com.qiwenshare.common.download.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.qiwenshare.common.config.QiwenFileConfig;
import com.qiwenshare.common.domain.DownloadFile;
import com.qiwenshare.common.download.Downloader;
import com.qiwenshare.common.oss.AliyunOSSDownload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Component
public class AliyunOSSDownloader extends Downloader {
    @Resource
    QiwenFileConfig qiwenFileConfig;
    @Override
    public void download(HttpServletResponse httpServletResponse, DownloadFile downloadFile) {
        BufferedInputStream bis = null;
        byte[] buffer = new byte[1024];
        AliyunOSSDownload aliyunOSSDownload= new AliyunOSSDownload();
        OSS ossClient = aliyunOSSDownload.createOSSClient(qiwenFileConfig.getAliyun().getOss());
        OSSObject ossObject = ossClient.getObject(qiwenFileConfig.getAliyun().getOss().getBucketName(), downloadFile.getTimeStampName());
        InputStream inputStream = ossObject.getObjectContent();
        try {
            bis = new BufferedInputStream(inputStream);
            OutputStream os = httpServletResponse.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        ossClient.shutdown();
    }
}
