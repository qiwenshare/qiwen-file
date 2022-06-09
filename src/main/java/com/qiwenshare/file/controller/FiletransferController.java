package com.qiwenshare.file.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qiwenshare.common.anno.MyLog;
import com.qiwenshare.common.result.RestResult;
import com.qiwenshare.common.util.MimeUtils;
import com.qiwenshare.common.util.security.JwtUser;
import com.qiwenshare.common.util.security.SessionUtil;
import com.qiwenshare.file.api.IFileService;
import com.qiwenshare.file.api.IFiletransferService;
import com.qiwenshare.file.api.IUserFileService;
import com.qiwenshare.file.component.FileDealComp;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.StorageBean;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.dto.file.BatchDownloadFileDTO;
import com.qiwenshare.file.dto.file.DownloadFileDTO;
import com.qiwenshare.file.dto.file.PreviewDTO;
import com.qiwenshare.file.dto.file.UploadFileDTO;
import com.qiwenshare.file.io.QiwenFile;
import com.qiwenshare.file.service.StorageService;
import com.qiwenshare.file.vo.file.UploadFileVo;
import com.qiwenshare.ufop.factory.UFOPFactory;
import com.qiwenshare.ufop.operation.download.Downloader;
import com.qiwenshare.ufop.operation.download.domain.DownloadFile;
import com.qiwenshare.ufop.operation.download.domain.Range;
import com.qiwenshare.ufop.util.UFOPUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "filetransfer", description = "该接口为文件传输接口，主要用来做文件的上传、下载和预览")
@RestController
@RequestMapping("/filetransfer")
public class FiletransferController {

    @Resource
    IFiletransferService filetransferService;

    @Resource
    IFileService fileService;
    @Resource
    IUserFileService userFileService;
    @Resource
    FileDealComp fileDealComp;
    @Resource
    StorageService storageService;
    @Resource
    UFOPFactory ufopFactory;


    public static final String CURRENT_MODULE = "文件传输接口";

