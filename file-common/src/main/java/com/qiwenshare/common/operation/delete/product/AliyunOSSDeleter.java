package com.qiwenshare.common.operation.delete.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.qiwenshare.common.config.QiwenFileConfig;
import com.qiwenshare.common.operation.delete.Deleter;
import com.qiwenshare.common.operation.delete.domain.DeleteFile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AliyunOSSDeleter extends Deleter {
    @Resource
    QiwenFileConfig qiwenFileConfig;
    @Override
    public void delete(DeleteFile deleteFile) {
        String endpoint = qiwenFileConfig.getAliyun().getOss().getEndpoint();
        String accessKeyId = qiwenFileConfig.getAliyun().getOss().getAccessKeyId();
        String accessKeySecret = qiwenFileConfig.getAliyun().getOss().getAccessKeySecret();
        String bucketName = qiwenFileConfig.getAliyun().getOss().getBucketName();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        ossClient.deleteObject(bucketName, deleteFile.getFileUrl().substring(1));



        // 关闭OSSClient。
        ossClient.shutdown();
    }
}
