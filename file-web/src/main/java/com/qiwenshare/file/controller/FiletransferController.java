package com.qiwenshare.file.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.qiwenshare.common.cbb.DateUtil;
import com.qiwenshare.common.operation.FileOperation;
import com.qiwenshare.common.oss.AliyunOSSDownload;
import com.qiwenshare.common.util.FileUtil;
import com.qiwenshare.common.util.PathUtil;
import com.qiwenshare.common.cbb.RestResult;
import com.qiwenshare.file.api.IFileService;
import com.qiwenshare.file.api.IFiletransferService;
import com.qiwenshare.file.api.IUserFileService;
import com.qiwenshare.file.api.IUserService;
import com.qiwenshare.file.config.QiwenFileConfig;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.StorageBean;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.dto.UploadFileDto;
import com.qiwenshare.file.vo.file.UploadFileVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/filetransfer")
public class FiletransferController {

    @Resource
    IFiletransferService filetransferService;

    @Resource
    FileController fileController;

    @Autowired
    QiwenFileConfig qiwenFileConfig;
    @Resource
    IFileService fileService;
    @Resource
    IUserService userService;
    @Resource
    IUserFileService userFileService;

    /**
     * 上传文件
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/uploadfile", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<UploadFileVo> uploadFileSpeed(HttpServletRequest request, UploadFileDto uploadFileDto, @RequestHeader("token") String token) {
        RestResult<UploadFileVo> restResult = new RestResult<UploadFileVo>();
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        if (sessionUserBean == null){
            restResult.setSuccess(false);
            restResult.setErrorMessage("未登录");
            return restResult;
        }
        RestResult<String> operationCheckResult = fileController.operationCheck(token);
        if (!operationCheckResult.isSuccess()){
            restResult.setSuccess(false);
            restResult.setErrorMessage("没权限，请联系管理员！");
            return restResult;
        }
        UploadFileVo uploadFileVo = new UploadFileVo();
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("identifier", uploadFileDto.getIdentifier());
        synchronized (FiletransferController.class) {
            List<FileBean> list = fileService.listByMap(param);
            if (list != null && !list.isEmpty()) {
                FileBean file = list.get(0);

                UserFile userFile = new UserFile();
                userFile.setFileId(file.getFileId());
                userFile.setUserId(sessionUserBean.getUserId());
                userFile.setFilePath(uploadFileDto.getFilePath());
                String fileName = uploadFileDto.getFilename();
                userFile.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
                userFile.setExtendName(FileUtil.getFileType(fileName));
                userFile.setDeleteFlag(0);
                userFile.setIsDir(0);
                userFile.setUploadTime(DateUtil.getCurrentTime());
                userFileService.save(userFile);
                fileService.increaseFilePointCount(file.getFileId());
                uploadFileVo.setSkipUpload(true);

            } else {
                uploadFileVo.setSkipUpload(false);

            }
        }

        restResult.setData(uploadFileVo);
        restResult.setSuccess(true);
        return restResult;
    }

    /**
     * 上传文件
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/uploadfile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<UploadFileVo> uploadFile(HttpServletRequest request, UploadFileDto uploadFileDto, @RequestHeader("token") String token) {
        RestResult<UploadFileVo> restResult = new RestResult<>();
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        if (sessionUserBean == null){
            restResult.setSuccess(false);
            restResult.setErrorMessage("未登录");
            return restResult;
        }
        RestResult<String> operationCheckResult = fileController.operationCheck(token);
        if (!operationCheckResult.isSuccess()){
            restResult.setSuccess(false);
            restResult.setErrorMessage("没权限，请联系管理员！");
            return restResult;
        }

        filetransferService.uploadFile(request, uploadFileDto, sessionUserBean.getUserId());
        UploadFileVo uploadFileVo = new UploadFileVo();

        restResult.setData(uploadFileVo);
        return restResult;
    }

    /**
     * 获取存储信息
     *
     * @return
     */
    @RequestMapping(value = "/getstorage", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<StorageBean> getStorage(@RequestHeader("token") String token) {
        RestResult<StorageBean> restResult = new RestResult<StorageBean>();

        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        StorageBean storageBean = new StorageBean();
        if (qiwenFileConfig.isShareMode()){
            storageBean.setUserId(2L);
        }else{
            storageBean.setUserId(sessionUserBean.getUserId());
        }

        Long storageSize = filetransferService.selectStorageSizeByUserId(sessionUserBean.getUserId());
        StorageBean storage = new StorageBean();
        storage.setUserId(sessionUserBean.getUserId());
        storage.setStorageSize(storageSize);
        restResult.setData(storage);
        restResult.setSuccess(true);
        return restResult;
    }


}
