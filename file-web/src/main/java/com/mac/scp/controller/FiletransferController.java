package com.mac.scp.controller;

import com.alibaba.fastjson.JSON;
import com.mac.common.operation.FileOperation;
import com.mac.common.util.PathUtil;
import com.mac.common.cbb.RestResult;
import com.mac.common.operation.ImageOperation;
import com.mac.scp.api.IFileService;
import com.mac.scp.api.IFiletransferService;
import com.mac.scp.domain.*;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.List;

@RestController
@RequestMapping("/filetransfer")
public class FiletransferController {

    @Resource
    IFiletransferService filetransferService;
    @Resource
    IFileService fileService;

    /**
     * 删除用户头像
     *
     * @param request 请求
     * @return 删除结果
     */
    @RequestMapping("/deleteuserimage")
    @ResponseBody
    public String deleteUserImage(HttpServletRequest request, UserImageBean userImageBean) {
        RestResult<String> result = new RestResult<String>();
        String servletPath = request.getServletPath();
        String realPath = request.getSession().getServletContext().getRealPath(servletPath);

        List<UserImageBean> userImageList = filetransferService.selectUserImageByUrl(userImageBean.getImageurl().replace("filetransfer/", ""));
        if (userImageList.size() <= 0) {
            result.setSuccess(false);
            result.setErrorMessage("文件路径不正确");
            return JSON.toJSONString(result);
        }
        String fileRealPath = new File(new File(realPath).getParent()).getParent() + "/" + userImageBean.getImageurl();
        File file = new File(fileRealPath);
        filetransferService.deleteUserImageById(userImageBean);

        if (file.isFile() && file.exists()) {
            boolean isDeleteSuccess = file.delete();
            if (isDeleteSuccess) {
                result.setSuccess(true);
            } else {
                result.setSuccess(false);
                result.setErrorMessage("文件删除失败");
            }
        } else {
            result.setSuccess(true);
            result.setErrorMessage("文件不存在");
        }

        String resultJson = JSON.toJSONString(result);
        return resultJson;
    }

