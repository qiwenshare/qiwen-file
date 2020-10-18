/**
 * 示例说明
 * 
 * HelloOSS是OSS Java SDK的示例程序，您可以修改endpoint、accessKeyId、accessKeySecret、bucketName后直接运行。
 * 运行方法请参考README。
 * 
 * 本示例中的并不包括OSS Java SDK的所有功能，详细功能及使用方法，请参看“SDK手册 > Java-SDK”，
 * 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/preface.html?spm=5176.docoss/sdk/java-sdk/。
 * 
 * 调用OSS Java SDK的方法时，抛出异常表示有错误发生；没有抛出异常表示成功执行。
 * 当错误发生时，OSS Java SDK的方法会抛出异常，异常中包括错误码、错误信息，详细请参看“SDK手册 > Java-SDK > 异常处理”，
 * 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/exception.html?spm=5176.docoss/api-reference/error-response。
 * 
 * OSS控制台可以直观的看到您调用OSS Java SDK的结果，OSS控制台地址是：https://oss.console.aliyun.com/index#/。
 * OSS控制台使用方法请参看文档中心的“控制台用户指南”， 指南的来链接地址是：https://help.aliyun.com/document_detail/oss/getting-started/get-started.html?spm=5176.docoss/user_guide。
 * 
 * OSS的文档中心地址是：https://help.aliyun.com/document_detail/oss/user_guide/overview.html。
 * OSS Java SDK的文档地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/install.html?spm=5176.docoss/sdk/java-sdk。
 * 
 */

package com.qiwenshare.common.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.qiwenshare.common.domain.AliyunOSS;
import com.qiwenshare.common.util.FileUtil;

import java.io.InputStream;


public class AliyunOSSDelete {


    /**
     * 流式上传
     */
    public static void deleteObject(AliyunOSS aliyunOSS, String objectName) {
        String endpoint = aliyunOSS.getEndpoint();
        String accessKeyId = aliyunOSS.getAccessKeyId();
        String accessKeySecret = aliyunOSS.getAccessKeySecret();
        String bucketName = aliyunOSS.getBucketName();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        ossClient.deleteObject(bucketName, objectName);



        // 关闭OSSClient。
        ossClient.shutdown();
    }


//
//    public static void main(String[] args) {
//
//
//
//        // 生成OSSClient，您可以指定一些参数，详见“SDK手册 > Java-SDK > 初始化”，
//        // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/init.html?spm=5176.docoss/sdk/java-sdk/get-start
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//        try {
//
//            // 判断Bucket是否存在。详细请参看“SDK手册 > Java-SDK > 管理Bucket”。
//            // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/manage_bucket.html?spm=5176.docoss/sdk/java-sdk/init
//            if (ossClient.doesBucketExist(bucketName)) {
//                System.out.println("您已经创建Bucket：" + bucketName + "。");
//            } else {
//                System.out.println("您的Bucket不存在，创建Bucket：" + bucketName + "。");
//                // 创建Bucket。详细请参看“SDK手册 > Java-SDK > 管理Bucket”。
//                // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/manage_bucket.html?spm=5176.docoss/sdk/java-sdk/init
//                ossClient.createBucket(bucketName);
//            }
//
//            // 查看Bucket信息。详细请参看“SDK手册 > Java-SDK > 管理Bucket”。
//            // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/manage_bucket.html?spm=5176.docoss/sdk/java-sdk/init
//            BucketInfo info = ossClient.getBucketInfo(bucketName);
//            System.out.println("Bucket " + bucketName + "的信息如下：");
//            System.out.println("\t数据中心：" + info.getBucket().getLocation());
//            System.out.println("\t创建时间：" + info.getBucket().getCreationDate());
//            System.out.println("\t用户标志：" + info.getBucket().getOwner());
//
//            // 把字符串存入OSS，Object的名称为firstKey。详细请参看“SDK手册 > Java-SDK > 上传文件”。
//            // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/upload_object.html?spm=5176.docoss/user_guide/upload_object
//            InputStream is = new ByteArrayInputStream("Hello OSS".getBytes());
//            ossClient.putObject(bucketName, firstKey, is);
//            System.out.println("Object：" + firstKey + "存入OSS成功。");
//
//            // 下载文件。详细请参看“SDK手册 > Java-SDK > 下载文件”。
//            // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/download_object.html?spm=5176.docoss/sdk/java-sdk/manage_object
//            OSSObject ossObject = ossClient.getObject(bucketName, firstKey);
//            InputStream inputStream = ossObject.getObjectContent();
//            StringBuilder objectContent = new StringBuilder();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//            while (true) {
//                String line = reader.readLine();
//                if (line == null)
//                    break;
//                objectContent.append(line);
//            }
//            inputStream.close();
//            System.out.println("Object：" + firstKey + "的内容是：" + objectContent);
//
//            // 文件存储入OSS，Object的名称为fileKey。详细请参看“SDK手册 > Java-SDK > 上传文件”。
//            // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/upload_object.html?spm=5176.docoss/user_guide/upload_object
//            String fileKey = "README.md";
//            ossClient.putObject(bucketName, fileKey, new File("README.md"));
//            System.out.println("Object：" + fileKey + "存入OSS成功。");
//
//            // 查看Bucket中的Object。详细请参看“SDK手册 > Java-SDK > 管理文件”。
//            // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/manage_object.html?spm=5176.docoss/sdk/java-sdk/manage_bucket
//            ObjectListing objectListing = ossClient.listObjects(bucketName);
//            List<OSSObjectSummary> objectSummary = objectListing.getObjectSummaries();
//            System.out.println("您有以下Object：");
//            for (OSSObjectSummary object : objectSummary) {
//                System.out.println("\t" + object.getKey());
//            }
//
//            // 删除Object。详细请参看“SDK手册 > Java-SDK > 管理文件”。
//            // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/manage_object.html?spm=5176.docoss/sdk/java-sdk/manage_bucket
//            ossClient.deleteObject(bucketName, firstKey);
//            System.out.println("删除Object：" + firstKey + "成功。");
//            ossClient.deleteObject(bucketName, fileKey);
//            System.out.println("删除Object：" + fileKey + "成功。");
//
//        } catch (OSSException oe) {
//            oe.printStackTrace();
//        } catch (ClientException ce) {
//            ce.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            ossClient.shutdown();
//        }
//
//    }

}
