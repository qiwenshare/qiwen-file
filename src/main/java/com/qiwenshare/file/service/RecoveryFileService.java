package com.qiwenshare.file.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiwenshare.file.api.IRecoveryFileService;
import com.qiwenshare.file.component.FileDealComp;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.RecoveryFile;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.mapper.FileMapper;
import com.qiwenshare.file.mapper.RecoveryFileMapper;
import com.qiwenshare.file.mapper.UserFileMapper;
import com.qiwenshare.file.vo.file.RecoveryFileListVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class RecoveryFileService  extends ServiceImpl<RecoveryFileMapper, RecoveryFile> implements IRecoveryFileService {
    @Resource
    UserFileMapper userFileMapper;
    @Resource
    FileMapper fileMapper;
    @Resource
    RecoveryFileMapper recoveryFileMapper;
    @Resource
    FileDealComp fileDealComp;

    public static Executor executor = Executors.newFixedThreadPool(20);

    @Override
    public void deleteRecoveryFile(UserFile userFile) {
        if (userFile == null) {
            return ;

        }
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

    @Override
    public void restorefile(String deleteBatchNum, String filePath, Long sessionUserId) {
//        LambdaQueryWrapper<RecoveryFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(RecoveryFile::getDeleteBatchNum, deleteBatchNum);
//        List<RecoveryFile> list  = recoveryFileMapper.selectList(lambdaQueryWrapper);
//        if ()

        LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userFileLambdaUpdateWrapper.set(UserFile::getDeleteFlag, 0)
                .set(UserFile::getDeleteBatchNum, "")
                .eq(UserFile::getDeleteBatchNum, deleteBatchNum);
        userFileMapper.update(null, userFileLambdaUpdateWrapper);

        fileDealComp.restoreParentFilePath(filePath, sessionUserId);

        fileDealComp.deleteRepeatSubDirFile(filePath, sessionUserId);
        // TODO 如果被还原的文件已存在，暂未实现

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
    public List<RecoveryFileListVo> selectRecoveryFileList(Long userId) {
        return recoveryFileMapper.selectRecoveryFileList(userId);
    }
}
