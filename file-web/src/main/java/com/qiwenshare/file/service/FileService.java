package com.qiwenshare.file.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiwenshare.common.cbb.DateUtil;
import com.qiwenshare.common.operation.FileOperation;
import com.qiwenshare.common.oss.AliyunOSSDelete;
import com.qiwenshare.common.util.FileUtil;
import com.qiwenshare.common.util.PathUtil;
import com.qiwenshare.file.api.IFileService;
import com.qiwenshare.file.config.AliyunConfig;
import com.qiwenshare.file.config.QiwenFileConfig;
import com.qiwenshare.file.mapper.FileMapper;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.StorageBean;
import com.qiwenshare.file.domain.UserBean;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;


@Service
public class FileService extends ServiceImpl<FileMapper, FileBean> implements IFileService {

    @Resource
    FileMapper fileMapper;
    @Resource
    FiletransferService filetransferService;
    @Resource
    QiwenFileConfig qiwenFileConfig;

    @Override
    public void batchInsertFile(List<FileBean> fileBeanList, Long userId) {
        StorageBean storageBean = filetransferService.selectStorageBean(new StorageBean(userId));
        long fileSizeSum = 0;
        for (FileBean fileBean : fileBeanList) {
            if (fileBean.getIsDir() == 0) {
                fileSizeSum += fileBean.getFileSize();
            }
        }
        fileMapper.batchInsertFile(fileBeanList);
        if (storageBean != null) {
            long updateFileSize = storageBean.getStorageSize() + fileSizeSum;

            storageBean.setStorageSize(updateFileSize);
            filetransferService.updateStorageBean(storageBean);
        }
    }

    @Override
    public void updateFile(FileBean fileBean) {
        fileBean.setUploadTime(DateUtil.getCurrentTime());
        fileMapper.updateFile(fileBean);
    }

