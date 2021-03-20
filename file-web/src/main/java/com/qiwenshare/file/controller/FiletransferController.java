package com.qiwenshare.file.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.qiwenshare.common.cbb.DateUtil;
import com.qiwenshare.common.operation.FileOperation;
import com.qiwenshare.common.oss.AliyunOSSDownload;
import com.qiwenshare.common.util.FileUtil;
import com.qiwenshare.common.cbb.RestResult;
import com.qiwenshare.common.util.PathUtil;
import com.qiwenshare.file.anno.MyLog;
import com.qiwenshare.file.api.IFileService;
import com.qiwenshare.file.api.IFiletransferService;
import com.qiwenshare.file.api.IUserFileService;
import com.qiwenshare.file.api.IUserService;
import com.qiwenshare.file.config.QiwenFileConfig;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.StorageBean;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.dto.DownloadFileDTO;
import com.qiwenshare.file.dto.UploadFileDTO;
import com.qiwenshare.file.vo.file.UploadFileVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "filetransfer", description = "该接口为文件传输接口，主要用来做文件的上传和下载")
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
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    public static final String CURRENT_MODULE = "文件传输接口";

    @Operation(summary = "极速上传", description = "校验文件MD5判断文件是否存在，如果存在直接上传成功并返回skipUpload=true，如果不存在返回skipUpload=false需要再次调用该接口的POST方法", tags = {"filetransfer"})
    @RequestMapping(value = "/uploadfile", method = RequestMethod.GET)
    @MyLog(operation = "极速上传", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<UploadFileVo> uploadFileSpeed(UploadFileDTO uploadFileDto, @RequestHeader("token") String token) {

        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        if (sessionUserBean == null){

            return RestResult.fail().message("未登录");
        }
        RestResult<String> operationCheckResult = fileController.operationCheck(token);
        if (!operationCheckResult.getSuccess()){
            return RestResult.fail().message("没权限，请联系管理员！");
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
                userFile.setExtendName(FileUtil.getFileExtendName(fileName));
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
        return RestResult.success().data(uploadFileVo);

    }

    /**
     * 上传文件
     *
     * @param request
     * @return
     */
    @Operation(summary = "上传文件", description = "真正的上传文件接口", tags = {"filetransfer"})
    @RequestMapping(value = "/uploadfile", method = RequestMethod.POST)
    @MyLog(operation = "上传文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<UploadFileVo> uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto, @RequestHeader("token") String token) {

        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        if (sessionUserBean == null){
            return RestResult.fail().message("未登录");
        }
        RestResult<String> operationCheckResult = fileController.operationCheck(token);
        if (!operationCheckResult.getSuccess()){
            return RestResult.fail().message("没权限，请联系管理员！");
        }

        filetransferService.uploadFile(request, uploadFileDto, sessionUserBean.getUserId());
        UploadFileVo uploadFileVo = new UploadFileVo();
        return RestResult.success().data(uploadFileVo);

    }

    /**
     * 下载文件
     *
     * @return
     */
    @Operation(summary = "下载文件", description = "下载文件接口", tags = {"filetransfer"})
    @MyLog(operation = "下载文件", module = CURRENT_MODULE)
    @RequestMapping(value = "/downloadfile", method = RequestMethod.GET)
    public void downloadFile(HttpServletResponse response, DownloadFileDTO downloadFileDTO) {
        UserFile userFile = userFileService.getById(downloadFileDTO.getUserFileId());

        String fileName = userFile.getFileName() + "." + userFile.getExtendName();
        try {
            fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("application/force-download");// 设置强制下载不打开
        response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名


        FileBean fileBean = fileService.getById(userFile.getFileId());
        if (fileBean.getIsOSS() != null && fileBean.getIsOSS() == 1) {
            aliyunDownload(response, fileBean);
        } else {
            localFileDownload(response, fileBean);
        }

    }

    private void localFileDownload(HttpServletResponse response, FileBean fileBean) {
        BufferedInputStream bis = null;
        byte[] buffer = new byte[1024];
        //设置文件路径
        File file = FileOperation.newFile(PathUtil.getStaticPath() + fileBean.getFileUrl());
        if (file.exists()) {


            FileInputStream fis = null;

            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void aliyunDownload(HttpServletResponse response, FileBean fileBean) {
        BufferedInputStream bis = null;
        byte[] buffer = new byte[1024];
        AliyunOSSDownload aliyunOSSDownload= new AliyunOSSDownload();
        OSS ossClient = aliyunOSSDownload.createOSSClient(qiwenFileConfig.getAliyun().getOss());
        OSSObject ossObject = ossClient.getObject(qiwenFileConfig.getAliyun().getOss().getBucketName(), fileBean.getTimeStampName());
        InputStream inputStream = ossObject.getObjectContent();
        try {
            bis = new BufferedInputStream(inputStream);
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        ossClient.shutdown();
    }


    public void fastFDSDownload(HttpServletResponse response, FileBean fileBean) throws IOException {
        String group = fileBean.getFileUrl().substring(0, fileBean.getFileUrl().indexOf("/"));
        String path = fileBean.getFileUrl().substring(fileBean.getFileUrl().indexOf("/") + 1);
        DownloadByteArray downloadByteArray = new DownloadByteArray();
        byte[] bytes = fastFileStorageClient.downloadFile(group, path, downloadByteArray);

        // 这里只是为了整合fastdfs，所以写死了文件格式。需要在上传的时候保存文件名。下载的时候使用对应的格式
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("sb.xlsx", "UTF-8"));
        response.setCharacterEncoding("UTF-8");
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Operation(summary = "获取存储信息", description = "获取存储信息", tags = {"filetransfer"})
    @RequestMapping(value = "/getstorage", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<StorageBean> getStorage(@RequestHeader("token") String token) {

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
        return RestResult.success().data(storage);

    }


}
