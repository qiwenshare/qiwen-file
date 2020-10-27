package com.qiwenshare.file.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiwenshare.common.cbb.DateUtil;
import com.qiwenshare.common.operation.FileOperation;
import com.qiwenshare.common.oss.AliyunOSSDelete;
import com.qiwenshare.common.util.PathUtil;
import com.qiwenshare.file.api.IFileService;
import com.qiwenshare.file.config.AliyunConfig;
import com.qiwenshare.file.config.QiwenFileConfig;
import com.qiwenshare.file.mapper.FileMapper;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.StorageBean;
import com.qiwenshare.file.domain.UserBean;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    public void insertFile(FileBean fileBean) {
        fileMapper.insertFile(fileBean);
    }

    @Override
    public void batchInsertFile(List<FileBean> fileBeanList) {
        UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        StorageBean storageBean = filetransferService.selectStorageBean(new StorageBean(sessionUserBean.getUserId()));
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
        return fileMapper.selectFileByNameAndPath(fileBean);
    }

    @Override
    public FileBean selectFileById(FileBean fileBean) {
        return fileMapper.selectFileById(fileBean);
    }

    @Override
    public List<FileBean> selectFilePathTreeByUserId(FileBean fileBean) {
        return fileMapper.selectFilePathTreeByUserId(fileBean);
    }

    @Override
    public List<FileBean> selectFileList(FileBean fileBean) {
        return fileMapper.selectFileList(fileBean);
    }

    @Override
    public List<FileBean> selectFileListByIds(List<Integer> fileIdList) {
        return fileMapper.selectFileListByIds(fileIdList);
    }

    @Override
    public List<FileBean> selectFileTreeListLikeFilePath(String filePath) {
        FileBean fileBean = new FileBean();
        filePath = filePath.replace("\\", "\\\\\\\\");
        filePath = filePath.replace("'", "\\'");
        filePath = filePath.replace("%", "\\%");
        filePath = filePath.replace("_", "\\_");

        fileBean.setFilePath(filePath);

        return fileMapper.selectFileTreeListLikeFilePath(fileBean);
    }

    @Override
    public void deleteFile(FileBean fileBean, UserBean sessionUserBean) {
        //UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
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
                fileMapper.deleteFileById(file);
                //1.2、如果是文件，需要记录文件大小
                if (file.getIsDir() != 1){
                    deleteSize += file.getFileSize();
                    //1.3、删除服务器文件，只删除文件，目录是虚拟的
                    if (file.getFileUrl() != null && file.getFileUrl().indexOf("upload") != -1){
                        if (file.getIsOSS() == 1) {
                            AliyunOSSDelete.deleteObject(qiwenFileConfig.getAliyun().getOss(), file.getFileUrl().substring(1));
                        } else {
                            FileOperation.deleteFile(PathUtil.getStaticPath() + file.getFileUrl());
                        }

                    }
                }
            }
            //2、根目录单独删除
            fileMapper.deleteFileById(fileBean);
        }else{
            fileMapper.deleteFileById(fileBean);
            deleteSize = FileOperation.getFileSize(fileUrl);
            if (deleteSize == 0) {
                deleteSize = fileBean.getFileSize();
            }
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

    @Override
    public void deleteFileByIds(List<Integer> fileIdList) {
        fileMapper.deleteFileByIds(fileIdList);
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
        return fileMapper.selectFileByExtendName(fileNameList, userId);
    }
}
