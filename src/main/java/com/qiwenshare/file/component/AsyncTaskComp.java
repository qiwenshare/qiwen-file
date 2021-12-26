package com.qiwenshare.file.component;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.file.api.IFileService;
import com.qiwenshare.file.api.IFiletransferService;
import com.qiwenshare.file.api.IRecoveryFileService;
import com.qiwenshare.file.api.IUserFileService;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.mapper.FileMapper;
import com.qiwenshare.file.mapper.UserFileMapper;
import com.qiwenshare.file.util.QiwenFileUtil;
import com.qiwenshare.ufop.factory.UFOPFactory;
import com.qiwenshare.ufop.operation.copy.domain.CopyFile;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

/**
 * 功能描述：异步任务业务类（@Async也可添加在方法上）
 */
@Slf4j
@Component
@Async("asyncTaskExecutor")
public class AsyncTaskComp {
    @Resource
    IUserFileService userFileService;

    @Resource
    IRecoveryFileService recoveryFileService;
    @Resource
    IFiletransferService filetransferService;
    @Resource
    UFOPFactory ufopFactory;
    @Resource
    UserFileMapper userFileMapper;
    @Resource
    FileMapper fileMapper;
    @Resource
    FileDealComp fileDealComp;

    @Value("${ufop.storage-type}")
    private Integer storageType;

    public Long getFilePointCount(Long fileId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileId, fileId);
        long count = userFileMapper.selectCount(lambdaQueryWrapper);
        return count;
    }

    public Future<String> deleteUserFile(Long userFileId) {

        long begin = System.currentTimeMillis();
        UserFile userFile = userFileService.getById(userFileId);
        if (userFile.getIsDir() == 1) {
            LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userFileLambdaQueryWrapper.eq(UserFile::getDeleteBatchNum, userFile.getDeleteBatchNum());
            List<UserFile> list = userFileService.list(userFileLambdaQueryWrapper);
            recoveryFileService.deleteUserFileByDeleteBatchNum(userFile.getDeleteBatchNum());
            for (UserFile userFileItem : list) {

                Long filePointCount = getFilePointCount(userFileItem.getFileId());

                if (filePointCount != null && filePointCount == 0 && userFileItem.getIsDir() == 0) {
                    FileBean fileBean = fileMapper.selectById(userFileItem.getFileId());
                    try {
                        filetransferService.deleteFile(fileBean);
                        fileMapper.deleteById(fileBean.getFileId());
                    } catch (Exception e) {
                        log.error("删除本地文件失败：" + JSON.toJSONString(fileBean));
                    }
                }
            }
        } else {

            recoveryFileService.deleteUserFileByDeleteBatchNum(userFile.getDeleteBatchNum());
            Long filePointCount = getFilePointCount(userFile.getFileId());

            if (filePointCount != null && filePointCount == 0 && userFile.getIsDir() == 0) {
                FileBean fileBean = fileMapper.selectById(userFile.getFileId());
                try {
                    filetransferService.deleteFile(fileBean);
                    fileMapper.deleteById(fileBean.getFileId());
                } catch (Exception e) {
                    log.error("删除本地文件失败：" + JSON.toJSONString(fileBean));
                }
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("任务 deleteUserFile 耗时=" + (end - begin));
        return new AsyncResult<String>("deleteUserFile");
    }


    public Future<String> saveUnzipFile(UserFile userFile, FileBean fileBean, int unzipMode, String entryName, String filePath) {
        String unzipUrl = UFOPUtils.getTempFile(fileBean.getFileUrl()).getAbsolutePath().replace("." + userFile.getExtendName(), "");
        String totalFileUrl = unzipUrl + entryName;
        File currentFile = new File(totalFileUrl);


        UserFile saveUserFile = new UserFile();

        saveUserFile.setUploadTime(DateUtil.getCurrentTime());
        saveUserFile.setUserId(userFile.getUserId());
        saveUserFile.setFilePath(UFOPUtils.pathSplitFormat(userFile.getFilePath() + entryName.replace(currentFile.getName(), "")).replace("\\", "/"));

        if (currentFile.isDirectory()) {
            saveUserFile.setIsDir(1);
            saveUserFile.setFileName(currentFile.getName());
        } else {

            FileInputStream fis = null;
            String md5Str = UUID.randomUUID().toString();
            try {
                fis = new FileInputStream(currentFile);
                md5Str = DigestUtils.md5Hex(fis);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(fis);
            }

            FileInputStream fileInputStream = null;
            try {
                Map<String, Object> param = new HashMap<>();
                param.put("identifier", md5Str);
                List<FileBean> list = fileMapper.selectByMap(param);

                if (list != null && !list.isEmpty()) { //文件已存在
                    saveUserFile.setFileId(list.get(0).getFileId());
                } else { //文件不存在
                    fileInputStream = new FileInputStream(currentFile);
                    CopyFile createFile = new CopyFile();
                    createFile.setExtendName(UFOPUtils.getFileExtendName(totalFileUrl));
                    String saveFileUrl = ufopFactory.getCopier().copy(fileInputStream, createFile);

                    FileBean tempFileBean = new FileBean(saveFileUrl, currentFile.length(), storageType, md5Str, userFile.getUserId());
;
                    fileMapper.insert(tempFileBean);

                    saveUserFile.setFileId(tempFileBean.getFileId());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(fileInputStream);
                System.gc();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                currentFile.delete();
            }

            saveUserFile.setIsDir(0);
            saveUserFile.setExtendName(UFOPUtils.getFileExtendName(totalFileUrl));
            saveUserFile.setFileName(UFOPUtils.getFileNameNotExtend(currentFile.getName()));

        }


        saveUserFile.setDeleteFlag(0);

        if (unzipMode == 1) {
            saveUserFile.setFilePath(UFOPUtils.pathSplitFormat(userFile.getFilePath() + userFile.getFileName() + "/" + entryName.replace(currentFile.getName(), "")).replace("\\", "/"));
        } else if (unzipMode == 2) {
            saveUserFile.setFilePath(UFOPUtils.pathSplitFormat(filePath + entryName.replace(currentFile.getName(), "")).replace("\\", "/"));
        }

        String fileName = fileDealComp.getRepeatFileName(saveUserFile, saveUserFile.getFilePath());

        if (saveUserFile.getIsDir() == 1 && !fileName.equals(saveUserFile.getFileName())) {
            //如果是目录，而且重复，什么也不做
        } else {
            saveUserFile.setFileName(fileName);
            userFileMapper.insert(saveUserFile);
        }


        return new AsyncResult<String>("saveUnzipFile");
    }

    //获取异步结果
    public Future<String> task4() throws InterruptedException {
        long begin = System.currentTimeMillis();
        Thread.sleep(2000L);
        long end = System.currentTimeMillis();
        System.out.println("任务4耗时=" + (end - begin));
        return new AsyncResult<String>("任务4");
    }


    public Future<String> task5() throws InterruptedException {
        long begin = System.currentTimeMillis();
        Thread.sleep(3000L);
        long end = System.currentTimeMillis();
        System.out.println("任务5耗时=" + (end - begin));
        return new AsyncResult<String>("任务5");
    }

    public Future<String> task6() throws InterruptedException {
        long begin = System.currentTimeMillis();
        Thread.sleep(1000L);
        long end = System.currentTimeMillis();
        System.out.println("任务6耗时=" + (end - begin));
        return new AsyncResult<String>("任务6");
    }

}