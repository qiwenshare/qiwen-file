package com.mac.scp.service;

import com.mac.common.cbb.DateUtil;
import com.mac.common.operation.FileOperation;
import com.mac.common.util.PathUtil;
import com.mac.scp.api.IFileService;
import com.mac.scp.domain.*;
import com.mac.scp.mapper.FileMapper;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class FileService implements IFileService {

    @Resource
    FileMapper fileMapper;
    @Resource
    FiletransferService filetransferService;

    @Override
    public void insertFile(FileBean fileBean) {
        fileMapper.insertFile(fileBean);
    }

    @Override
    public void batchInsertFile(List<FileBean> fileBeanList) {
        fileMapper.batchInsertFile(fileBeanList);
    }

    @Override
    public void updateFile(FileBean fileBean) {
        fileBean.setUploadtime(DateUtil.getCurrentTime());
        fileMapper.updateFile(fileBean);
    }

    @Override
    public FileBean selectFileById(FileBean fileBean) {
        return fileMapper.selectFileById(fileBean);
    }

    @Override
    public List<FileBean> selectFilePathTreeByUserid(FileBean fileBean) {
        return fileMapper.selectFilePathTreeByUserid(fileBean);
    }

    @Override
    public List<FileBean> selectFileList(FileBean fileBean) {
        return fileMapper.selectFileList(fileBean);
    }

    @Override
    public List<FileBean> selectFileListByIds(List<Integer> fileidList) {
        return fileMapper.selectFileListByIds(fileidList);
    }

    @Override
    public List<FileBean> selectFileTreeListLikeFilePath(String filePath) {
        FileBean fileBean = new FileBean();
        fileBean.setFilepath(filePath);

        return fileMapper.selectFileTreeListLikeFilePath(fileBean);
    }

    @Override
    public void deleteFile(FileBean fileBean) {
        UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        StorageBean storageBean = filetransferService.selectStorageBean(new StorageBean(sessionUserBean.getUserId()));
        long deleteSize = 0;
        String fileUrl = PathUtil.getStaticPath() + fileBean.getFileurl();
        if (fileBean.getIsdir() == 1) {
            //1、先删除子目录
            String filePath = fileBean.getFilepath() + fileBean.getFilename() + "/";
            List<FileBean> fileList = selectFileTreeListLikeFilePath(filePath);

            for (int i = 0; i < fileList.size(); i++){
                FileBean file = fileList.get(i);
                //1.1、删除数据库文件
                fileMapper.deleteFileById(file);
                //1.2、如果是文件，需要记录文件大小
                if (file.getIsdir() != 1){
                    deleteSize += file.getFilesize();
                    //1.3、删除服务器文件，只删除文件，目录是虚拟的
                    if (file.getFileurl() != null && file.getFileurl().indexOf("upload") != -1){
                        FileOperation.deleteFile(PathUtil.getStaticPath() + file.getFileurl());
                    }
                }
            }
            //2、根目录单独删除
            fileMapper.deleteFileById(fileBean);
        }else{
            fileMapper.deleteFileById(fileBean);
            deleteSize = FileOperation.getFileSize(fileUrl);
            //删除服务器文件
            if (fileBean.getFileurl() != null && fileBean.getFileurl().indexOf("upload") != -1){
                FileOperation.deleteFile(fileUrl);
            }
        }

        if (storageBean != null) {
            long updateFileSize = storageBean.getStoragesize() - deleteSize;
            if (updateFileSize < 0) {
                updateFileSize = 0;
            }
            storageBean.setStoragesize(updateFileSize);
            filetransferService.updateStorageBean(storageBean);
        }
    }

    @Override
    public void deleteFileByIds(List<Integer> fileidList) {
        fileMapper.deleteFileByIds(fileidList);
    }


    @Override
    public void updateFilepathByFilepath(String oldfilepath, String newfilepath, String filename, String extendname) {
        if ("null".equals(extendname)){
            extendname = null;
        }
        //移动根目录
        fileMapper.updateFilepathByPathAndName(oldfilepath, newfilepath, filename, extendname);

        //移动子目录
        oldfilepath = oldfilepath + filename + "/";
        newfilepath = newfilepath + filename + "/";

        if (extendname == null) { //为null说明是目录，则需要移动子目录
            fileMapper.updateFilepathByFilepath(oldfilepath, newfilepath);
        }

    }

    @Override
    public List<FileBean> selectFileByExtendName(List<String> filenameList, long userid) {
        return fileMapper.selectFileByExtendName(filenameList, userid);
    }
}
