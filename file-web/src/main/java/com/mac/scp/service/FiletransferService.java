package com.mac.scp.service;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.mac.common.cbb.DateUtil;
import com.mac.common.cbb.RestResult;
import com.mac.common.cbb.Uploader;
import com.mac.common.domain.UploadFile;
import com.mac.scp.api.IFiletransferService;

import com.mac.scp.domain.*;
import com.mac.scp.mapper.FileMapper;
import com.mac.scp.mapper.FiletransferMapper;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;


@Service
public class FiletransferService implements IFiletransferService {

    @Resource
    FiletransferMapper filetransferMapper;
    @Resource
    FileMapper fileMapper;


    @Override
    public void deleteUserImageById(UserImageBean userImageBean) {
        filetransferMapper.deleteUserImageById(userImageBean);

    }


    /**
     * 添加用户头像
     */
    @Override
    public RestResult<String> insertUserImage(UserImageBean userImageBean) {
        RestResult<String> restResult = new RestResult<String>();
        filetransferMapper.insertUserImage(userImageBean);

        restResult.setSuccess(true);
        return restResult;
    }

    /**
     * 查找用户头像
     */
    @Override
    public RestResult<List<UserImageBean>> selectUserImage(long userId) {
        RestResult<List<UserImageBean>> restResult = new RestResult<List<UserImageBean>>();
        List<UserImageBean> result = filetransferMapper.selectUserImage(userId);
        if (result == null) {
            restResult.setSuccess(false);
            restResult.setErrorMessage("没有头像");
        } else {
            restResult.setSuccess(true);
            restResult.setData(result);
        }
        return restResult;
    }

    /**
     * 上传用户头像
     */
    @Override
    public RestResult<String> uploadUserImage(HttpServletRequest request) {
        RestResult<String> restResult = new RestResult<String>();
        UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        UserImageBean userImageBean = new UserImageBean();
        UserBean userBean = null;
        //判断用户是否登陆
        if (sessionUserBean == null) {
            restResult.setErrorCode("用户未登陆");
            restResult.setSuccess(false);
        } else {
            Uploader uploader = null;
            uploader = new Uploader(request);
            List<UploadFile> uploadFile = uploader.upload();

            String imageurl = uploadFile.get(0).getUrl();
            userBean = sessionUserBean;
            userImageBean.setUserid(userBean.getUserId());
            userImageBean.setImageurl(imageurl);
            userImageBean.setUploadtime(DateUtil.getCurrentTime());
            insertUserImage(userImageBean);
            restResult.setData("filetransfer/" + imageurl);
            restResult.setSuccess(true);
        }

        return restResult;
    }

    @Override
    public List<UserImageBean> selectUserImageByUrl(String url) {
        List<UserImageBean> result = filetransferMapper.selectUserImageByUrl(url);
        return result;
    }


    @Override
    public void deleteUserImageByIds(List<Integer> imageidList) {
        filetransferMapper.deleteUserImageByIds(imageidList);
    }




//    public void insertFile(FileBean fileBean){
//        filetransferMapper.insertFile(fileBean);
//    }

    @Override
    public void uploadFile(HttpServletRequest request, FileBean fileBean) {
        Uploader uploader = new Uploader(request);
        List<UploadFile> uploadFileList = uploader.upload();
        for (int i = 0; i < uploadFileList.size(); i++){
            UploadFile uploadFile = uploadFileList.get(i);
            if (uploadFile.getSuccess() == 1){
                fileBean.setFileurl(uploadFile.getUrl());
                fileBean.setFilesize(uploadFile.getFileSize());
                fileBean.setFilename(uploadFile.getFileName());
                fileBean.setExtendname(uploadFile.getFileType());
                fileBean.setTimestampname(uploadFile.getTimeStampName());
                fileBean.setUploadtime(DateUtil.getCurrentTime());

                fileMapper.insertFile(fileBean);
            }


            synchronized (FiletransferService.class) {
                UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();

                long sessionUserId = sessionUserBean.getUserId();
                StorageBean storageBean = selectStorageBean(new StorageBean(sessionUserId));
                if (storageBean == null) {
                    StorageBean storage = new StorageBean(sessionUserId);
                    storage.setStoragesize(fileBean.getFilesize());
                    insertStorageBean(storage);
                } else {
                    storageBean.setStoragesize(storageBean.getStoragesize() + uploadFile.getFileSize());
                    updateStorageBean(storageBean);
                }
            }

        }
    }

    @Override
    public StorageBean selectStorageBean(StorageBean storageBean) {
        return filetransferMapper.selectStorageBean(storageBean);
    }

    @Override
    public void insertStorageBean(StorageBean storageBean) {
        filetransferMapper.insertStorageBean(storageBean);
    }

    @Override
    public void updateStorageBean(StorageBean storageBean) {
        filetransferMapper.updateStorageBean(storageBean);
    }

    @Override
    public StorageBean selectStorageByUser(StorageBean storageBean) {
        return filetransferMapper.selectStorageByUser(storageBean);
    }
}