    @Operation(summary = "极速上传", description = "校验文件MD5判断文件是否存在，如果存在直接上传成功并返回skipUpload=true，如果不存在返回skipUpload=false需要再次调用该接口的POST方法", tags = {"filetransfer"})
    @RequestMapping(value = "/uploadfile", method = RequestMethod.GET)
    @MyLog(operation = "极速上传", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<UploadFileVo> uploadFileSpeed(UploadFileDTO uploadFileDto) {

        JwtUser sessionUserBean = SessionUtil.getSession();

        boolean isCheckSuccess = storageService.checkStorage(sessionUserBean.getUserId(), uploadFileDto.getTotalSize());
        if (!isCheckSuccess) {
            return RestResult.fail().message("存储空间不足");
        }
        UploadFileVo uploadFileVo = filetransferService.uploadFileSpeed(uploadFileDto);
        return RestResult.success().data(uploadFileVo);

    }

    @Operation(summary = "上传文件", description = "真正的上传文件接口", tags = {"filetransfer"})
    @RequestMapping(value = "/uploadfile", method = RequestMethod.POST)
    @MyLog(operation = "上传文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<UploadFileVo> uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto) {

        JwtUser sessionUserBean = SessionUtil.getSession();

        filetransferService.uploadFile(request, uploadFileDto, sessionUserBean.getUserId());

        UploadFileVo uploadFileVo = new UploadFileVo();
        return RestResult.success().data(uploadFileVo);

    }


    @Operation(summary = "下载文件", description = "下载文件接口", tags = {"filetransfer"})
    @MyLog(operation = "下载文件", module = CURRENT_MODULE)
    @RequestMapping(value = "/downloadfile", method = RequestMethod.GET)
    public void downloadFile(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO) {
        Cookie[] cookieArr = httpServletRequest.getCookies();
        String token = "";
        if (cookieArr != null) {
            for (Cookie cookie : cookieArr) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }
        boolean authResult = fileDealComp.checkAuthDownloadAndPreview(downloadFileDTO.getShareBatchNum(),
                downloadFileDTO.getExtractionCode(),
                token,
                downloadFileDTO.getUserFileId(), null);
        if (!authResult) {
            log.error("没有权限下载！！！");
            return;
        }
        httpServletResponse.setContentType("application/force-download");// 设置强制下载不打开
        UserFile userFile = userFileService.getById(downloadFileDTO.getUserFileId());
        String fileName = "";
        if (userFile.getIsDir() == 1) {
            fileName = userFile.getFileName() + ".zip";
        } else {
            fileName = userFile.getFileName() + "." + userFile.getExtendName();

        }
        try {
            fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        httpServletResponse.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名

        filetransferService.downloadFile(httpServletResponse, downloadFileDTO);
    }

    @Operation(summary = "批量下载文件", description = "批量下载文件", tags = {"filetransfer"})
    @RequestMapping(value = "/batchDownloadFile", method = RequestMethod.GET)
    @MyLog(operation = "批量下载文件", module = CURRENT_MODULE)
    @ResponseBody
    public void batchDownloadFile(HttpServletResponse httpServletResponse, BatchDownloadFileDTO batchDownloadFileDTO) {

        String files = batchDownloadFileDTO.getUserFileIds();
        String[] userFileIdStrs = files.split(",");
        List<String> userFileIds = new ArrayList<>();
        for(String userFileId : userFileIdStrs) {
            UserFile userFile = userFileService.getById(userFileId);
            if (userFile.getIsDir() == 0) {
                userFileIds.add(userFileId);
            } else {
                QiwenFile qiwenFile = new QiwenFile(userFile.getFilePath(), userFile.getFileName(), true);
                List<UserFile> userFileList = userFileService.selectUserFileByLikeRightFilePath(qiwenFile.getPath(), userFile.getUserId());
                List<String> userFileIds1 = userFileList.stream().map(UserFile::getUserFileId).collect(Collectors.toList());
                userFileIds.add(userFile.getUserFileId());
                userFileIds.addAll(userFileIds1);
            }
            
        }
        UserFile userFile = userFileService.getById(userFileIdStrs[0]);
        httpServletResponse.setContentType("application/force-download");// 设置强制下载不打开
        Date date = new Date();
        String fileName = String.valueOf(date.getTime());
        httpServletResponse.addHeader("Content-Disposition", "attachment;fileName=" + fileName + ".zip");// 设置文件名
        filetransferService.downloadUserFileList(httpServletResponse, userFile.getFilePath(), fileName, userFileIds);
    }

    @Operation(summary="预览文件", description="用于文件预览", tags = {"filetransfer"})
    @GetMapping("/preview")
    public void preview(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,  PreviewDTO previewDTO) throws IOException {

        if (previewDTO.getPlatform() != null && previewDTO.getPlatform() == 2) {
            filetransferService.previewPictureFile(httpServletResponse, previewDTO);
            return ;
        }
        String token = "";
        if (StringUtils.isNotEmpty(previewDTO.getToken())) {
            token = previewDTO.getToken();
        } else {
            Cookie[] cookieArr = httpServletRequest.getCookies();
            if (cookieArr != null) {
                for (Cookie cookie : cookieArr) {
                    if ("token".equals(cookie.getName())) {
                        token = cookie.getValue();
                    }
                }
            }
        }

        UserFile userFile = userFileService.getById(previewDTO.getUserFileId());
        boolean authResult = fileDealComp.checkAuthDownloadAndPreview(previewDTO.getShareBatchNum(),
                previewDTO.getExtractionCode(),
                token,
                previewDTO.getUserFileId(),
                previewDTO.getPlatform());

        if (!authResult) {
            log.error("没有权限预览！！！");
            return;
        }

        String fileName = userFile.getFileName() + "." + userFile.getExtendName();
        try {
            fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        httpServletResponse.addHeader("Content-Disposition", "fileName=" + fileName);// 设置文件名
        String mime = MimeUtils.getMime(userFile.getExtendName());
        httpServletResponse.setHeader("Content-Type", mime);

        FileBean fileBean = fileService.getById(userFile.getFileId());
        if ((UFOPUtils.isVideoFile(userFile.getExtendName()) || "mp3".equalsIgnoreCase(userFile.getExtendName()) || "flac".equalsIgnoreCase(userFile.getExtendName()))
                && !"true".equals(previewDTO.getIsMin())) {
            //获取从那个字节开始读取文件
            String rangeString = httpServletRequest.getHeader("Range");
            int start = 0;
            if (StringUtils.isNotBlank(rangeString)) {
                start = Integer.valueOf(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
            }

            Downloader downloader = ufopFactory.getDownloader(fileBean.getStorageType());
            DownloadFile downloadFile = new DownloadFile();
            downloadFile.setFileUrl(fileBean.getFileUrl());
            Range range = new Range();
            range.setStart(start);
            range.setLength(1024 * 1024 * 1);
            downloadFile.setRange(range);
            InputStream inputStream = downloader.getInputStream(downloadFile);

            OutputStream outputStream = httpServletResponse.getOutputStream();
            try {

                //返回码需要为206，代表只处理了部分请求，响应了部分数据
                httpServletResponse.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                // 每次请求只返回1MB的视频流

                httpServletResponse.setHeader("Accept-Ranges", "bytes");
                //设置此次相应返回的数据范围
                httpServletResponse.setHeader("Content-Range", "bytes " + start + "-" + (fileBean.getFileSize() - 1) + "/" + fileBean.getFileSize());
                IOUtils.copy(inputStream, outputStream);


            } finally {
                IOUtils.closeQuietly(inputStream);
                IOUtils.closeQuietly(outputStream);
                if (downloadFile.getOssClient() != null) {
                    downloadFile.getOssClient().shutdown();
                }
            }

        } else {
            filetransferService.previewFile(httpServletResponse, previewDTO);
        }

    }

    @Operation(summary = "获取存储信息", description = "获取存储信息", tags = {"filetransfer"})
    @RequestMapping(value = "/getstorage", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<StorageBean> getStorage() {

        JwtUser sessionUserBean = SessionUtil.getSession();
        StorageBean storageBean = new StorageBean();

        storageBean.setUserId(sessionUserBean.getUserId());


        Long storageSize = filetransferService.selectStorageSizeByUserId(sessionUserBean.getUserId());
        StorageBean storage = new StorageBean();
        storage.setUserId(sessionUserBean.getUserId());
        storage.setStorageSize(storageSize);
        Long totalStorageSize = storageService.getTotalStorageSize(sessionUserBean.getUserId());
        storage.setTotalStorageSize(totalStorageSize);
        return RestResult.success().data(storage);

    }


}
