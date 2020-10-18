package com.qiwenshare.file.controller;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.qiwenshare.common.operation.FileOperation;
import com.qiwenshare.common.oss.AliyunOSSDownload;
import com.qiwenshare.common.util.PathUtil;
import com.qiwenshare.common.cbb.RestResult;
import com.qiwenshare.common.operation.ImageOperation;
import com.qiwenshare.file.api.IFileService;
import com.qiwenshare.file.api.IFiletransferService;
import com.qiwenshare.file.api.IRemoteUserService;
import com.qiwenshare.file.config.QiwenFileConfig;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.StorageBean;
import com.qiwenshare.file.domain.UserBean;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@RestController
@RequestMapping("/filetransfer")
public class FiletransferController {

    @Resource
    IFiletransferService filetransferService;

    @Resource
    FileController fileController;

    @Autowired
    IRemoteUserService remoteUserService;
    @Autowired
    QiwenFileConfig qiwenFileConfig;
    @Resource
    IFileService fileService;

    /**
     * 上传文件
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/uploadfile", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFile(HttpServletRequest request, FileBean fileBean, @RequestHeader("token") String token) {
        RestResult<String> restResult = new RestResult<String>();
        //UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        UserBean sessionUserBean = new UserBean();
        boolean isRemoteLogin = qiwenFileConfig.isRemoteLogin();
        if (isRemoteLogin) {

            RestResult<UserBean> restUserBean = remoteUserService.checkUserLoginInfo(token);
            sessionUserBean = restUserBean.getData();
        } else {
            sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        }
        if (sessionUserBean == null){
            restResult.setSuccess(false);
            restResult.setErrorMessage("未登录");
            return JSON.toJSONString(restResult);
        }
        RestResult<String> operationCheckResult = fileController.operationCheck(token);
        if (!operationCheckResult.isSuccess()){
            return JSON.toJSONString(operationCheckResult);
        }

        fileBean.setUserId(sessionUserBean.getUserId());

        filetransferService.uploadFile(request, fileBean, sessionUserBean);

        restResult.setSuccess(true);
        String resultJson = JSON.toJSONString(restResult);
        return resultJson;
    }
    /**
     * 下载文件
     *
     * @return
     */
    @RequestMapping(value = "/downloadfile", method = RequestMethod.GET)
    public String downloadFile(HttpServletResponse response, FileBean fileBean) {
        RestResult<String> restResult = new RestResult<>();
        String fileName = null;// 文件名
        try {
            fileName = new String(fileBean.getFileName().getBytes("utf-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        fileName = fileName + "." + fileBean.getExtendName();
        response.setContentType("application/force-download");// 设置强制下载不打开
        response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
        byte[] buffer = new byte[1024];
        BufferedInputStream bis = null;
        FileBean fileBean1 = fileService.getById(fileBean.getFileId());
        if (fileBean1.getIsOSS() != null && fileBean1.getIsOSS() == 1) {

            AliyunOSSDownload aliyunOSSDownload= new AliyunOSSDownload();
            OSS ossClient = aliyunOSSDownload.createOSSClient(qiwenFileConfig.getAliyun().getOss());
            OSSObject ossObject = ossClient.getObject(qiwenFileConfig.getAliyun().getOss().getBucketName(), fileBean1.getTimeStampName());
            InputStream inputStream = ossObject.getObjectContent();
            try {
                bis = new BufferedInputStream(inputStream);
                OutputStream os = response.getOutputStream();
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
        } else {
            //设置文件路径
            File file = FileOperation.newFile(PathUtil.getStaticPath() + fileBean.getFileUrl());
            if (file.exists()) {


                FileInputStream fis = null;

                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }

                } catch (Exception e) {
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
            }

        }
        return null;

    }


    /**
     * 获取存储信息
     *
     * @return
     */
    @RequestMapping(value = "/getstorage", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<StorageBean> getStorage(@RequestHeader("token") String token) {
        RestResult<StorageBean> restResult = new RestResult<StorageBean>();
        //UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        UserBean sessionUserBean = new UserBean();
        boolean isRemoteLogin = qiwenFileConfig.isRemoteLogin();
        if (isRemoteLogin) {

            RestResult<UserBean> restUserBean = remoteUserService.checkUserLoginInfo(token);
            sessionUserBean = restUserBean.getData();
        } else {
            sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        }
        StorageBean storageBean = new StorageBean();
        if (qiwenFileConfig.isShareMode()){
            storageBean.setUserId(2L);
        }else{
            storageBean.setUserId(sessionUserBean.getUserId());
        }

        StorageBean storage = filetransferService.selectStorageByUser(storageBean);
        restResult.setData(storage);
        restResult.setSuccess(true);
        return restResult;
    }



}
