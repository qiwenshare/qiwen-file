package com.qiwenshare.file.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.common.result.RestResult;
import com.qiwenshare.file.anno.MyLog;
import com.qiwenshare.file.api.*;
import com.qiwenshare.file.domain.Share;
import com.qiwenshare.file.domain.ShareFile;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.dto.sharefile.*;
import com.qiwenshare.file.vo.share.ShareFileListVO;
import com.qiwenshare.file.vo.share.ShareFileVO;
import com.qiwenshare.file.vo.share.ShareTypeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Tag(name = "share", description = "该接口为文件分享接口")
@RestController
@Slf4j
@RequestMapping("/share")
public class ShareController {

    public static final String CURRENT_MODULE = "文件分享";
    @Resource
    IUserService userService;
    @Resource
    IShareService shareService;
    @Resource
    IFileService fileService;
    @Resource
    IUserFileService userFileService;

    @Operation(summary = "分享文件", description = "分享文件统一接口", tags = {"share"})
    @PostMapping(value = "/sharefile")
    @MyLog(operation = "分享文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<ShareFileVO> shareFile( @RequestBody ShareFileDTO shareSecretDTO, @RequestHeader("token") String token) {
        ShareFileVO shareSecretVO = new ShareFileVO();
        UserBean sessionUserBean = userService.getUserBeanByToken(token);

        String uuid = UUID.randomUUID().toString().replace("-", "");
        Share share = new Share();
        BeanUtil.copyProperties(shareSecretDTO, share);
        share.setShareTime(DateUtil.getCurrentTime());
        share.setUserId(sessionUserBean.getUserId());
        share.setShareStatus(0);
        if (shareSecretDTO.getShareType() == 1) {
            String extractionCode = RandomUtil.randomNumbers(6);
            share.setExtractionCode(extractionCode);
            shareSecretVO.setExtractionCode(share.getExtractionCode());
        }

        share.setShareBatchNum(uuid);
        shareService.save(share);

        List<ShareFile> fileList = JSON.parseArray(shareSecretDTO.getFiles(), ShareFile.class);
        List<ShareFile> saveFileList = new ArrayList<>();
        for (ShareFile shareFile : fileList) {
            UserFile userFile = userFileService.getById(shareFile.getUserFileId());
            if (userFile.getIsDir() == 1) {
                List<UserFile> userfileList = userFileService.selectFileListLikeRightFilePath(userFile.getFilePath(), sessionUserBean.getUserId());
                for (UserFile userFile1 : userfileList) {
                    ShareFile shareFile1 = new ShareFile();
                    shareFile1.setUserFileId(userFile1.getUserFileId());
                    shareFile1.setShareBatchNum(uuid);
                    saveFileList.add(shareFile1);
                }
            } else {
                shareFile.setShareBatchNum(uuid);
                saveFileList.add(shareFile);
            }

        }
        shareService.batchInsertShareFile(saveFileList);
        shareSecretVO.setShareBatchNum(uuid);

        return RestResult.success().data(shareSecretVO);
    }

    @Operation(summary = "保存分享文件", description = "用来将别人分享的文件保存到自己的网盘中", tags = {"share"})
    @PostMapping(value = "/sharefile")
    @MyLog(operation = "保存分享文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult saveShareFile(@RequestBody SaveShareFileDTO saveShareFileDTO, @RequestHeader("token") String token) {
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        List<ShareFile> fileList = JSON.parseArray(saveShareFileDTO.getFiles(), ShareFile.class);

        List<UserFile> saveUserFileList = new ArrayList<>();
        for (ShareFile shareFile : fileList) {
            UserFile userFile = userFileService.getById(shareFile.getUserFileId());
            if (userFile.getIsDir() == 1) {
                List<UserFile> userfileList = userFileService.selectFileListLikeRightFilePath(userFile.getFilePath(), sessionUserBean.getUserId());
                for (UserFile userFile1 : userfileList) {
                    userFile1.setUserId(sessionUserBean.getUserId());
                    userFile1.setFilePath(saveShareFileDTO.getFilePath());
                    saveUserFileList.add(userFile1);
                    fileService.increaseFilePointCount(userFile1.getFileId());
                }
            } else {
                userFile.setUserFileId(null);
                userFile.setUserId(sessionUserBean.getUserId());
                userFile.setFilePath(saveShareFileDTO.getFilePath());
                saveUserFileList.add(userFile);
                fileService.increaseFilePointCount(userFile.getFileId());
            }
        }
        userFileService.saveBatch(saveUserFileList);

        return RestResult.success();
    }

