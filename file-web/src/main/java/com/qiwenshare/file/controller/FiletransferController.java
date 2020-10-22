package com.qiwenshare.file.controller;

import java.io.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.qiwenshare.file.domain.RestResult;
import com.qiwenshare.file.util.FileUtils;
import com.qiwenshare.file.util.PathUtils;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.StorageBean;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.service.FileService;
import com.qiwenshare.file.service.FiletransferService;

@RestController
@RequestMapping("/filetransfer")
public class FiletransferController extends BaseController{

    @Autowired
    FiletransferService filetransferService;
    @Autowired
    FileService fileService;

    /**
     * @author dehui dou
     * @description 上传文件
     * @param request
     * @param fileBean
     * @param token
     * @return java.lang.String
     */
    @RequestMapping(value = "/uploadfile", method = RequestMethod.POST)
    public String uploadFile(HttpServletRequest request, FileBean fileBean, @RequestHeader("token") String token) {
        RestResult<String> restResult = new RestResult<String>();
        UserBean sessionUserBean = getLoginUserInfo();
        fileBean.setUserId(sessionUserBean.getUserId());
        filetransferService.uploadFile(request, fileBean, sessionUserBean);
        restResult.setSuccess(true);
        String resultJson = JSON.toJSONString(restResult);
        return resultJson;
    }

    /**
     * @author dehui dou
     * @description 下载文件
     * @param response
     * @param fileBean
     * @return java.lang.String
     */
    @RequestMapping(value = "/downloadfile", method = RequestMethod.GET)
    public String downloadFile(HttpServletResponse response, FileBean fileBean) {
        String fileName = null;
        try {
            fileName = new String(fileBean.getFileName().getBytes("utf-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        fileName = fileName + "." + fileBean.getExtendName();
        response.setContentType("application/force-download");
        response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
        byte[] buffer = new byte[1024];
        BufferedInputStream bis = null;
        // todo 抽象出文件存储服务器
        File file = FileUtils.newFile(PathUtils.getStaticPath() + fileBean.getFileUrl());
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
        return null;

    }

    /**
     * @author dehui dou
     * @description 获取存储信息
     * @param token
     * @return com.qiwenshare.common.cbb.RestResult<com.qiwenshare.file.domain.StorageBean>
     */
    @RequestMapping(value = "/getstorage", method = RequestMethod.GET)
    public RestResult<StorageBean> getStorage(@RequestHeader("token") String token) {
        RestResult<StorageBean> restResult = new RestResult<StorageBean>();
        UserBean sessionUserBean = getLoginUserInfo();
        StorageBean storageBean = new StorageBean();
        storageBean.setUserId(sessionUserBean.getUserId());
        StorageBean storage = filetransferService.selectStorageBean(storageBean);
        restResult.setData(storage);
        restResult.setSuccess(true);
        return restResult;
    }

}
