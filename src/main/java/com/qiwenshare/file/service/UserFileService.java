package com.qiwenshare.file.service;

import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiwenshare.common.constant.FileConstant;
import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.common.util.security.JwtUser;
import com.qiwenshare.common.util.security.SessionUtil;
import com.qiwenshare.file.api.IUserFileService;
import com.qiwenshare.file.component.FileDealComp;
import com.qiwenshare.file.domain.RecoveryFile;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.io.QiwenFile;
import com.qiwenshare.file.mapper.RecoveryFileMapper;
import com.qiwenshare.file.mapper.UserFileMapper;
import com.qiwenshare.file.vo.file.FileListVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserFileService extends ServiceImpl<UserFileMapper, UserFile> implements IUserFileService {
    @Resource
    UserFileMapper userFileMapper;
    @Resource
    RecoveryFileMapper recoveryFileMapper;
    @Resource
    FileDealComp fileDealComp;

    public static Executor executor = Executors.newFixedThreadPool(20);


    @Override
    public List<UserFile> selectUserFileByNameAndPath(String fileName, String filePath, String userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileName, fileName)
                .eq(UserFile::getFilePath, filePath)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getDeleteFlag, 0);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<UserFile> selectSameUserFile(String fileName, String filePath, String extendName, String userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileName, fileName)
                .eq(UserFile::getFilePath, filePath)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getExtendName, extendName)
                .eq(UserFile::getDeleteFlag, "0");
        return userFileMapper.selectList(lambdaQueryWrapper);
    }


    @Override
    public IPage<FileListVO> userFileList(String userId, String filePath, Long currentPage, Long pageCount) {
        Page<FileListVO> page = new Page<>(currentPage, pageCount);
        UserFile userFile = new UserFile();
        JwtUser sessionUserBean = SessionUtil.getSession();
        if (userId == null) {
            userFile.setUserId(sessionUserBean.getUserId());
        } else {
            userFile.setUserId(userId);
        }

        userFile.setFilePath(URLDecoder.decodeForPath(filePath, StandardCharsets.UTF_8));

        return userFileMapper.selectPageVo(page, userFile, null);
    }

    @Override
    public void updateFilepathByUserFileId(String userFileId, String newfilePath, String userId) {
        UserFile userFile = userFileMapper.selectById(userFileId);
        String oldfilePath = userFile.getFilePath();
        String fileName = userFile.getFileName();

        userFile.setFilePath(newfilePath);
        if (userFile.getIsDir() == 0) {
            String repeatFileName = fileDealComp.getRepeatFileName(userFile, userFile.getFilePath());
            userFile.setFileName(repeatFileName);
        }
        try {
            userFileMapper.updateById(userFile);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        //移动子目录
        oldfilePath = new QiwenFile(oldfilePath, fileName, true).getPath();
        newfilePath = new QiwenFile(newfilePath, fileName, true).getPath();

        if (userFile.isDirectory()) { //如果是目录，则需要移动子目录
            List<UserFile> list = selectUserFileByLikeRightFilePath(oldfilePath, userId);

            for (UserFile newUserFile : list) {
                newUserFile.setFilePath(newUserFile.getFilePath().replaceFirst(oldfilePath, newfilePath));
                if (newUserFile.getIsDir() == 0) {
                    String repeatFileName = fileDealComp.getRepeatFileName(newUserFile, newUserFile.getFilePath());
                    newUserFile.setFileName(repeatFileName);
                }
                try {
                    userFileMapper.updateById(newUserFile);
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }
            }
        }

    }

    @Override
    public void userFileCopy(String userId, String userFileId, String newfilePath) {
        UserFile userFile = userFileMapper.selectById(userFileId);
        String oldfilePath = userFile.getFilePath();
        String oldUserId = userFile.getUserId();
        String fileName = userFile.getFileName();

        userFile.setFilePath(newfilePath);
        userFile.setUserId(userId);
        userFile.setUserFileId(IdUtil.getSnowflakeNextIdStr());
        if (userFile.getIsDir() == 0) {
            String repeatFileName = fileDealComp.getRepeatFileName(userFile, userFile.getFilePath());
            userFile.setFileName(repeatFileName);
        }
        try {
            userFileMapper.insert(userFile);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        oldfilePath = new QiwenFile(oldfilePath, fileName, true).getPath();
        newfilePath = new QiwenFile(newfilePath, fileName, true).getPath();


        if (userFile.isDirectory()) {
            List<UserFile> subUserFileList = userFileMapper.selectUserFileByLikeRightFilePath(oldfilePath, oldUserId);

            for (UserFile newUserFile : subUserFileList) {
                newUserFile.setFilePath(newUserFile.getFilePath().replaceFirst(oldfilePath, newfilePath));
                newUserFile.setUserFileId(IdUtil.getSnowflakeNextIdStr());
                if (newUserFile.isDirectory()) {
                    String repeatFileName = fileDealComp.getRepeatFileName(newUserFile, newUserFile.getFilePath());
                    newUserFile.setFileName(repeatFileName);
                }
                newUserFile.setUserId(userId);
                try {
                    userFileMapper.insert(newUserFile);
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }
            }
        }

    }

    @Override
    public IPage<FileListVO> getFileByFileType(Integer fileTypeId, Long currentPage, Long pageCount, String userId) {
        Page<FileListVO> page = new Page<>(currentPage, pageCount);

        UserFile userFile = new UserFile();
        userFile.setUserId(userId);
        return userFileMapper.selectPageVo(page, userFile, fileTypeId);
    }

    @Override
    public List<UserFile> selectUserFileListByPath(String filePath, String userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(UserFile::getFilePath, filePath)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getDeleteFlag, 0);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<UserFile> selectFilePathTreeByUserId(String userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getUserId, userId)
                .eq(UserFile::getIsDir, 1)
                .eq(UserFile::getDeleteFlag, 0);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }


    @Override
    public void deleteUserFile(String userFileId, String sessionUserId) {
        UserFile userFile = userFileMapper.selectById(userFileId);
        String uuid = UUID.randomUUID().toString();
        if (userFile.getIsDir() == 1) {
            LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<UserFile>();
            userFileLambdaUpdateWrapper.set(UserFile::getDeleteFlag, RandomUtil.randomInt(FileConstant.deleteFileRandomSize))
                    .set(UserFile::getDeleteBatchNum, uuid)
                    .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                    .eq(UserFile::getUserFileId, userFileId);
            userFileMapper.update(null, userFileLambdaUpdateWrapper);

            String filePath = new QiwenFile(userFile.getFilePath(), userFile.getFileName(), true).getPath();
            updateFileDeleteStateByFilePath(filePath, uuid, sessionUserId);

        } else {
            LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            userFileLambdaUpdateWrapper.set(UserFile::getDeleteFlag, RandomUtil.randomInt(1, FileConstant.deleteFileRandomSize))
                    .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                    .set(UserFile::getDeleteBatchNum, uuid)
                    .eq(UserFile::getUserFileId, userFileId);
            userFileMapper.update(null, userFileLambdaUpdateWrapper);
        }

        RecoveryFile recoveryFile = new RecoveryFile();
        recoveryFile.setUserFileId(userFileId);
        recoveryFile.setDeleteTime(DateUtil.getCurrentTime());
        recoveryFile.setDeleteBatchNum(uuid);
        recoveryFileMapper.insert(recoveryFile);


    }

    @Override
    public List<UserFile> selectUserFileByLikeRightFilePath(String filePath, String userId) {
        return userFileMapper.selectUserFileByLikeRightFilePath(filePath, userId);
    }

    private void updateFileDeleteStateByFilePath(String filePath, String deleteBatchNum, String userId) {
        executor.execute(() -> {
            List<UserFile> fileList = selectUserFileByLikeRightFilePath(filePath, userId);
            List<String> userFileIds = fileList.stream().map(UserFile::getUserFileId).collect(Collectors.toList());

                //标记删除标志
            if (CollectionUtils.isNotEmpty(userFileIds)) {
                LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper1 = new LambdaUpdateWrapper<>();
                userFileLambdaUpdateWrapper1.set(UserFile::getDeleteFlag, RandomUtil.randomInt(FileConstant.deleteFileRandomSize))
                        .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                        .set(UserFile::getDeleteBatchNum, deleteBatchNum)
                        .in(UserFile::getUserFileId, userFileIds)
                        .eq(UserFile::getDeleteFlag, 0);
                userFileMapper.update(null, userFileLambdaUpdateWrapper1);
            }
            for (String userFileId : userFileIds) {
                fileDealComp.deleteESByUserFileId(userFileId);
            }
        });
    }


}