    public static void main(String[] args) {
        String sss = "[{\"fileId\":null,\"timeStampName\":null,\"fileUrl\":null,\"fileSize\":null,\"isOSS\":null,\"storageType\":null,\"pointCount\":null,\"identifier\":null,\"userFileId\":619,\"userId\":2,\"fileName\":\"2222\",\"filePath\":\"/\",\"extendName\":null,\"isDir\":1,\"uploadTime\":\"2021-03-15 22:16:26\",\"deleteFlag\":0,\"deleteTime\":null,\"deleteBatchNum\":null}]";
        List<ShareFile> fileList = JSON.parseArray(sss, ShareFile.class);
        fileList.forEach(p->p.setShareBatchNum("123"));
        System.out.println(fileList);

    }

    @Operation(summary = "分享列表", description = "分享列表", tags = {"share"})
    @GetMapping(value = "/sharefileList")
    @ResponseBody
    public RestResult<List<ShareFileListVO>> shareFileListBySecret(ShareFileListBySecretDTO shareFileListBySecretDTO) {
        log.info(JSON.toJSONString(shareFileListBySecretDTO));
        List<ShareFileListVO> list = shareService.selectShareFileList(shareFileListBySecretDTO.getShareBatchNum(), shareFileListBySecretDTO.getFilePath());
        return RestResult.success().data(list);
    }

    @Operation(summary = "分享类型", description = "可用此接口判断是否需要提取码", tags = {"share"})
    @GetMapping(value = "/sharetype")
    @ResponseBody
    public RestResult<ShareTypeVO> shareType(ShareTypeDTO shareTypeDTO) {
        LambdaQueryWrapper<Share> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Share::getShareBatchNum, shareTypeDTO.getShareBatchNum());
        Share share = shareService.getOne(lambdaQueryWrapper);
        ShareTypeVO shareTypeVO = new ShareTypeVO();
        shareTypeVO.setShareType(share.getShareType());
        return RestResult.success().data(shareTypeVO);

    }

    @Operation(summary = "校验提取码", description = "校验提取码", tags = {"share"})
    @GetMapping(value = "/checkextractioncode")
    @ResponseBody
    public RestResult<String> checkExtractionCode(CheckExtractionCodeDTO checkExtractionCodeDTO) {
//        log.info(JSON.toJSONString(shareFileListBySecretDTO));
        LambdaQueryWrapper<Share> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Share::getShareBatchNum, checkExtractionCodeDTO.getShareBatchNum())
                .eq(Share::getExtractionCode, checkExtractionCodeDTO.getExtractionCode());
        List<Share> list = shareService.list(lambdaQueryWrapper);
        if (list.isEmpty()) {
            return RestResult.fail().message("校验失败");
        } else {
            return RestResult.success();
        }
    }

    @Operation(summary = "校验过期时间", description = "校验过期时间", tags = {"share"})
    @GetMapping(value = "/checkendtime")
    @ResponseBody
    public RestResult<String> checkEndTime(CheckEndTimeDTO checkEndTimeDTO) {
        LambdaQueryWrapper<Share> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Share::getShareBatchNum, checkEndTimeDTO.getShareBatchNum());
        Share share = shareService.getOne(lambdaQueryWrapper);
        String endTime = share.getEndTime();
        Date endTimeDate = null;
        try {
            endTimeDate = DateUtil.getDateByFormatString(endTime, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            log.error("日期解析失败：{}" , e);
        }
        if (new Date().after(endTimeDate))  {
            return RestResult.fail().message("分享已过期");
        } else {
            return RestResult.success();
        }
    }
}
