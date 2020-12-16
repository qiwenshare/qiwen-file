package com.qiwenshare.file.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qiwenshare.common.cbb.DateUtil;
import com.qiwenshare.common.cbb.RestResult;
import com.qiwenshare.common.operation.FileOperation;
import com.qiwenshare.common.oss.AliyunOSSRename;
import com.qiwenshare.common.util.FileUtil;
import com.qiwenshare.common.util.PathUtil;
import com.qiwenshare.file.api.IFileService;
import com.qiwenshare.file.api.IUserService;
import com.qiwenshare.file.config.QiwenFileConfig;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.TreeNode;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.dto.*;
import com.qiwenshare.file.service.UserFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.qiwenshare.common.util.FileUtil.getFileExtendsByType;

@RestController
@Slf4j
@RequestMapping("/file")
public class FileController {

    @Resource
    IFileService fileService;
    @Resource
    IUserService userService;
    @Resource
    UserFileService userFileService;

    @Resource
    QiwenFileConfig qiwenFileConfig;
    public static Executor executor = Executors.newFixedThreadPool(20);

    public static int COMPLETE_COUNT = 0;

    public static long treeid = 0;


    /**
     * 创建文件
     *
     * @return
     */
    @RequestMapping(value = "/createfile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> createFile(@RequestBody CreateFileDto createFileDto, @RequestHeader("token") String token) {
        RestResult<String> restResult = new RestResult<>();
        if (!operationCheck(token).isSuccess()){
            return operationCheck(token);
        }

        UserBean sessionUserBean = userService.getUserBeanByToken(token);

        List<UserFile> userFiles = userFileService.selectUserFileByNameAndPath(createFileDto.getFileName(), createFileDto.getFilePath(), sessionUserBean.getUserId());
        if (userFiles != null && !userFiles.isEmpty()) {
            restResult.setErrorMessage("同名文件已存在");
            restResult.setSuccess(false);
            return restResult;
        }

        UserFile userFile = new UserFile();
        userFile.setUserId(sessionUserBean.getUserId());
        userFile.setFileName(createFileDto.getFileName());
        userFile.setFilePath(createFileDto.getFilePath());
        userFile.setDeleteFlag(0);
        userFile.setIsDir(1);
        userFile.setUploadTime(DateUtil.getCurrentTime());

        userFileService.save(userFile);

        restResult.setSuccess(true);
        return restResult;
    }

    /**
     * 文件重命名
     *
     * @return
     */
    @RequestMapping(value = "/renamefile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> renameFile(@RequestBody RenameFileDto renameFileDto, @RequestHeader("token") String token) {
        RestResult<String> restResult = new RestResult<>();
        if (!operationCheck(token).isSuccess()){
            return operationCheck(token);
        }
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
//        fileBean.setUserId(sessionUserBean.getUserId());
//        fileBean.setUploadTime(DateUtil.getCurrentTime());
        List<UserFile> userFiles = userFileService.selectUserFileByNameAndPath(renameFileDto.getFileName(), renameFileDto.getFilePath(), sessionUserBean.getUserId());
        if (userFiles != null && !userFiles.isEmpty()) {
            restResult.setErrorMessage("同名文件已存在");
            restResult.setSuccess(false);
            return restResult;
        }
        if (1 == renameFileDto.getIsDir()) {
            LambdaUpdateWrapper<UserFile> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.set(UserFile::getFileName, renameFileDto.getFileName())
                    .set(UserFile::getUploadTime, DateUtil.getCurrentTime())
                    .eq(UserFile::getUserFileId, renameFileDto.getUserFileId());
            userFileService.update(lambdaUpdateWrapper);
            userFileService.replaceUserFilePath(renameFileDto.getFilePath() + renameFileDto.getFileName() + "/",
                    renameFileDto.getFilePath() + renameFileDto.getOldFileName() + "/", sessionUserBean.getUserId());
        } else {
            if (renameFileDto.getIsOSS() == 1) {
                LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UserFile::getUserFileId, renameFileDto.getUserFileId());
                UserFile userFile = userFileService.getOne(lambdaQueryWrapper);

                FileBean file = fileService.getById(userFile.getFileId());
                String fileUrl = file.getFileUrl();
                String newFileUrl = fileUrl.replace(userFile.getFileName(), renameFileDto.getFileName());
//                renameFileDto.setFileUrl(newFileUrl);
                AliyunOSSRename.rename(qiwenFileConfig.getAliyun().getOss(),
                        fileUrl.substring(1),
                        newFileUrl.substring(1));
                LambdaUpdateWrapper<FileBean> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper
//                        .set(FileBean::getFileName, renameFileDto.getFileName())
//                        .set(FileBean::getUploadTime, DateUtil.getCurrentTime())
                        .set(FileBean::getFileUrl, newFileUrl)
                        .eq(FileBean::getFileId, file.getFileId());
                fileService.update(lambdaUpdateWrapper);

                LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                userFileLambdaUpdateWrapper
                        .set(UserFile::getFileName, renameFileDto.getFileName())
                        .set(UserFile::getUploadTime, DateUtil.getCurrentTime())
                        .eq(UserFile::getUserFileId, renameFileDto.getUserFileId());
                userFileService.update(userFileLambdaUpdateWrapper);
            } else {
                LambdaUpdateWrapper<UserFile> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.set(UserFile::getFileName, renameFileDto.getFileName())
                        .set(UserFile::getUploadTime, DateUtil.getCurrentTime())
                        .eq(UserFile::getUserFileId, renameFileDto.getUserFileId());
                userFileService.update(lambdaUpdateWrapper);
            }


        }

