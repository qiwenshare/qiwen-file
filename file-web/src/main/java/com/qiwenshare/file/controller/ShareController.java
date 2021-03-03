package com.qiwenshare.file.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qiwenshare.common.cbb.DateUtil;
import com.qiwenshare.common.cbb.RestResult;
import com.qiwenshare.file.anno.MyLog;
import com.qiwenshare.file.api.IShareService;
import com.qiwenshare.file.api.IUserService;
import com.qiwenshare.file.domain.Share;
import com.qiwenshare.file.domain.ShareFile;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.dto.sharefile.CheckExtractionCodeDTO;
import com.qiwenshare.file.dto.sharefile.ShareFileListBySecretDTO;
import com.qiwenshare.file.dto.sharefile.ShareSecretDTO;
import com.qiwenshare.file.service.ShareService;
import com.qiwenshare.file.service.UserService;
import com.qiwenshare.file.vo.share.ShareSecretVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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

    @Operation(summary = "私密分享", description = "私密分享", tags = {"share"})
    @PostMapping(value = "/sharesecret")
    @MyLog(operation = "私密分享", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<ShareSecretVO> shareSecret(ShareSecretDTO shareSecretDTO, @RequestHeader("token") String token) {
        ShareSecretVO shareSecretVO = new ShareSecretVO();
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        Share share = new Share();
        BeanUtil.copyProperties(sessionUserBean, share);
        share.setShareTime(DateUtil.getCurrentTime());
        share.setUserId(sessionUserBean.getUserId());
        share.setShareStatus(0);
        shareService.save(share);
        String uuid = UUID.randomUUID().toString();
        List<ShareFile> fileList = JSON.parseArray(shareSecretDTO.getFiles(), ShareFile.class);
        fileList.forEach(p->p.setShareBatchNum("S@#" + uuid.replace("-", "")));
        shareService.batchInsertShareFile(fileList);
        shareSecretVO.setShareBatchNum("S@#" + uuid.replace("-", ""));
        return RestResult.success().data(shareSecretVO);
    }

    @Operation(summary = "分享列表", description = "分享列表", tags = {"share"})
    @GetMapping(value = "/sharefileList")
    @ResponseBody
    public RestResult shareFileListBySecret(ShareFileListBySecretDTO shareFileListBySecretDTO) {
        log.info(JSON.toJSONString(shareFileListBySecretDTO));
        LambdaQueryWrapper<Share> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Share::getShareBatchNum, shareFileListBySecretDTO.getShareBatchNum());
        List<Share> list = shareService.list(lambdaQueryWrapper);
        return RestResult.success().data(list.get(0));
    }

    @Operation(summary = "校验提取码", description = "校验提取码", tags = {"share"})
    @GetMapping(value = "/checkextractioncode")
    @ResponseBody
    public RestResult checkExtractionCode(CheckExtractionCodeDTO checkExtractionCodeDTO) {
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
}
