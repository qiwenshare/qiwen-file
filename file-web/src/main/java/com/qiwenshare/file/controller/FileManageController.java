package com.qiwenshare.file.controller;

import static com.qiwenshare.file.util.FileUtils.getFileExtendsByType;
import java.io.File;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSON;
import com.qiwenshare.file.util.DateUtils;
import com.qiwenshare.file.domain.RestResult;
import com.qiwenshare.file.util.FileUtils;
import com.qiwenshare.file.util.PathUtils;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.TreeNode;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.service.FileService;

@RestController
@RequestMapping("/file")
public class FileManageController extends BaseController{

    @Autowired
    FileService fileService;

    public static long treeid = 0;

    /**
     * @author dehui dou
     * @description 创建文件
     * @param fileBean
     * @param token
     * @return com.qiwenshare.common.cbb.RestResult<java.lang.String>
     */
    @RequestMapping(value = "/createfile", method = RequestMethod.POST)
    public RestResult<String> createFile(@RequestBody FileBean fileBean, @RequestHeader("token") String token) {
        RestResult<String> restResult = new RestResult<>();
        if (!operationCheck(token).isSuccess()) {
            return operationCheck(token);
        }
        List<FileBean> fileBeans = fileService.selectFileByNameAndPath(fileBean);
        if (fileBeans != null && !fileBeans.isEmpty()) {
            restResult.setErrorMessage("同名文件已存在");
            restResult.setSuccess(false);
            return restResult;
        }
        UserBean sessionUserBean = getLoginUserInfo();
        fileBean.setUserId(sessionUserBean.getUserId());
        fileBean.setUploadTime(DateUtils.getCurrentTime());
        fileService.insertFile(fileBean);
        restResult.setSuccess(true);
        return restResult;
    }

    /**
     * @author dehui dou
     * @description 文件重命名
     * @param fileBean
     * @param token
     * @return com.qiwenshare.common.cbb.RestResult<java.lang.String>
     */
    @RequestMapping(value = "/renamefile", method = RequestMethod.POST)
    public RestResult<String> renameFile(@RequestBody FileBean fileBean, @RequestHeader("token") String token) {
        RestResult<String> restResult = new RestResult<>();
        if (!operationCheck(token).isSuccess()) {
            return operationCheck(token);
        }
        UserBean sessionUserBean = getLoginUserInfo();
        fileBean.setUserId(sessionUserBean.getUserId());
        fileBean.setUploadTime(DateUtils.getCurrentTime());
        List<FileBean> fileBeans = fileService.selectFileByNameAndPath(fileBean);
        if (fileBeans != null && !fileBeans.isEmpty()) {
            restResult.setErrorMessage("同名文件已存在");
            restResult.setSuccess(false);
            return restResult;
        }
        if (1 == fileBean.getIsDir()) {
            fileBean.setOldFilePath(fileBean.getFilePath() + fileBean.getOldFileName() + "/");
            fileBean.setFilePath(fileBean.getFilePath() + fileBean.getFileName() + "/");
        }
        fileService.updateFile(fileBean);
        restResult.setSuccess(true);
        return restResult;
    }

    /**
     * @author dehui dou
     * @description 获取文件列表
     * @param fileBean
     * @param token
     * @return com.qiwenshare.common.cbb.RestResult<java.util.List<com.qiwenshare.file.domain.FileBean>>
     */
    @RequestMapping(value = "/getfilelist", method = RequestMethod.GET)
    public RestResult<List<FileBean>> getFileList(FileBean fileBean, @RequestHeader("token") String token) {
        RestResult<List<FileBean>> restResult = new RestResult<>();
        UserBean sessionUserBean = getLoginUserInfo();
        if (fileBean == null) {
            restResult.setSuccess(false);
            return restResult;
        }
        fileBean.setUserId(sessionUserBean.getUserId());
        fileBean.setFilePath(PathUtils.urlDecode(fileBean.getFilePath()));
        List<FileBean> fileList = fileService.selectFileList(fileBean);
        restResult.setData(fileList);
        restResult.setSuccess(true);
        return restResult;
    }

    /**
     * @author dehui dou
     * @description 批量删除
     * @param fileBean
     * @param token
     * @return com.qiwenshare.common.cbb.RestResult<java.lang.String>
     */
    @RequestMapping(value = "/batchdeletefile", method = RequestMethod.POST)
    public RestResult<String> deleteImageByIds(@RequestBody FileBean fileBean, @RequestHeader("token") String token) {
        RestResult<String> result = new RestResult<String>();
        if (!operationCheck(token).isSuccess()) {
            return operationCheck(token);
        }
        UserBean sessionUserBean = getLoginUserInfo();
        List<FileBean> fileList = JSON.parseArray(fileBean.getFiles(), FileBean.class);
        for (FileBean file : fileList) {
            fileService.deleteFile(file, sessionUserBean);
        }
        result.setData("批量删除文件成功");
        result.setSuccess(true);
        return result;
    }

