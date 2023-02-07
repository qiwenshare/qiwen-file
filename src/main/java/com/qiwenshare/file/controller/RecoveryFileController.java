package com.qiwenshare.file.controller;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qiwenshare.common.anno.MyLog;
import com.qiwenshare.common.result.RestResult;
import com.qiwenshare.common.util.security.JwtUser;
import com.qiwenshare.common.util.security.SessionUtil;
import com.qiwenshare.file.api.*;
import com.qiwenshare.file.component.AsyncTaskComp;
import com.qiwenshare.file.domain.RecoveryFile;
import com.qiwenshare.file.dto.file.DeleteRecoveryFileDTO;
import com.qiwenshare.file.dto.recoveryfile.BatchDeleteRecoveryFileDTO;
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
    @Resource
    IFileService fileService;
    @Resource
    IFiletransferService filetransferService;
    @Resource
    AsyncTaskComp asyncTaskComp;


    public static final String CURRENT_MODULE = "回收站文件接口";

    @Operation(summary = "删除回收文件", description = "删除回收文件", tags = {"recoveryfile"})
    @MyLog(operation = "删除回收文件", module = CURRENT_MODULE)
    @RequestMapping(value = "/deleterecoveryfile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> deleteRecoveryFile(@RequestBody DeleteRecoveryFileDTO deleteRecoveryFileDTO) {
        RecoveryFile recoveryFile = recoveryFileService.getOne(new QueryWrapper<RecoveryFile>().lambda().eq(RecoveryFile::getUserFileId, deleteRecoveryFileDTO.getUserFileId()));

        asyncTaskComp.deleteUserFile(recoveryFile.getUserFileId());

        recoveryFileService.removeById(recoveryFile.getRecoveryFileId());
        return RestResult.success().data("删除成功");
    }

    @Operation(summary = "批量删除回收文件", description = "批量删除回收文件", tags = {"recoveryfile"})
    @RequestMapping(value = "/batchdelete", method = RequestMethod.POST)
    @MyLog(operation = "批量删除回收文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<String> batchDeleteRecoveryFile(@RequestBody BatchDeleteRecoveryFileDTO batchDeleteRecoveryFileDTO) {
        String userFileIds = batchDeleteRecoveryFileDTO.getUserFileIds();
        String[] userFileIdList = userFileIds.split(",");
        for (String userFileId : userFileIdList) {
            RecoveryFile recoveryFile = recoveryFileService.getOne(new QueryWrapper<RecoveryFile>().lambda().eq(RecoveryFile::getUserFileId, userFileId));

            if (recoveryFile != null) {
                asyncTaskComp.deleteUserFile(recoveryFile.getUserFileId());

                recoveryFileService.removeById(recoveryFile.getRecoveryFileId());
            }

        }
        return RestResult.success().data("批量删除成功");
    }

    @Operation(summary = "回收文件列表", description = "回收文件列表", tags = {"recoveryfile"})
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<RecoveryFileListVo> getRecoveryFileList() {
        JwtUser sessionUserBean = SessionUtil.getSession();
        List<RecoveryFileListVo> recoveryFileList = recoveryFileService.selectRecoveryFileList(sessionUserBean.getUserId());
        return RestResult.success().dataList(recoveryFileList, recoveryFileList.size());
    }

    @Operation(summary = "还原文件", description = "还原文件", tags = {"recoveryfile"})
    @RequestMapping(value = "/restorefile", method = RequestMethod.POST)
    @MyLog(operation = "还原文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult restoreFile(@RequestBody RestoreFileDTO restoreFileDto) {
        JwtUser sessionUserBean = SessionUtil.getSession();
        recoveryFileService.restorefile(restoreFileDto.getDeleteBatchNum(), restoreFileDto.getFilePath(), sessionUserBean.getUserId());
        return RestResult.success().message("还原成功！");
    }

}