       // fileService.updateFile(fileBean);
        restResult.setSuccess(true);
        return restResult;
    }

    @RequestMapping(value = "/recyclefile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> recycleFile(@RequestBody FileBean fileBean, @RequestHeader("token") String token) {
        
        return null;
    }

    @RequestMapping(value = "/getfilelist", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<List<Map<String, Object>>> getFileList(FileListDto fileListDto, @RequestHeader("token") String token){
        RestResult<List<Map<String, Object>>> restResult = new RestResult<>();
        UserFile userFile = new UserFile();
        if(qiwenFileConfig.isShareMode()){
            userFile.setUserId(2L);
        }else {
            UserBean sessionUserBean = userService.getUserBeanByToken(token);
            if (userFile == null) {
                restResult.setSuccess(false);
                return restResult;
            }
            userFile.setUserId(sessionUserBean.getUserId());
        }

        List<Map<String, Object>> fileList = null;
        userFile.setFilePath(PathUtil.urlDecode(fileListDto.getFilePath()));
        if (fileListDto.getCurrentPage() == null || fileListDto.getPageCount() == null) {
            fileList = userFileService.userFileList(userFile, 0L, 10L);
        } else {
            Long beginCount = (fileListDto.getCurrentPage() - 1) * fileListDto.getPageCount();

            fileList = userFileService.userFileList(userFile, beginCount, fileListDto.getPageCount()); //fileService.selectFileListByPath(fileBean);

        }

        restResult.setData(fileList);
        restResult.setSuccess(true);
        return restResult;
    }

    /**
     * 批量删除文件
     *
     * @return
     */
    @RequestMapping(value = "/batchdeletefile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> deleteImageByIds(@RequestBody BatchDeleteFileDto batchDeleteFileDto, @RequestHeader("token") String token) {
        RestResult<String> result = new RestResult<String>();
        if (!operationCheck(token).isSuccess()) {
            return operationCheck(token);
        }
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        List<UserFile> userFiles = JSON.parseArray(batchDeleteFileDto.getFiles(), UserFile.class);

        for (UserFile userFile : userFiles) {
            userFileService.deleteUserFile(userFile,sessionUserBean);
        }

        result.setData("批量删除文件成功");
        result.setSuccess(true);
        return result;
    }

    /**
     * 删除文件
     *
     * @return
     */
    @RequestMapping(value = "/deletefile", method = RequestMethod.POST)
    @ResponseBody
    public String deleteFile(@RequestBody DeleteFileDto deleteFileDto, @RequestHeader("token") String token) {
        RestResult<String> result = new RestResult<String>();
        if (!operationCheck(token).isSuccess()){
            return JSON.toJSONString(operationCheck(token));
        }
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        UserFile userFile = new UserFile();
        userFile.setUserFileId(deleteFileDto.getUserFileId());
        BeanUtil.copyProperties(deleteFileDto, userFile);
        userFileService.deleteUserFile(userFile, sessionUserBean);

        result.setSuccess(true);
        String resultJson = JSON.toJSONString(result);
        return resultJson;
    }

    /**
     * 解压文件
     *
     * @return
     */
    @RequestMapping(value = "/unzipfile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> unzipFile(@RequestBody UnzipFileDto unzipFileDto, @RequestHeader("token") String token) {
        RestResult<String> result = new RestResult<String>();
        if (!operationCheck(token).isSuccess()){
            return operationCheck(token);
        }

        String zipFileUrl = PathUtil.getStaticPath() + unzipFileDto.getFileUrl();
        File file = FileOperation.newFile(zipFileUrl);
        String unzipUrl = file.getParent();
        String[] arr = unzipFileDto.getFileUrl().split("\\.");
        if (arr.length <= 1) {
            result.setErrorMessage("文件名格式错误！");
            result.setSuccess(false);
            return result;
        }
        List<String> fileEntryNameList = new ArrayList<>();
        if ("zip".equals(arr[1])) {
            fileEntryNameList = FileOperation.unzip(file, unzipUrl);
        } else if ("rar".equals(arr[1])) {
            try {
                fileEntryNameList = FileOperation.unrar(file, unzipUrl);
            } catch (Exception e) {
                e.printStackTrace();
                result.setErrorMessage("rar解压失败！");
                result.setSuccess(false);
                return result;
            }
        } else {
            result.setErrorMessage("不支持的文件格式！");
            result.setSuccess(false);
            return result;
        }

        List<FileBean> fileBeanList = new ArrayList<>();
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        log.info("解压缩文件数量：" + fileBeanList);

        for (int i = 0; i < fileEntryNameList.size(); i++){
            String entryName = fileEntryNameList.get(i);
            log.info("文件名："+ entryName);
            executor.execute(() -> {
                String totalFileUrl = unzipUrl + entryName;
                File currentFile = FileOperation.newFile(totalFileUrl);

                FileBean tempFileBean = new FileBean();
                UserFile userFile = new UserFile();

                userFile.setUploadTime(DateUtil.getCurrentTime());
                userFile.setUserId(sessionUserBean.getUserId());
                userFile.setFilePath(FileUtil.pathSplitFormat(unzipFileDto.getFilePath() + entryName.replace(currentFile.getName(), "")).replace("\\", "/"));

                if (currentFile.isDirectory()){

                    userFile.setIsDir(1);

                    userFile.setFileName(currentFile.getName());
                    tempFileBean.setTimeStampName(currentFile.getName());
                }else{

                    userFile.setIsDir(0);
                    userFile.setExtendName(FileUtil.getFileType(totalFileUrl));
                    userFile.setFileName(FileUtil.getFileNameNotExtend(currentFile.getName()));
                    tempFileBean.setFileSize(currentFile.length());
                    tempFileBean.setTimeStampName(FileUtil.getFileNameNotExtend(currentFile.getName()));
                    tempFileBean.setFileUrl(File.separator + (currentFile.getPath()).replace(PathUtil.getStaticPath(), ""));
                    tempFileBean.setPointCount(1);
                    fileService.save(tempFileBean);
                }

                userFile.setFileId(tempFileBean.getFileId());
                userFile.setDeleteFlag(0);
                userFileService.save(userFile);
            });

            //fileBeanList.add(tempFileBean);
        }

//        fileService.batchInsertFile(fileBeanList, sessionUserBean.getUserId());
        result.setSuccess(true);

        return result;
    }

    /**
     * 文件移动
     *
     *
     * @return 返回前台移动结果
     */
    @RequestMapping(value = "/movefile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> moveFile(@RequestBody MoveFileDto moveFileDto, @RequestHeader("token") String token) {
        RestResult<String> result = new RestResult<String>();
        if (!operationCheck(token).isSuccess()){
            return operationCheck(token);
        }
        String oldfilePath = moveFileDto.getOldFilePath();
        String newfilePath = moveFileDto.getFilePath();
        String fileName = moveFileDto.getFileName();
        String extendName = moveFileDto.getExtendName();

        userFileService.updateFilepathByFilepath(oldfilePath, newfilePath, fileName, extendName);
        result.setSuccess(true);
        return result;
    }

    /**
     * 批量移动文件
     *
     *
     * @return 返回前台移动结果
     */
    @RequestMapping(value = "/batchmovefile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> batchMoveFile(@RequestBody BatchMoveFileDto batchMoveFileDto, @RequestHeader("token") String token) {

        RestResult<String> result = new RestResult<String>();
        if (!operationCheck(token).isSuccess()) {
            return operationCheck(token);
        }

        String files = batchMoveFileDto.getFiles();
        String newfilePath = batchMoveFileDto.getFilePath();

        List<UserFile> fileList = JSON.parseArray(files, UserFile.class);

        for (UserFile userFile : fileList) {
            userFileService.updateFilepathByFilepath(userFile.getFilePath(), newfilePath, userFile.getFileName(), userFile.getExtendName());
        }

        result.setData("批量移动文件成功");
        result.setSuccess(true);
        return result;
    }

    public RestResult<String> operationCheck(String token){
        RestResult<String> result = new RestResult<String>();
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        if (sessionUserBean == null){
            result.setSuccess(false);
            result.setErrorMessage("未登录");
            return result;
        }
        if (qiwenFileConfig.isShareMode()){
            if (sessionUserBean.getUserId() > 2){
                result.setSuccess(false);
                result.setErrorMessage("没权限，请联系管理员！");
                return result;
            }
        }
        result.setSuccess(true);
        return result;
    }

    /**
     * 通过文件类型选择文件
     *
     * @return
     */
    @RequestMapping(value = "/selectfilebyfiletype", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<List<Map<String, Object>>> selectFileByFileType(int fileType, @RequestHeader("token") String token) {
        RestResult<List<Map<String, Object>>> result = new RestResult<List<Map<String, Object>>>();
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        long userId = sessionUserBean.getUserId();
        if (qiwenFileConfig.isShareMode()){
            userId = 2;
        }
        List<Map<String, Object>> fileList = new ArrayList<>();
        if (fileType == FileUtil.OTHER_TYPE) {

            List<String> arrList = new ArrayList<>();
            arrList.addAll(Arrays.asList(FileUtil.DOC_FILE));
            arrList.addAll(Arrays.asList(FileUtil.IMG_FILE));
            arrList.addAll(Arrays.asList(FileUtil.VIDEO_FILE));
            arrList.addAll(Arrays.asList(FileUtil.MUSIC_FILE));
            fileList = userFileService.selectFileNotInExtendNames(arrList, userId);
        } else {
            fileList = userFileService.selectFileByExtendName(getFileExtendsByType(fileType), userId);
        }
        result.setData(fileList);
        result.setSuccess(true);
        return result;
    }

    /**
     * 获取文件树
     * @return
     */
    @RequestMapping(value = "/getfiletree", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<TreeNode> getFileTree(@RequestHeader("token") String token){
        RestResult<TreeNode> result = new RestResult<TreeNode>();
        UserFile userFile = new UserFile();
        UserBean sessionUserBean = userService.getUserBeanByToken(token);
        if (qiwenFileConfig.isShareMode()){
            userFile.setUserId(2L);
        }else{
            userFile.setUserId(sessionUserBean.getUserId());
        }

        List<UserFile> filePathList = userFileService.selectFilePathTreeByUserId(sessionUserBean.getUserId());
        TreeNode resultTreeNode = new TreeNode();
        resultTreeNode.setLabel("/");

        for (int i = 0; i < filePathList.size(); i++){
            String filePath = filePathList.get(i).getFilePath() + filePathList.get(i).getFileName() + "/";

            Queue<String> queue = new LinkedList<>();

            String[] strArr = filePath.split("/");
            for (int j = 0; j < strArr.length; j++){
                if (!"".equals(strArr[j]) && strArr[j] != null){
                    queue.add(strArr[j]);
                }

            }
            if (queue.size() == 0){
                continue;
            }
            resultTreeNode = insertTreeNode(resultTreeNode,"/", queue);


        }
        result.setSuccess(true);
        result.setData(resultTreeNode);
        return result;

    }

    public TreeNode insertTreeNode(TreeNode treeNode, String filePath, Queue<String> nodeNameQueue){

        List<TreeNode> childrenTreeNodes = treeNode.getChildren();
        String currentNodeName = nodeNameQueue.peek();
        if (currentNodeName == null){
            return treeNode;
        }

        Map<String, String> map = new HashMap<>();
        filePath = filePath + currentNodeName + "/";
        map.put("filePath", filePath);

        if (!isExistPath(childrenTreeNodes, currentNodeName)){  //1、判断有没有该子节点，如果没有则插入
            //插入
            TreeNode resultTreeNode = new TreeNode();


            resultTreeNode.setAttributes(map);
            resultTreeNode.setLabel(nodeNameQueue.poll());
            resultTreeNode.setId(treeid++);

            childrenTreeNodes.add(resultTreeNode);

        }else{  //2、如果有，则跳过
            nodeNameQueue.poll();
        }

        if (nodeNameQueue.size() != 0) {
            for (int i = 0; i < childrenTreeNodes.size(); i++) {

                TreeNode childrenTreeNode = childrenTreeNodes.get(i);
                if (currentNodeName.equals(childrenTreeNode.getLabel())){
                    childrenTreeNode = insertTreeNode(childrenTreeNode, filePath, nodeNameQueue);
                    childrenTreeNodes.remove(i);
                    childrenTreeNodes.add(childrenTreeNode);
                    treeNode.setChildren(childrenTreeNodes);
                }

            }
        }else{
            treeNode.setChildren(childrenTreeNodes);
        }

        return treeNode;

    }

    public boolean isExistPath(List<TreeNode> childrenTreeNodes, String path){
        boolean isExistPath = false;

        try {
            for (int i = 0; i < childrenTreeNodes.size(); i++){
                if (path.equals(childrenTreeNodes.get(i).getLabel())){
                    isExistPath = true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return isExistPath;
    }


}