    /**
     * @author dehui dou
     * @description 删除文件
     * @param fileBean
     * @param token
     * @return java.lang.String
     */
    @RequestMapping(value = "/deletefile", method = RequestMethod.POST)
    public String deleteFile(@RequestBody FileBean fileBean, @RequestHeader("token") String token) {
        RestResult<String> result = new RestResult<String>();
        if (!operationCheck(token).isSuccess()) {
            return JSON.toJSONString(operationCheck(token));
        }
        UserBean sessionUserBean = getLoginUserInfo();
        fileService.deleteFile(fileBean, sessionUserBean);
        result.setSuccess(true);
        String resultJson = JSON.toJSONString(result);
        return resultJson;
    }

    /**
     * @author dehui dou
     * @description 解压文件
     * @param fileBean
     * @param token
     * @return com.qiwenshare.common.cbb.RestResult<java.lang.String>
     */
    @RequestMapping(value = "/unzipfile", method = RequestMethod.POST)
    public RestResult<String> unzipFile(@RequestBody FileBean fileBean, @RequestHeader("token") String token) {
        RestResult<String> result = new RestResult<String>();
        if (!operationCheck(token).isSuccess()) {
            return operationCheck(token);
        }

        String zipFileUrl = PathUtils.getStaticPath() + fileBean.getFileUrl();
        File file = FileUtils.newFile(zipFileUrl);
        String unzipUrl = file.getParent();
        String[] arr = fileBean.getFileUrl().split("\\.");
        if (arr.length <= 1) {
            result.setErrorMessage("文件名格式错误！");
            result.setSuccess(false);
            return result;
        }
        List<String> fileEntryNameList = new ArrayList<>();
        if ("zip".equals(arr[1])) {
            fileEntryNameList = FileUtils.unzip(file, unzipUrl);
        } else if ("rar".equals(arr[1])) {
            try {
                fileEntryNameList = FileUtils.unrar(file, unzipUrl);
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
        UserBean sessionUserBean = getLoginUserInfo();
        for (int i = 0; i < fileEntryNameList.size(); i++) {
            String entryName = fileEntryNameList.get(i);
            String totalFileUrl = unzipUrl + entryName;
            File currentFile = FileUtils.newFile(totalFileUrl);

            FileBean tempFileBean = new FileBean();
            tempFileBean.setUploadTime(DateUtils.getCurrentTime());
            tempFileBean.setUserId(sessionUserBean.getUserId());
            tempFileBean.setFilePath(
                FileUtils.pathSplitFormat(fileBean.getFilePath() + entryName.replace(currentFile.getName(), ""))
                    .replace("\\", "/"));
            if (currentFile.isDirectory()) {
                tempFileBean.setIsDir(1);
                tempFileBean.setFileName(currentFile.getName());
                tempFileBean.setTimeStampName(currentFile.getName());
            } else {
                tempFileBean.setIsDir(0);
                tempFileBean.setExtendName(FileUtils.getFileType(totalFileUrl));
                tempFileBean.setFileName(FileUtils.getFileNameNotExtend(currentFile.getName()));
                tempFileBean.setFileSize(currentFile.length());
                tempFileBean.setTimeStampName(FileUtils.getFileNameNotExtend(currentFile.getName()));
                tempFileBean
                    .setFileUrl(File.separator + (currentFile.getPath()).replace(PathUtils.getStaticPath(), ""));
            }
            fileBeanList.add(tempFileBean);
        }
        fileService.batchInsertFile(fileBeanList);
        result.setSuccess(true);

        return result;
    }

    /**
     * @author dehui dou
     * @description 文件移动
     * @param fileBean
     * @param token
     * @return com.qiwenshare.common.cbb.RestResult<java.lang.String>
     */
    @RequestMapping(value = "/movefile", method = RequestMethod.POST)
    public RestResult<String> moveFile(@RequestBody FileBean fileBean, @RequestHeader("token") String token) {
        RestResult<String> result = new RestResult<String>();
        if (!operationCheck(token).isSuccess()) {
            return operationCheck(token);
        }
        String oldfilePath = fileBean.getOldFilePath();
        String newfilePath = fileBean.getFilePath();
        String fileName = fileBean.getFileName();
        String extendName = fileBean.getExtendName();

        fileService.updateFilepathByFilepath(oldfilePath, newfilePath, fileName, extendName);
        result.setSuccess(true);
        return result;
    }

    /**
     * @author dehui dou
     * @description 批量移动文件
     * @param fileBean
     * @param token
     * @return com.qiwenshare.common.cbb.RestResult<java.lang.String>
     */
    @RequestMapping(value = "/batchmovefile", method = RequestMethod.POST)
    public RestResult<String> batchMoveFile(@RequestBody FileBean fileBean, @RequestHeader("token") String token) {

        RestResult<String> result = new RestResult<String>();
        if (!operationCheck(token).isSuccess()) {
            return operationCheck(token);
        }

        String files = fileBean.getFiles();
        String newfilePath = fileBean.getFilePath();

        List<FileBean> fileList = JSON.parseArray(files, FileBean.class);

        for (FileBean file : fileList) {
            fileService.updateFilepathByFilepath(file.getFilePath(), newfilePath, file.getFileName(),
                file.getExtendName());
        }
        result.setData("批量移动文件成功");
        result.setSuccess(true);
        return result;
    }

    public RestResult<String> operationCheck(String token) {
        RestResult<String> result = new RestResult<String>();
        UserBean sessionUserBean = getLoginUserInfo();
        if (sessionUserBean == null) {
            result.setSuccess(false);
            result.setErrorMessage("未登录");
            return result;
        }
        result.setSuccess(true);
        return result;
    }

    /**
     * @author dehui dou
     * @description 通过文件类型选择文件
     * @param fileBean
     * @param token
     * @return com.qiwenshare.common.cbb.RestResult<java.util.List<com.qiwenshare.file.domain.FileBean>>
     */
    @RequestMapping(value = "/selectfilebyfiletype", method = RequestMethod.GET)
    public RestResult<List<FileBean>> selectFileByFileType(FileBean fileBean, @RequestHeader("token") String token) {
        RestResult<List<FileBean>> result = new RestResult<List<FileBean>>();
        UserBean sessionUserBean = getLoginUserInfo();
        long userId = sessionUserBean.getUserId();
        List<FileBean> file = fileService.selectFileByExtendName(getFileExtendsByType(fileBean.getFileType()), userId);
        result.setData(file);
        result.setSuccess(true);
        return result;
    }

    /**
     * 获取文件树
     * 
     * @return
     */
    @RequestMapping(value = "/getfiletree", method = RequestMethod.GET)
    public RestResult<TreeNode> getFileTree(@RequestHeader("token") String token) {
        RestResult<TreeNode> result = new RestResult<TreeNode>();
        FileBean fileBean = new FileBean();
        UserBean sessionUserBean = getLoginUserInfo();
        fileBean.setUserId(sessionUserBean.getUserId());
        List<FileBean> filePathList = fileService.selectFilePathTreeByUserId(fileBean);
        TreeNode resultTreeNode = new TreeNode();
        resultTreeNode.setLabel("/");
        for (int i = 0; i < filePathList.size(); i++) {
            String filePath = filePathList.get(i).getFilePath() + filePathList.get(i).getFileName() + "/";
            Queue<String> queue = new LinkedList<>();
            String[] strArr = filePath.split("/");
            for (int j = 0; j < strArr.length; j++) {
                if (!"".equals(strArr[j]) && strArr[j] != null) {
                    queue.add(strArr[j]);
                }
            }
            if (queue.size() == 0) {
                continue;
            }
            resultTreeNode = insertTreeNode(resultTreeNode, "/", queue);
        }
        result.setSuccess(true);
        result.setData(resultTreeNode);
        return result;

    }

    public TreeNode insertTreeNode(TreeNode treeNode, String filePath, Queue<String> nodeNameQueue) {
        List<TreeNode> childrenTreeNodes = treeNode.getChildren();
        String currentNodeName = nodeNameQueue.peek();
        if (currentNodeName == null) {
            return treeNode;
        }
        Map<String, String> map = new HashMap<>();
        filePath = filePath + currentNodeName + "/";
        map.put("filePath", filePath);
        if (!isExistPath(childrenTreeNodes, currentNodeName)) { // 1、判断有没有该子节点，如果没有则插入
            // 插入
            TreeNode resultTreeNode = new TreeNode();

            resultTreeNode.setAttributes(map);
            resultTreeNode.setLabel(nodeNameQueue.poll());
            resultTreeNode.setId(treeid++);

            childrenTreeNodes.add(resultTreeNode);

        } else { // 2、如果有，则跳过
            nodeNameQueue.poll();
        }

        if (nodeNameQueue.size() != 0) {
            for (int i = 0; i < childrenTreeNodes.size(); i++) {

                TreeNode childrenTreeNode = childrenTreeNodes.get(i);
                if (currentNodeName.equals(childrenTreeNode.getLabel())) {
                    childrenTreeNode = insertTreeNode(childrenTreeNode, filePath, nodeNameQueue);
                    childrenTreeNodes.remove(i);
                    childrenTreeNodes.add(childrenTreeNode);
                    treeNode.setChildren(childrenTreeNodes);
                }
            }
        } else {
            treeNode.setChildren(childrenTreeNodes);
        }
        return treeNode;
    }

    public boolean isExistPath(List<TreeNode> childrenTreeNodes, String path) {
        boolean isExistPath = false;
        try {
            for (int i = 0; i < childrenTreeNodes.size(); i++) {
                if (path.equals(childrenTreeNodes.get(i).getLabel())) {
                    isExistPath = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isExistPath;
    }

}
