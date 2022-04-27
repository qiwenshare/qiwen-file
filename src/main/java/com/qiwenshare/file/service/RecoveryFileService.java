package com.qiwenshare.file.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiwenshare.file.api.IRecoveryFileService;
import com.qiwenshare.file.component.FileDealComp;
import com.qiwenshare.file.domain.RecoveryFile;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.io.QiwenFile;
import com.qiwenshare.file.mapper.RecoveryFileMapper;
import com.qiwenshare.file.mapper.UserFileMapper;
import com.qiwenshare.file.vo.file.RecoveryFileListVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class RecoveryFileService  extends ServiceImpl<RecoveryFileMapper, RecoveryFile> implements IRecoveryFileService {
    @Resource
    UserFileMapper userFileMapper;
    @Resource
    RecoveryFileMapper recoveryFileMapper;
    @Resource
    FileDealComp fileDealComp;


    @Override
    public void deleteUserFileByDeleteBatchNum(String deleteBatchNum) {


        LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userFileLambdaQueryWrapper.eq(UserFile::getDeleteBatchNum, deleteBatchNum);
        userFileMapper.delete(userFileLambdaQueryWrapper);



    }

    @Override
    public void restorefile(String deleteBatchNum, String filePath, Long sessionUserId) {

        LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userFileLambdaUpdateWrapper.set(UserFile::getDeleteFlag, 0)
                .set(UserFile::getDeleteBatchNum, "")
                .eq(UserFile::getDeleteBatchNum, deleteBatchNum);
        userFileMapper.update(null, userFileLambdaUpdateWrapper);
        QiwenFile qiwenFile = new QiwenFile(filePath, true);
        fileDealComp.restoreParentFilePath(qiwenFile, sessionUserId);

        fileDealComp.deleteRepeatSubDirFile(filePath, sessionUserId);
        // TODO 如果被还原的文件已存在，暂未实现

        LambdaQueryWrapper<RecoveryFile> recoveryFileServiceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        recoveryFileServiceLambdaQueryWrapper.eq(RecoveryFile::getDeleteBatchNum, deleteBatchNum);
        recoveryFileMapper.delete(recoveryFileServiceLambdaQueryWrapper);
    }

    @Override
    public List<RecoveryFileListVo> selectRecoveryFileList(Long userId) {
        return recoveryFileMapper.selectRecoveryFileList(userId);
    }
}
