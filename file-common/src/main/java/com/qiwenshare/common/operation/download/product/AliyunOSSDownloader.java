package com.qiwenshare.common.operation.download.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.qiwenshare.common.config.QiwenFileConfig;
import com.qiwenshare.common.domain.AliyunOSS;
import com.qiwenshare.common.domain.DownloadFile;
import com.qiwenshare.common.operation.download.Downloader;
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

        OSS ossClient = createOSSClient(qiwenFileConfig.getAliyun().getOss());
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

    public OSS createOSSClient(AliyunOSS aliyunOSS) {
        String endpoint = aliyunOSS.getEndpoint();
        String accessKeyId = aliyunOSS.getAccessKeyId();
        String accessKeySecret = aliyunOSS.getAccessKeySecret();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        return ossClient;
    }
}