    /**
     * 旋转图片
     * @param direction 方向
     * @param imageid 图片id
     * @return 返回结果
     */
    @RequestMapping("/totationimage")
    @ResponseBody
    public RestResult<String> totationImage(String direction, int imageid){
        RestResult<String> result = new RestResult<String>();
        FileBean fileBean = new FileBean();
        fileBean.setFileid(imageid);
        fileBean = fileService.selectFileById(fileBean);
        String imageUrl = fileBean.getFileurl();
        String extendName = fileBean.getExtendname();
        File file = FileOperation.newFile(PathUtil.getStaticPath() + imageUrl);
        File minfile = FileOperation.newFile(PathUtil.getStaticPath() + imageUrl.replace("." + extendName, "_min." + extendName));
        if ("left".equals(direction)){
            try {
                ImageOperation.leftTotation(file, file, 90);
                ImageOperation.leftTotation(minfile, minfile, 90);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if ("right".equals(direction)){
            try {
                ImageOperation.rightTotation(file, file, 90);
                ImageOperation.rightTotation(minfile, minfile, 90);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        result.setSuccess(true);
        return result;
    }

    /**
     * 批量删除图片
     *
     * @return
     */
    @RequestMapping("/deleteimagebyids")
    @ResponseBody
    public String deleteImageByIds(String imageids) {
        RestResult<String> result = new RestResult<String>();
        List<Integer> imageidList = JSON.parseArray(imageids, Integer.class);
        UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();

        long sessionUserId = sessionUserBean.getUserId();

//        List<ImageBean> imageBeanList = filetransferService.selectUserImageByIds(imageidList);
//        filetransferService.deleteUserImageByIds(imageidList);
        List<FileBean> fileList = fileService.selectFileListByIds(imageidList);
        fileService.deleteFileByIds(imageidList);
        long totalFileSize = 0;
        for (FileBean fileBean : fileList) {
            String imageUrl = PathUtil.getStaticPath() + fileBean.getFileurl();
            String minImageUrl = imageUrl.replace("." + fileBean.getExtendname(), "_min." + fileBean.getExtendname());
            totalFileSize += FileOperation.getFileSize(imageUrl);
            FileOperation.deleteFile(imageUrl);
            FileOperation.deleteFile(minImageUrl);
        }
        StorageBean storageBean = filetransferService.selectStorageBean(new StorageBean(sessionUserId));
        if (storageBean != null){
            long updateFileSize = storageBean.getStoragesize() - totalFileSize;
            if (updateFileSize < 0){
                updateFileSize = 0;
            }
            storageBean.setStoragesize(updateFileSize);
            filetransferService.updateStorageBean(storageBean);

        }

        result.setData("删除文件成功");
        result.setSuccess(true);
        String resultJson = JSON.toJSONString(result);
        return resultJson;
    }

    /**
     * 删除图片
     *
     * @param request
     * @return
     */
    @RequestMapping("/deleteimage")
    @ResponseBody
    public String deleteImage(HttpServletRequest request, FileBean fileBean) {
        RestResult<String> result = new RestResult<String>();
        UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        long sessionUserId = sessionUserBean.getUserId();
        String imageUrl = PathUtil.getStaticPath() + fileBean.getFileurl();
        String minImageUrl = imageUrl.replace("." + fileBean.getExtendname(), "_min." + fileBean.getExtendname());
        long fileSize = FileOperation.getFileSize(imageUrl);
        fileBean.setIsdir(0);
        //filetransferService.deleteImageById(fileBean);
        fileService.deleteFile(fileBean);

        FileOperation.deleteFile(imageUrl);
        FileOperation.deleteFile(minImageUrl);


        StorageBean storageBean = filetransferService.selectStorageBean(new StorageBean(sessionUserId));
        if (storageBean != null){
            long updateFileSize = storageBean.getStoragesize() - fileSize;
            if (updateFileSize < 0){
                updateFileSize = 0;
            }
            storageBean.setStoragesize(updateFileSize);
            filetransferService.updateStorageBean(storageBean);

        }

        String resultJson = JSON.toJSONString(result);
        return resultJson;
    }



    /**
     * 上传头像
     *
     * @param request
     * @return
     */
    @RequestMapping("/uploadimg")
    @ResponseBody
    public String uploadImg(HttpServletRequest request) {
        RestResult<String> restResult = filetransferService.uploadUserImage(request);
        String resultJson = JSON.toJSONString(restResult);
        return resultJson;
    }

    /**
     * 上传文件
     *
     * @param request
     * @return
     */
    @RequestMapping("/uploadfile")
    @ResponseBody
    public String uploadFile(HttpServletRequest request, FileBean fileBean) {
        RestResult<String> restResult = new RestResult<String>();
        UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        RestResult<String> operationCheckResult = new FileController().operationCheck();
        if (!operationCheckResult.isSuccess()){
            return JSON.toJSONString(operationCheckResult);
        }

        fileBean.setUserid(sessionUserBean.getUserId());

        filetransferService.uploadFile(request, fileBean);

        restResult.setSuccess(true);
        String resultJson = JSON.toJSONString(restResult);
        return resultJson;
    }
    /**
     * 下载文件
     *
     * @return
     */
    @RequestMapping("/downloadfile")
    public String downloadFile(HttpServletResponse response, FileBean fileBean){
        RestResult<String> restResult =  new RestResult<>();
        String fileName = null;// 文件名
        try {
            fileName = new String(fileBean.getFilename().getBytes("utf-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (fileName != null) {
            fileName = fileName + "." + fileBean.getExtendname();
            //设置文件路径
            File file = FileOperation.newFile(PathUtil.getStaticPath() + fileBean.getFileurl());
            if (file.exists()) {
                response.setContentType("application/force-download");// 设置强制下载不打开
                response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
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
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;

    }

    /**
     * 得到用户头像列表
     *
     * @return
     */
    @RequestMapping("/getuploadimglist")
    @ResponseBody
    public RestResult<List<UserImageBean>> getUploadImgList(HttpServletRequest request) {
        UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        RestResult<List<UserImageBean>> restResult = filetransferService.selectUserImage(sessionUserBean.getUserId());
        restResult.setSuccess(true);

        return restResult;
    }

    /**
     * 获取存储信息
     *
     * @return
     */
    @RequestMapping("/getstorage")
    @ResponseBody
    public RestResult<StorageBean> getStorage() {
        RestResult<StorageBean> restResult = new RestResult<StorageBean>();
        UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        StorageBean storageBean = new StorageBean();
        if (FileController.isShareFile){
            storageBean.setUserid(2);
        }else{
            storageBean.setUserid(sessionUserBean.getUserId());
        }

        StorageBean storage = filetransferService.selectStorageByUser(storageBean);
        restResult.setData(storage);
        restResult.setSuccess(true);
        return restResult;
    }



}
