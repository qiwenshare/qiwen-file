package com.qiwenshare.file.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiwenshare.common.cbb.DateUtil;
import com.qiwenshare.common.operation.FileOperation;
import com.qiwenshare.common.oss.AliyunOSSDelete;
import com.qiwenshare.common.util.FileUtil;
import com.qiwenshare.common.util.PathUtil;
import com.qiwenshare.file.api.IFileService;
import com.qiwenshare.file.config.AliyunConfig;
import com.qiwenshare.file.config.QiwenFileConfig;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.mapper.FileMapper;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.StorageBean;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.mapper.UserFileMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class FileService extends ServiceImpl<FileMapper, FileBean> implements IFileService {

    @Resource
    FileMapper fileMapper;
    @Resource
    UserFileMapper userFileMapper;
    @Resource
    FiletransferService filetransferService;
    @Resource
    QiwenFileConfig qiwenFileConfig;

//    @Override
//    public void batchInsertFile(List<FileBean> fileBeanList, Long userId) {
//        StorageBean storageBean = filetransferService.selectStorageBean(new StorageBean(userId));
//        long fileSizeSum = 0;
//        for (FileBean fileBean : fileBeanList) {
//            if (fileBean.getIsDir() == 0) {
//                fileSizeSum += fileBean.getFileSize();
//            }
//        }
//        fileMapper.batchInsertFile(fileBeanList);
//        if (storageBean != null) {
//            long updateFileSize = storageBean.getStorageSize() + fileSizeSum;
//
//            storageBean.setStorageSize(updateFileSize);
//            filetransferService.updateStorageBean(storageBean);
//        }
//    }

    @Override
    public void increaseFilePointCount(Long fileId) {
        FileBean fileBean = fileMapper.selectById(fileId);
        fileBean.setPointCount(fileBean.getPointCount()+1);
        fileMapper.updateById(fileBean);
    }

    @Override
    public void decreaseFilePointCount(Long fileId) {
        FileBean fileBean = fileMapper.selectById(fileId);
        fileBean.setPointCount(fileBean.getPointCount()-1);
        fileMapper.updateById(fileBean);
    }

//    @Override
//    public void updateFile(FileBean fileBean) {
//        fileBean.setUploadTime(DateUtil.getCurrentTime());
//        fileMapper.updateFile(fileBean);
//    }




//    @Override
//    public List<FileBean> selectFileListByPath(FileBean fileBean) {
//        LambdaQueryWrapper<FileBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(FileBean::getFilePath, fileBean.getFilePath())
//                .eq(FileBean::getUserId, fileBean.getUserId())
//                .orderByDesc(FileBean::getIsDir);
//        return fileMapper.selectList(lambdaQueryWrapper);
//    }
    @Override
    public void deleteLocalFile(FileBean fileBean) {
        log.info("删除本地文件：" + JSON.toJSONString(fileBean));
        //删除服务器文件
        if (fileBean.getFileUrl() != null && fileBean.getFileUrl().indexOf("upload") != -1){
            if (fileBean.getIsOSS() != null && fileBean.getIsOSS() == 1) {
                AliyunOSSDelete.deleteObject(qiwenFileConfig.getAliyun().getOss(), fileBean.getFileUrl().substring(1));
            } else {
                FileOperation.deleteFile(PathUtil.getStaticPath() + fileBean.getFileUrl());
                if (FileUtil.isImageFile(FileUtil.getFileType(fileBean.getFileUrl()))) {
                    FileOperation.deleteFile(PathUtil.getStaticPath() + fileBean.getFileUrl().replace(fileBean.getTimeStampName(), fileBean.getTimeStampName() + "_min"));
                }
            }
        }
    }






}
