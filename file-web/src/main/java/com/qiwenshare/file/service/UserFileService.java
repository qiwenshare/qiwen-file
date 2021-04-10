package com.qiwenshare.file.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.file.api.IUserFileService;
import com.qiwenshare.common.config.QiwenFileConfig;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.RecoveryFile;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.mapper.FileMapper;
import com.qiwenshare.file.mapper.RecoveryFileMapper;
import com.qiwenshare.file.mapper.UserFileMapper;
import com.qiwenshare.file.vo.file.FileListVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class UserFileService  extends ServiceImpl<UserFileMapper, UserFile> implements IUserFileService {
    @Resource
    UserFileMapper userFileMapper;
    @Resource
    FileMapper fileMapper;
    @Resource
    RecoveryFileMapper recoveryFileMapper;

    public static Executor executor = Executors.newFixedThreadPool(20);


    @Override
    public List<UserFile> selectUserFileByNameAndPath(String fileName, String filePath, Long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileName, fileName)
                .eq(UserFile::getFilePath, filePath)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getDeleteFlag, "0");
        return userFileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public void replaceUserFilePath(String filePath, String oldFilePath, Long userId) {
        userFileMapper.replaceFilePath(filePath, oldFilePath, userId);
    }

    @Override
    public List<FileListVo> userFileList(UserFile userFile, Long beginCount, Long pageCount) {
        return userFileMapper.userFileList(userFile, beginCount, pageCount);
    }

