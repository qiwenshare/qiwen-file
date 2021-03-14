package com.qiwenshare.file.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qiwenshare.common.cbb.DateUtil;
import com.qiwenshare.common.cbb.RestResult;
import com.qiwenshare.common.util.PathUtil;
import com.qiwenshare.file.anno.MyLog;
import com.qiwenshare.file.api.IRecoveryFileService;
import com.qiwenshare.file.api.IUserFileService;
import com.qiwenshare.file.api.IUserService;
import com.qiwenshare.file.domain.RecoveryFile;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.dto.BatchDeleteRecoveryFileDTO;
import com.qiwenshare.file.dto.DeleteRecoveryFileDTO;
import com.qiwenshare.file.dto.recoveryfile.RestoreFileDTO;
import com.qiwenshare.file.vo.file.RecoveryFileListVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Tag(name = "recoveryfile", description = "文件删除后会进入回收站，该接口主要是对回收站文件进行管理")
@RestController
@Slf4j
@RequestMapping("/recoveryfile")
public class RecoveryFileController {
    @Resource
    IRecoveryFileService recoveryFileService;
    @Resource
    IUserFileService userFileService;
    @Resource
    IUserService userService;
    public static final String CURRENT_MODULE = "回收站文件接口";

    @Operation(summary = "删除回收文件", description = "删除回收文件", tags = {"recoveryfile"})
    @MyLog(operation = "删除回收文件", module = CURRENT_MODULE)
    @RequestMapping(value = "/deleterecoveryfile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> deleteRecoveryFile(@RequestBody DeleteRecoveryFileDTO deleteRecoveryFileDTO, @RequestHeader("token") String token) {

        RecoveryFile recoveryFile = recoveryFileService.getById(deleteRecoveryFileDTO.getRecoveryFileId());
        UserFile userFile =userFileService.getById(recoveryFile.getUserFileId());

        recoveryFileService.deleteRecoveryFile(userFile);
        recoveryFileService.removeById(deleteRecoveryFileDTO.getRecoveryFileId());

        return RestResult.success().data("删除成功");
    }

    @Operation(summary = "批量删除回收文件", description = "批量删除回收文件", tags = {"recoveryfile"})
    @RequestMapping(value = "/batchdelete", method = RequestMethod.POST)
    @MyLog(operation = "批量删除回收文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<String> batchDeleteRecoveryFile(@RequestBody BatchDeleteRecoveryFileDTO batchDeleteRecoveryFileDTO, @RequestHeader("token") String token) {



        List<RecoveryFile> recoveryFileList = JSON.parseArray(batchDeleteRecoveryFileDTO.getRecoveryFileIds(), RecoveryFile.class);
        for (RecoveryFile recoveryFile : recoveryFileList) {

            RecoveryFile recoveryFile1 = recoveryFileService.getById(recoveryFile.getRecoveryFileId());
            UserFile userFile =userFileService.getById(recoveryFile1.getUserFileId());

            recoveryFileService.deleteRecoveryFile(userFile);
            recoveryFileService.removeById(recoveryFile.getRecoveryFileId());
        }
        return RestResult.success().data("批量删除成功");
    }

    @Operation(summary = "回收文件列表", description = "回收文件列表", tags = {"recoveryfile"})
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<List<RecoveryFileListVo>> getRecoveryFileList(@RequestHeader("token") String token) {
        RestResult<List<RecoveryFileListVo>> restResult = new RestResult<List<RecoveryFileListVo>>();
        List<RecoveryFileListVo> recoveryFileList = recoveryFileService.selectRecoveryFileList();
        restResult.setData(recoveryFileList);
        restResult.setSuccess(true);

        return restResult;
    }

    @Operation(summary = "还原文件", description = "还原文件", tags = {"recoveryfile"})
    @RequestMapping(value = "/restorefile", method = RequestMethod.POST)
    @MyLog(operation = "还原文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult restoreFile(@RequestBody RestoreFileDTO restoreFileDto, @RequestHeader("token") String token) {
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userFileLambdaUpdateWrapper.set(UserFile::getDeleteFlag, 0)
                .set(UserFile::getDeleteBatchNum, "")
                .eq(UserFile::getDeleteBatchNum, restoreFileDto.getDeleteBatchNum());
        userFileService.update(userFileLambdaUpdateWrapper);

        String filePath = PathUtil.getParentPath(restoreFileDto.getFilePath());
        while(filePath.indexOf("/") != -1) {
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            filePath = PathUtil.getParentPath(filePath);
            LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UserFile::getFilePath, filePath + "/")
                    .eq(UserFile::getDeleteFlag, 0)
                    .eq(UserFile::getUserId, sessionUserBean.getUserId());
            List<UserFile> userFileList = userFileService.list(lambdaQueryWrapper);
            if (userFileList.size() == 0) {
                UserFile userFile = new UserFile();
                userFile.setUserId(sessionUserBean.getUserId());
                userFile.setFileName(fileName);
                userFile.setFilePath(filePath + "/");
                userFile.setDeleteFlag(0);
                userFile.setIsDir(1);
                userFile.setUploadTime(DateUtil.getCurrentTime());

                userFileService.save(userFile);
            }

        }

        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.select(UserFile::getFileName, UserFile::getFilePath)
                .likeRight(UserFile::getFilePath, restoreFileDto.getFilePath())
                .eq(UserFile::getIsDir, 1)
                .eq(UserFile::getDeleteFlag, 0)
                .groupBy(UserFile::getFilePath, UserFile::getFileName)
                .having("count(fileName) >= 2");
        List<UserFile> repeatList = userFileService.list(lambdaQueryWrapper);

        for (UserFile userFile : repeatList) {
            LambdaQueryWrapper<UserFile> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(UserFile::getFilePath, userFile.getFilePath())
                    .eq(UserFile::getFileName, userFile.getFileName())
                    .eq(UserFile::getDeleteFlag, "0");
            List<UserFile> userFiles = userFileService.list(lambdaQueryWrapper1);
            log.info("重复的文件:" + JSON.toJSONString(userFiles));
            for (int i = 0; i < userFiles.size() - 1; i ++) {
                log.info("删除文件：" + JSON.toJSONString(userFiles.get(i)));
                userFileService.removeById(userFiles.get(i).getUserFileId());
            }
        }

        log.info(JSON.toJSONString(repeatList));

        LambdaQueryWrapper<RecoveryFile> recoveryFileServiceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        recoveryFileServiceLambdaQueryWrapper.eq(RecoveryFile::getDeleteBatchNum, restoreFileDto.getDeleteBatchNum());
        recoveryFileService.remove(recoveryFileServiceLambdaQueryWrapper);


        return RestResult.success().message("还原成功！");
    }

}