    @Override
    public List<FileBean> selectFileByNameAndPath(FileBean fileBean) {
        LambdaQueryWrapper<FileBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileBean::getFileName, fileBean.getFileName()).eq(FileBean::getFilePath, fileBean.getFilePath());
        return fileMapper.selectList(lambdaQueryWrapper);
//        return fileMapper.selectFileByNameAndPath(fileBean);
    }


    @Override
    public List<FileBean> selectFilePathTreeByUserId(FileBean fileBean) {
        LambdaQueryWrapper<FileBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FileBean::getUserId, fileBean.getUserId()).eq(FileBean::getIsDir, 1);
        return fileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<FileBean> selectFileList(FileBean fileBean) {
        return fileMapper.selectFileList(fileBean);
    }


    @Override
    public List<FileBean> selectFileTreeListLikeFilePath(String filePath) {
        FileBean fileBean = new FileBean();
        filePath = filePath.replace("\\", "\\\\\\\\");
        filePath = filePath.replace("'", "\\'");
        filePath = filePath.replace("%", "\\%");
        filePath = filePath.replace("_", "\\_");

        fileBean.setFilePath(filePath);

        LambdaQueryWrapper<FileBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.likeLeft(FileBean::getFilePath, filePath);
        return fileMapper.selectList(lambdaQueryWrapper);
//        return fileMapper.selectFileTreeListLikeFilePath(fileBean);
    }

    @Override
    public void deleteFile(FileBean fileBean, UserBean sessionUserBean) {
        StorageBean storageBean = filetransferService.selectStorageBean(new StorageBean(sessionUserBean.getUserId()));
        long deleteSize = 0;
        String fileUrl = PathUtil.getStaticPath() + fileBean.getFileUrl();
        if (fileBean.getIsDir() == 1) {
            //1、先删除子目录
            String filePath = fileBean.getFilePath() + fileBean.getFileName() + "/";
            List<FileBean> fileList = selectFileTreeListLikeFilePath(filePath);

            for (int i = 0; i < fileList.size(); i++){
                FileBean file = fileList.get(i);
                //1.1、删除数据库文件
                fileMapper.deleteById(file.getFileId());
                //1.2、如果是文件，需要记录文件大小
                if (file.getIsDir() != 1){
                    deleteSize += file.getFileSize();
                    //1.3、删除服务器文件，只删除文件，目录是虚拟的
                    if (file.getFileUrl() != null && file.getFileUrl().indexOf("upload") != -1){
                        if (file.getIsOSS() != null && file.getIsOSS() == 1) {
                            AliyunOSSDelete.deleteObject(qiwenFileConfig.getAliyun().getOss(), file.getFileUrl().substring(1));
                        } else {
                            FileOperation.deleteFile(PathUtil.getStaticPath() + file.getFileUrl());
                            if (FileUtil.isImageFile(file.getExtendName())) {
                                FileOperation.deleteFile(PathUtil.getStaticPath() + file.getFileUrl().replace(file.getTimeStampName(), file.getTimeStampName() + "_min"));
                            }
                        }

                    }
                }
            }
            //2、根目录单独删除
            fileMapper.deleteById(fileBean.getFileId());
        }else{
            fileMapper.deleteById(fileBean.getFileId());
            deleteSize = FileOperation.getFileSize(fileUrl);
            if (deleteSize == 0) {
                deleteSize = fileBean.getFileSize();
            }
            //删除服务器文件
            if (fileBean.getFileUrl() != null && fileBean.getFileUrl().indexOf("upload") != -1){
                if (fileBean.getIsOSS() != null && fileBean.getIsOSS() == 1) {
                    AliyunOSSDelete.deleteObject(qiwenFileConfig.getAliyun().getOss(), fileBean.getFileUrl().substring(1));
                } else {
                    FileOperation.deleteFile(fileUrl);
                    if (FileUtil.isImageFile(fileBean.getExtendName())) {
                        FileOperation.deleteFile(PathUtil.getStaticPath() + fileBean.getFileUrl().replace(fileBean.getTimeStampName(), fileBean.getTimeStampName() + "_min"));
                    }
                }
            }
        }

        if (storageBean != null) {
            long updateFileSize = storageBean.getStorageSize() - deleteSize;
            if (updateFileSize < 0) {
                updateFileSize = 0;
            }
            storageBean.setStorageSize(updateFileSize);
            filetransferService.updateStorageBean(storageBean);
        }
    }


    @Override
    public void updateFilepathByFilepath(String oldfilePath, String newfilePath, String fileName, String extendName) {
        if ("null".equals(extendName)){
            extendName = null;
        }
        //移动根目录
        fileMapper.updateFilepathByPathAndName(oldfilePath, newfilePath, fileName, extendName);

        //移动子目录
        oldfilePath = oldfilePath + fileName + "/";
        newfilePath = newfilePath + fileName + "/";

        oldfilePath = oldfilePath.replace("\\", "\\\\\\\\");
        oldfilePath = oldfilePath.replace("'", "\\'");
        oldfilePath = oldfilePath.replace("%", "\\%");
        oldfilePath = oldfilePath.replace("_", "\\_");

        if (extendName == null) { //为null说明是目录，则需要移动子目录
            fileMapper.updateFilepathByFilepath(oldfilePath, newfilePath);
        }

    }

    @Override
    public List<FileBean> selectFileByExtendName(List<String> fileNameList, long userId) {
        LambdaQueryWrapper<FileBean> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(FileBean::getExtendName, fileNameList).eq(FileBean::getUserId, userId);
        List<FileBean> fileBeans = fileMapper.selectList(wrapper);
        return fileBeans;
        //return fileMapper.selectFileByExtendName(fileNameList, userId);
    }

    @Override
    public List<FileBean> selectFileNotInExtendNames(List<String> fileNameList, long userId) {
        LambdaQueryWrapper<FileBean> wrapper = new LambdaQueryWrapper<>();
        wrapper.notIn(FileBean::getExtendName, fileNameList).eq(FileBean::getUserId, userId);
        List<FileBean> fileBeans = fileMapper.selectList(wrapper);
        return fileBeans;
    }
}