//    public void renameUserFile(Long userFileId, String newFileName, Long userId) {
//        UserFile userFile = userFileMapper.selectById(userFileId);
//        if (1 == userFile.getIsDir()) {
//            LambdaUpdateWrapper<UserFile> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//            lambdaUpdateWrapper.set(UserFile::getFileName, newFileName)
//                    .set(UserFile::getUploadTime, DateUtil.getCurrentTime())
//                    .eq(UserFile::getUserFileId, userFile.getUserFileId());
//            userFileMapper.update(null, lambdaUpdateWrapper);
//            replaceUserFilePath(userFile.getFilePath() + newFileName + "/",
//                    userFile.getFilePath() + userFile.getFileName() + "/", userId);
//        } else {
//            FileBean fileBean = fileMapper.selectById(userFile.getFileId());
//            if (fileBean.getIsOSS() == 1) {
////                LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
////                lambdaQueryWrapper.eq(UserFile::getUserFileId, renameFileDto.getUserFileId());
////                UserFile userFile = userFileService.getOne(lambdaQueryWrapper);
////
////                FileBean file = fileService.getById(userFile.getFileId());
//                String fileUrl = fileBean.getFileUrl();
//                String newFileUrl = fileUrl.replace(userFile.getFileName(), newFileName);
//
//                AliyunOSSRename.rename(qiwenFileConfig.getAliyun().getOss(),
//                        fileUrl.substring(1),
//                        newFileUrl.substring(1));
//                LambdaUpdateWrapper<FileBean> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//                lambdaUpdateWrapper
//                        .set(FileBean::getFileUrl, newFileUrl)
//                        .eq(FileBean::getFileId, fileBean.getFileId());
//                fileMapper.update(null, lambdaUpdateWrapper);
//
//                LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//                userFileLambdaUpdateWrapper
//                        .set(UserFile::getFileName, newFileName)
//                        .set(UserFile::getUploadTime, DateUtil.getCurrentTime())
//                        .eq(UserFile::getUserFileId, userFileId);
//                userFileMapper.update(null, userFileLambdaUpdateWrapper);
//            } else {
//                LambdaUpdateWrapper<UserFile> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//                lambdaUpdateWrapper.set(UserFile::getFileName, newFileName)
//                        .set(UserFile::getUploadTime, DateUtil.getCurrentTime())
//                        .eq(UserFile::getUserFileId, userFileId);
//                userFileMapper.update(null, lambdaUpdateWrapper);
//            }
//
//
//        }
//    }

    @Override
    public void updateFilepathByFilepath(String oldfilePath, String newfilePath, String fileName, String extendName, long userId) {
        if ("null".equals(extendName)){
            extendName = null;
        }
        //移动根目录
        userFileMapper.updateFilepathByPathAndName(oldfilePath, newfilePath, fileName, extendName, userId);

        //移动子目录
        oldfilePath = oldfilePath + fileName + "/";
        newfilePath = newfilePath + fileName + "/";

        oldfilePath = oldfilePath.replace("\\", "\\\\\\\\");
        oldfilePath = oldfilePath.replace("'", "\\'");
        oldfilePath = oldfilePath.replace("%", "\\%");
        oldfilePath = oldfilePath.replace("_", "\\_");

        if (extendName == null) { //为null说明是目录，则需要移动子目录
            userFileMapper.updateFilepathByFilepath(oldfilePath, newfilePath, userId);
        }

    }


    @Override
    public List<FileListVo> selectFileByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, long userId) {
        return userFileMapper.selectFileByExtendName(fileNameList, beginCount, pageCount, userId);
    }

    @Override
    public Long selectCountByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, long userId) {
        return userFileMapper.selectCountByExtendName(fileNameList, beginCount, pageCount, userId);
    }

    @Override
    public List<FileListVo> selectFileNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, long userId) {
        return userFileMapper.selectFileNotInExtendNames(fileNameList, beginCount, pageCount, userId);
    }

    @Override
    public Long selectCountNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, long userId) {
        return userFileMapper.selectCountNotInExtendNames(fileNameList, beginCount, pageCount, userId);
    }

    @Override
    public List<UserFile> selectFileListLikeRightFilePath(String filePath, long userId) {
        filePath = filePath.replace("\\", "\\\\\\\\");
        filePath = filePath.replace("'", "\\'");
        filePath = filePath.replace("%", "\\%");
        filePath = filePath.replace("_", "\\_");

        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        log.info("查询文件路径：" + filePath);

        lambdaQueryWrapper.eq(UserFile::getUserId, userId)
                .likeRight(UserFile::getFilePath, filePath)
                .eq(UserFile::getDeleteFlag, 0);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<UserFile> selectFilePathTreeByUserId(Long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getUserId, userId)
                .eq(UserFile::getIsDir, 1)
                .eq(UserFile::getDeleteFlag, 0);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }


    @Override
    public void deleteUserFile(Long userFileId, Long sessionUserId) {
//        UserFile userFile
        UserFile userFile = userFileMapper.selectById(userFileId);
        String uuid = UUID.randomUUID().toString();
        if (userFile.getIsDir() == 1) {
            LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<UserFile>();
            userFileLambdaUpdateWrapper.set(UserFile::getDeleteFlag, 1)
                    .set(UserFile::getDeleteBatchNum, uuid)
                    .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                    .eq(UserFile::getUserFileId, userFileId);
            userFileMapper.update(null, userFileLambdaUpdateWrapper);

            String filePath = userFile.getFilePath() + userFile.getFileName() + "/";
            updateFileDeleteStateByFilePath(filePath, userFile.getDeleteBatchNum(), sessionUserId);

        }else{

            UserFile userFileTemp = userFileMapper.selectById(userFileId);
            FileBean fileBean = fileMapper.selectById(userFileTemp.getFileId());

            LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            userFileLambdaUpdateWrapper.set(UserFile::getDeleteFlag, 1)
                    .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                    .set(UserFile::getDeleteBatchNum, uuid)
                    .eq(UserFile::getUserFileId, userFileTemp.getUserFileId());
            userFileMapper.update(null, userFileLambdaUpdateWrapper);

        }


        RecoveryFile recoveryFile = new RecoveryFile();
        recoveryFile.setUserFileId(userFileId);
        recoveryFile.setDeleteTime(DateUtil.getCurrentTime());
        recoveryFile.setDeleteBatchNum(uuid);
        recoveryFileMapper.insert(recoveryFile);


    }

    @Override
    public UserFile repeatUserFileDeal(UserFile userFile) {
        String fileName = userFile.getFileName();
        String filePath = userFile.getFilePath();
        String extendName = userFile.getExtendName();
        Integer deleteFlag = userFile.getDeleteFlag();
        Long userId = userFile.getUserId();
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFilePath, filePath)
                .eq(UserFile::getExtendName, extendName)
                .eq(UserFile::getDeleteFlag, deleteFlag)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getFileName, fileName);
        List<UserFile> list = userFileMapper.selectList(lambdaQueryWrapper);
        if (list == null) {
            return userFile;
        }
        if (list.isEmpty()) {
            return userFile;
        }
        int i = 0;

        while (list != null && !list.isEmpty()) {
            i++;
            lambdaQueryWrapper.eq(UserFile::getFilePath, filePath)
                    .eq(UserFile::getExtendName, extendName)
                    .eq(UserFile::getDeleteFlag, deleteFlag)
                    .eq(UserFile::getUserId, userId)
                    .eq(UserFile::getFileName, fileName + "(" + i + ")");
            list = userFileMapper.selectList(lambdaQueryWrapper);
        }
        userFile.setFileName(fileName + "(" + i + ")");
        return userFile;

    }

    private void updateFileDeleteStateByFilePath(String filePath, String deleteBatchNum, Long userId) {
        new Thread(()->{
            List<UserFile> fileList = selectFileListLikeRightFilePath(filePath, userId);
            for (int i = 0; i < fileList.size(); i++){
                UserFile userFileTemp = fileList.get(i);
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        //标记删除标志
                        LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper1 = new LambdaUpdateWrapper<>();
                        userFileLambdaUpdateWrapper1.set(UserFile::getDeleteFlag, 1)
                                .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                                .set(UserFile::getDeleteBatchNum, deleteBatchNum)
                                .eq(UserFile::getUserFileId, userFileTemp.getUserFileId())
                                .eq(UserFile::getDeleteFlag, 0);
                        userFileMapper.update(null, userFileLambdaUpdateWrapper1);
                    }
                });

            }
        }).start();
    }


}
