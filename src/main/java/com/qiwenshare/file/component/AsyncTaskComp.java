package com.qiwenshare.file.component;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qiwenshare.file.api.IFileService;
import com.qiwenshare.file.api.IFiletransferService;
import com.qiwenshare.file.api.IRecoveryFileService;
import com.qiwenshare.file.api.IUserFileService;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.UserFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
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
    IFileService fileService;
    @Resource
    IRecoveryFileService recoveryFileService;
    @Resource
    IFiletransferService filetransferService;

    public Future<String> deleteUserFile(Long userFileId) {

        long begin = System.currentTimeMillis();
        UserFile userFile =userFileService.getById(userFileId);
        if (userFile.getIsDir() == 1) {
            LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userFileLambdaQueryWrapper.eq(UserFile::getDeleteBatchNum, userFile.getDeleteBatchNum());
            List<UserFile> list = userFileService.list(userFileLambdaQueryWrapper);
            recoveryFileService.deleteUserFileByDeleteBatchNum(userFile.getDeleteBatchNum());
            for (UserFile userFileItem : list) {

                Long filePointCount = fileService.getFilePointCount(userFileItem.getFileId());

                if (filePointCount != null && filePointCount == 0 && userFileItem.getIsDir() == 0) {
                    FileBean fileBean = fileService.getById(userFileItem.getFileId());
                    try {
                        filetransferService.deleteFile(fileBean);
                        fileService.removeById(fileBean.getFileId());
                    } catch (Exception e) {
                        log.error("删除本地文件失败：" + JSON.toJSONString(fileBean));
                    }
                }
            }
        } else {

            recoveryFileService.deleteUserFileByDeleteBatchNum(userFile.getDeleteBatchNum());
            Long filePointCount = fileService.getFilePointCount(userFile.getFileId());

            if (filePointCount != null && filePointCount == 0 && userFile.getIsDir() == 0) {
                FileBean fileBean = fileService.getById(userFile.getFileId());
                try {
                    filetransferService.deleteFile(fileBean);
                    fileService.removeById(fileBean.getFileId());
                } catch (Exception e) {
                    log.error("删除本地文件失败：" + JSON.toJSONString(fileBean));
                }
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("任务 deleteUserFile 耗时="+(end-begin));
        return new AsyncResult<String>("deleteUserFile");
    }
 
    //获取异步结果
    public Future<String> task4() throws InterruptedException{
        long begin = System.currentTimeMillis();
        Thread.sleep(2000L);
        long end = System.currentTimeMillis();
        System.out.println("任务4耗时="+(end-begin));
        return new AsyncResult<String>("任务4");
    }
    
    
    public Future<String> task5() throws InterruptedException{
        long begin = System.currentTimeMillis();
        Thread.sleep(3000L);
        long end = System.currentTimeMillis();
        System.out.println("任务5耗时="+(end-begin));
        return new AsyncResult<String>("任务5");
    }
    
    public Future<String> task6() throws InterruptedException{
        long begin = System.currentTimeMillis();
        Thread.sleep(1000L);
        long end = System.currentTimeMillis();
        System.out.println("任务6耗时="+(end-begin));
        return new AsyncResult<String>("任务6");
    }
        
}