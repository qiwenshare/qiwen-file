package com.qiwenshare.file.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.common.util.PathUtil;
import com.qiwenshare.file.api.IRecoveryFileService;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.RecoveryFile;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.mapper.FileMapper;
import com.qiwenshare.file.mapper.RecoveryFileMapper;
import com.qiwenshare.file.mapper.UserFileMapper;
import com.qiwenshare.file.vo.file.RecoveryFileListVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class RecoveryFileService  extends ServiceImpl<RecoveryFileMapper, RecoveryFile> implements IRecoveryFileService {
    @Resource
    UserFileMapper userFileMapper;
    @Resource
    FileMapper fileMapper;
    @Resource
    RecoveryFileMapper recoveryFileMapper;

    public static Executor executor = Executors.newFixedThreadPool(20);

    @Override
    public void deleteRecoveryFile(UserFile userFile) {
        if (userFile.getIsDir() == 1) {
            updateFilePointCountByBatchNum(userFile.getDeleteBatchNum());

        }else{

            UserFile userFileTemp = userFileMapper.selectById(userFile.getUserFileId());
            FileBean fileBean = fileMapper.selectById(userFileTemp.getFileId());

            LambdaUpdateWrapper<FileBean> fileBeanLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            fileBeanLambdaUpdateWrapper.set(FileBean::getPointCount, fileBean.getPointCount() -1)
                    .eq(FileBean::getFileId, fileBean.getFileId());

            fileMapper.update(null, fileBeanLambdaUpdateWrapper);
        }
        LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userFileLambdaQueryWrapper.eq(UserFile::getDeleteBatchNum, userFile.getDeleteBatchNum());
        userFileMapper.delete(userFileLambdaQueryWrapper);



    }
    
    public void restorefile(String deleteBatchNum, String filePath, Long sessionUserId) {
        LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userFileLambdaUpdateWrapper.set(UserFile::getDeleteFlag, 0)
                .set(UserFile::getDeleteBatchNum, "")
                .eq(UserFile::getDeleteBatchNum, deleteBatchNum);
        userFileMapper.update(null, userFileLambdaUpdateWrapper);

        String parentFilePath = PathUtil.getParentPath(filePath);
        while(parentFilePath.indexOf("/") != -1) {
            String fileName = parentFilePath.substring(parentFilePath.lastIndexOf("/") + 1);
            parentFilePath = PathUtil.getParentPath(parentFilePath);
            LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UserFile::getFilePath, parentFilePath + "/")
                    .eq(UserFile::getDeleteFlag, 0)
                    .eq(UserFile::getUserId, sessionUserId);
            List<UserFile> userFileList = userFileMapper.selectList(lambdaQueryWrapper);
            if (userFileList.size() == 0) {
                UserFile userFile = new UserFile();
                userFile.setUserId(sessionUserId);
                userFile.setFileName(fileName);
                userFile.setFilePath(parentFilePath + "/");
                userFile.setDeleteFlag(0);
                userFile.setIsDir(1);
                userFile.setUploadTime(DateUtil.getCurrentTime());

                userFileMapper.insert(userFile);
            }

        }

        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.select(UserFile::getFileName, UserFile::getFilePath)
                .likeRight(UserFile::getFilePath, filePath)
                .eq(UserFile::getIsDir, 1)
                .eq(UserFile::getDeleteFlag, 0)
                .groupBy(UserFile::getFilePath, UserFile::getFileName)
                .having("count(fileName) >= 2");
        List<UserFile> repeatList = userFileMapper.selectList(lambdaQueryWrapper);

        for (UserFile userFile : repeatList) {
            LambdaQueryWrapper<UserFile> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(UserFile::getFilePath, userFile.getFilePath())
                    .eq(UserFile::getFileName, userFile.getFileName())
                    .eq(UserFile::getDeleteFlag, "0");
            List<UserFile> userFiles = userFileMapper.selectList(lambdaQueryWrapper1);
            log.info("重复的文件:" + JSON.toJSONString(userFiles));
            for (int i = 0; i < userFiles.size() - 1; i ++) {
                log.info("删除文件：" + JSON.toJSONString(userFiles.get(i)));
                userFileMapper.deleteById(userFiles.get(i).getUserFileId());
            }
        }

        log.info(JSON.toJSONString(repeatList));

        LambdaQueryWrapper<RecoveryFile> recoveryFileServiceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        recoveryFileServiceLambdaQueryWrapper.eq(RecoveryFile::getDeleteBatchNum, deleteBatchNum);
        recoveryFileMapper.delete(recoveryFileServiceLambdaQueryWrapper);
    }



    private void updateFilePointCountByBatchNum(String deleteBatchNum) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getDeleteBatchNum, deleteBatchNum);
        List<UserFile> fileList = userFileMapper.selectList(lambdaQueryWrapper);

        new Thread(()->{
            for (int i = 0; i < fileList.size(); i++){
                UserFile userFileTemp = fileList.get(i);
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (userFileTemp.getIsDir() != 1){
                            FileBean fileBean = fileMapper.selectById(userFileTemp.getFileId());
                            if (fileBean.getPointCount() != null) {

                                LambdaUpdateWrapper<FileBean> fileBeanLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                                fileBeanLambdaUpdateWrapper.set(FileBean::getPointCount, fileBean.getPointCount() -1)
                                        .eq(FileBean::getFileId, fileBean.getFileId());
                                fileMapper.update(null, fileBeanLambdaUpdateWrapper);

                            }
                        }
                    }
                });

            }
        }).start();
    }
    @Override
    public List<RecoveryFileListVo> selectRecoveryFileList() {
        return recoveryFileMapper.selectRecoveryFileList();
    }
}
