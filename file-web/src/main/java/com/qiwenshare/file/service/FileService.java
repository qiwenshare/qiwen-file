package com.qiwenshare.file.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.qiwenshare.common.util.IDUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.qiwenshare.common.cbb.DateUtil;
import com.qiwenshare.common.operation.FileOperation;
import com.qiwenshare.common.oss.AliyunOSSDelete;
import com.qiwenshare.common.util.PathUtils;
import com.qiwenshare.file.config.QiwenFileConfig;
import com.qiwenshare.file.dao.FileDao;
import com.qiwenshare.file.dao.entity.File;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.StorageBean;
import com.qiwenshare.file.domain.UserBean;


@Service
public class FileService {

    @Resource
    FiletransferService filetransferService;
    @Resource
    QiwenFileConfig qiwenFileConfig;
    @Resource
    FileDao fileDao;

    
    public void insertFile(FileBean fileBean) {
        File file = new File();
        BeanUtils.copyProperties(fileBean,file);
        file.setFileId(IDUtils.nextId());
        fileDao.save(file);
    }

    
    public void batchInsertFile(List<FileBean> fileBeanList) {
        UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        StorageBean storageBean = filetransferService.selectStorageBean(new StorageBean(sessionUserBean.getUserId()));
        long fileSizeSum = 0;
        for (FileBean fileBean : fileBeanList) {
            if (fileBean.getIsDir() == 0) {
                fileSizeSum += fileBean.getFileSize();
            }
        }
        if (!CollectionUtils.isEmpty(fileBeanList)) {
            List<File> fileList = new ArrayList<>();
            fileBeanList.forEach(item->{
                File file = new File();
                BeanUtils.copyProperties(item,file);
                fileList.add(file);
            });
            fileDao.saveBatch(fileList);
        }

        if (storageBean != null) {
            long updateFileSize = storageBean.getStorageSize() + fileSizeSum;

            storageBean.setStorageSize(updateFileSize);
            filetransferService.updateStorageBean(storageBean);
        }
    }

    
    public void updateFile(FileBean fileBean) {
        fileBean.setUploadTime(DateUtil.getCurrentTime());
        File file = new File();
        BeanUtils.copyProperties(fileBean,file);
        fileDao.edit(file);
    }

    
    public List<FileBean> selectFileByNameAndPath(FileBean fileBean) {
        List<FileBean> fileBeanList = new ArrayList<>();
        List<File> fileList = fileDao.getListByNameAndPath(fileBean.getFileName(), fileBean.getFilePath());
        if (!CollectionUtils.isEmpty(fileList)) {
            fileList.forEach(item->{
                FileBean fileBeanTemp = new FileBean();
                BeanUtils.copyProperties(item,fileBeanTemp);
                fileBeanList.add(fileBeanTemp);
            });
        }
        return fileBeanList;
    }

    
    public FileBean selectFileById(FileBean fileBean) {
        File file = fileDao.getOne(fileBean.getFileId());
        if (file == null) {
            return null;
        }
        BeanUtils.copyProperties(file, fileBean);
        return fileBean;
    }

    
    public List<FileBean> selectFilePathTreeByUserId(FileBean fileBean) {
        List<FileBean> fileBeanList = new ArrayList<>();
        List<File> fileList = fileDao.getListByUserIdAndIsDir(fileBean.getUserId());
        if (!CollectionUtils.isEmpty(fileList)) {
            fileList.forEach(item->{
                FileBean fileBeanTemp = new FileBean();
                BeanUtils.copyProperties(item,fileBeanTemp);
                fileBeanList.add(fileBeanTemp);
            });
        }
        return fileBeanList;
    }

    
    public List<FileBean> selectFileList(FileBean fileBean) {
        List<FileBean> fileBeanList = new ArrayList<>();
        String filePath = fileBean.getFilePath();
        Long userId = fileBean.getUserId();
        List<File> fileList = fileDao.getListByFilePathAndUserId(filePath, userId);
        if (!CollectionUtils.isEmpty(fileList)) {
            fileList.forEach(item->{
                FileBean fileBeanTemp = new FileBean();
                BeanUtils.copyProperties(item,fileBeanTemp);
                fileBeanList.add(fileBeanTemp);
            });
        }
        return fileBeanList;
    }

    
    public List<FileBean> selectFileListByIds(List<Long> fileIdList) {
        List<FileBean> fileBeanList = new ArrayList<>();
        List<File> fileList = fileDao.getListByIdList(fileIdList);
        if (!CollectionUtils.isEmpty(fileList)) {
            fileList.forEach(item->{
                FileBean fileBeanTemp = new FileBean();
                BeanUtils.copyProperties(item,fileBeanTemp);
                fileBeanList.add(fileBeanTemp);
            });
        }
        return fileBeanList;
    }

    
    public List<FileBean> selectFileTreeListLikeFilePath(String filePath) {
        FileBean fileBean = new FileBean();
        filePath = filePath.replace("\\", "\\\\\\\\");
        filePath = filePath.replace("'", "\\'");
        filePath = filePath.replace("%", "\\%");
        filePath = filePath.replace("_", "\\_");

        fileBean.setFilePath(filePath);
        List<FileBean> fileBeanList = new ArrayList<>();
        List<File> fileList = fileDao.getListByFilePathLike(fileBean.getFilePath());
        if (!CollectionUtils.isEmpty(fileList)) {
            fileList.forEach(item->{
                FileBean fileBeanTemp = new FileBean();
                BeanUtils.copyProperties(item,fileBeanTemp);
                fileBeanList.add(fileBeanTemp);
            });
        }
        return fileBeanList;
    }

    
    public void deleteFile(FileBean fileBean, UserBean sessionUserBean) {
        //UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        StorageBean storageBean = filetransferService.selectStorageBean(new StorageBean(sessionUserBean.getUserId()));
        long deleteSize = 0;
        String fileUrl = PathUtils.getStaticPath() + fileBean.getFileUrl();
        if (fileBean.getIsDir() == 1) {
            //1、先删除子目录
            String filePath = fileBean.getFilePath() + fileBean.getFileName() + "/";
            List<FileBean> fileList = selectFileTreeListLikeFilePath(filePath);

            for (int i = 0; i < fileList.size(); i++){
                FileBean file = fileList.get(i);
                //1.1、删除数据库文件
                fileDao.removeById(file.getFileId());
                //1.2、如果是文件，需要记录文件大小
                if (file.getIsDir() != 1){
                    deleteSize += file.getFileSize();
                    //1.3、删除服务器文件，只删除文件，目录是虚拟的
                    if (file.getFileUrl() != null && file.getFileUrl().indexOf("upload") != -1){
                        if (file.getIsOSS() == 1) {
                            AliyunOSSDelete.deleteObject(qiwenFileConfig.getAliyun().getOss(), file.getFileUrl().substring(1));
                        } else {
                            FileOperation.deleteFile(PathUtils.getStaticPath() + file.getFileUrl());
                        }

                    }
                }
            }
            //2、根目录单独删除
            fileDao.removeById(fileBean.getFileId());
        }else{
            fileDao.removeById(fileBean.getFileId());
            deleteSize = FileOperation.getFileSize(fileUrl);
            //删除服务器文件
            if (fileBean.getFileUrl() != null && fileBean.getFileUrl().indexOf("upload") != -1){
                if (fileBean.getIsOSS() == 1) {
                    AliyunOSSDelete.deleteObject(qiwenFileConfig.getAliyun().getOss(), fileBean.getFileUrl().substring(1));
                } else {
                    FileOperation.deleteFile(fileUrl);
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


    public void deleteFileByIds(List<Long> fileIdList) {
        fileDao.removeByIdList(fileIdList);
    }

    public void updateFilepathByFilepath(String oldfilePath, String newfilePath, String fileName, String extendName) {
        if ("null".equals(extendName)){
            extendName = null;
        }
        //移动根目录
        fileDao.editFilepathByPathAndName(oldfilePath, newfilePath, fileName, extendName);

        //移动子目录
        oldfilePath = oldfilePath + fileName + "/";
        newfilePath = newfilePath + fileName + "/";

        oldfilePath = oldfilePath.replace("\\", "\\\\\\\\");
        oldfilePath = oldfilePath.replace("'", "\\'");
        oldfilePath = oldfilePath.replace("%", "\\%");
        oldfilePath = oldfilePath.replace("_", "\\_");

        if (extendName == null) { //为null说明是目录，则需要移动子目录
            fileDao.editFilePathByFilePath(oldfilePath, newfilePath);
        }

    }

    
    public List<FileBean> selectFileByExtendName(List<String> fileNameList, long userId) {
        List<FileBean> fileBeanList = new ArrayList<>();
        List<File> fileList = fileDao.getListByUserIdExtendNameList(userId, fileNameList);
        if (!CollectionUtils.isEmpty(fileList)) {
            fileList.forEach(item->{
                FileBean fileBeanTemp = new FileBean();
                BeanUtils.copyProperties(item,fileBeanTemp);
                fileBeanList.add(fileBeanTemp);
            });
        }
        return fileBeanList;
    }
}
