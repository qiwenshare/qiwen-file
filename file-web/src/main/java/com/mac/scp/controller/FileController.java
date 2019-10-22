package com.mac.scp.controller;

import com.alibaba.fastjson.JSON;
import com.mac.common.cbb.DateUtil;
import com.mac.common.cbb.RestResult;
import com.mac.common.operation.FileOperation;
import com.mac.common.util.FileUtil;
import com.mac.common.util.PathUtil;
import com.mac.scp.api.IFileService;
import com.mac.scp.api.IFiletransferService;
import com.mac.scp.domain.*;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

import static com.mac.common.util.FileUtil.getFileExtendsByType;

@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    IFileService fileService;
    @Resource
    IFiletransferService filetransferService;

    /**
     * 是否开启共享文件模式
     */
    public static Boolean isShareFile = true;

    public static long treeid = 0;

    /**
     * @return
     */
    @RequestMapping("/fileindex")
    @ResponseBody
    public ModelAndView essayIndex() {
        ModelAndView mv = new ModelAndView("/file/fileIndex.html");
        return mv;
    }

    /**
     * 创建文件
     *
     *
     * @return
     */
    @RequestMapping("/createfile")
    @ResponseBody
    public RestResult<String> createFile(FileBean fileBean) {
        RestResult<String> restResult = new RestResult<>();
        if (!operationCheck().isSuccess()){
            return operationCheck();
        }

        UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        fileBean.setUserid(sessionUserBean.getUserId());

        fileBean.setUploadtime(DateUtil.getCurrentTime());

        fileService.insertFile(fileBean);
        restResult.setSuccess(true);
        return restResult;
    }

    @RequestMapping("/getfilelist")
    @ResponseBody
    public RestResult<List<FileBean>> getFileList(FileBean fileBean){
        RestResult<List<FileBean>> restResult = new RestResult<>();
        UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        if(isShareFile){
            fileBean.setUserid(2);
        }else {
            fileBean.setUserid(sessionUserBean.getUserId());
        }

        fileBean.setFilepath(PathUtil.urlDecode(fileBean.getFilepath()));
        List<FileBean> fileList = fileService.selectFileList(fileBean);

        if ("/".equals(fileBean.getFilepath())){
            FileBean albumFile = new FileBean();
            albumFile.setFilename("我的相册");

            albumFile.setFilepath("/");
            albumFile.setIsdir(1);
            fileList.add(albumFile);
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
    @RequestMapping("/batchdeletefile")
    @ResponseBody
    public String deleteImageByIds(String files) {
        RestResult<String> result = new RestResult<String>();
        if (!operationCheck().isSuccess()){
            return JSON.toJSONString(operationCheck());
        }

        List<FileBean> fileList = JSON.parseArray(files, FileBean.class);

        for (FileBean fileBean : fileList) {
            fileService.deleteFile(fileBean);
        }

        result.setData("批量删除文件成功");
        result.setSuccess(true);
        String resultJson = JSON.toJSONString(result);
        return resultJson;
    }

    /**
     * 删除文件
     *
     * @return
     */
    @RequestMapping("/deletefile")
    @ResponseBody
    public String deleteFile(FileBean fileBean) {
        RestResult<String> result = new RestResult<String>();
        if (!operationCheck().isSuccess()){
            return JSON.toJSONString(operationCheck());
        }

        fileService.deleteFile(fileBean);

        result.setSuccess(true);
        String resultJson = JSON.toJSONString(result);
        return resultJson;
    }

    /**
     * 解压文件
     *
     * @return
     */
    @RequestMapping("/unzipfile")
    @ResponseBody
    public String unzipFile(FileBean fileBean){
        RestResult<String> result = new RestResult<String>();
        if (!operationCheck().isSuccess()){
            return JSON.toJSONString(operationCheck());
        }

        String zipFileUrl = PathUtil.getStaticPath() + fileBean.getFileurl();
        File file = FileOperation.newFile(zipFileUrl);
        String unzipUrl = file.getParent();

        List<String> fileEntryNameList = FileOperation.unzip(file, unzipUrl);

        List<FileBean> fileBeanList = new ArrayList<>();
        UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        for (int i = 0; i < fileEntryNameList.size(); i++){
            String entryName = fileEntryNameList.get(i);
            String totalFileUrl = unzipUrl + entryName;
            File currentFile = FileOperation.newFile(totalFileUrl);

            FileBean tempFileBean = new FileBean();
            tempFileBean.setUploadtime(DateUtil.getCurrentTime());
            tempFileBean.setUserid(sessionUserBean.getUserId());
            tempFileBean.setFilepath(FileUtil.pathSplitFormat(fileBean.getFilepath() + entryName.replace(currentFile.getName(), "")));
            if (currentFile.isDirectory()){

                tempFileBean.setIsdir(1);

                tempFileBean.setFilename(currentFile.getName());
                tempFileBean.setTimestampname(currentFile.getName());
                //tempFileBean.setFileurl(File.separator + (file.getParent() + File.separator + currentFile.getName()).replace(PathUtil.getStaticPath(), ""));
            }else{

                tempFileBean.setIsdir(0);

                tempFileBean.setExtendname(FileUtil.getFileType(totalFileUrl));
                tempFileBean.setFilename(FileUtil.getFileNameNotExtend(currentFile.getName()));
                tempFileBean.setFilesize(currentFile.length());
                tempFileBean.setTimestampname(FileUtil.getFileNameNotExtend(currentFile.getName()));
                tempFileBean.setFileurl(File.separator + (currentFile.getPath()).replace(PathUtil.getStaticPath(), ""));
            }
            fileBeanList.add(tempFileBean);
        }
        fileService.batchInsertFile(fileBeanList);
        result.setSuccess(true);
        String resultJson = JSON.toJSONString(result);
        return resultJson;
    }

    /**
     * 文件移动
     * @param oldfilepath 源路径
     * @param newfilepath 目的路径
     * @param filename 文件名
     * @return 返回前台移动结果
     */
    @RequestMapping("/movefile")
    @ResponseBody
    public RestResult<String> moveFile(String oldfilepath, String newfilepath, String filename, String extendname){
        RestResult<String> result = new RestResult<String>();
        if (!operationCheck().isSuccess()){
            return operationCheck();
        }

        fileService.updateFilepathByFilepath(oldfilepath, newfilepath, filename, extendname);
        result.setSuccess(true);
        return result;
    }

    /**
     * 批量移动文件
     * @param newfilepath 目的路径
     * @param files 需要移动的文件列表
     * @return 返回前台移动结果
     */
    @RequestMapping("/batchmovefile")
    @ResponseBody
    public RestResult<String> batchMoveFile(String newfilepath, String files){

        RestResult<String> result = new RestResult<String>();
        if (!operationCheck().isSuccess()){
            return operationCheck();
        }

        List<FileBean> fileList = JSON.parseArray(files, FileBean.class);

        for (FileBean fileBean : fileList) {
            fileService.updateFilepathByFilepath(fileBean.getFilepath(), newfilepath, fileBean.getFilename(), fileBean.getExtendname());
        }

        result.setData("批量移动文件成功");
        result.setSuccess(true);
        return result;
    }

    public RestResult<String> operationCheck(){
        RestResult<String> result = new RestResult<String>();
        UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        if (sessionUserBean == null){
            result.setSuccess(false);
            result.setErrorMessage("未登录");
            return result;
        }
        if (isShareFile){
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
     * @param fileType 文件类型
     * @return
     */
    @RequestMapping("/selectfilebyfiletype")
    @ResponseBody
    public RestResult<List<FileBean>> selectFileByFileType(int fileType){
        RestResult<List<FileBean>> result = new RestResult<List<FileBean>>();
        UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        long userid = sessionUserBean.getUserId();
        if (isShareFile){
            userid = 2;
        }
        List<FileBean> file = fileService.selectFileByExtendName(getFileExtendsByType(fileType), userid);
        result.setData(file);
        result.setSuccess(true);
        return result;
    }

    /**
     * 获取文件树
     * @return
     */
    @RequestMapping("/getfiletree")
    @ResponseBody
    public RestResult<TreeNode> getFileTree(){
        RestResult<TreeNode> result = new RestResult<TreeNode>();
        FileBean fileBean = new FileBean();
        UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
        if (isShareFile){
            fileBean.setUserid(2);
        }else{
            fileBean.setUserid(sessionUserBean.getUserId());
        }

        List<FileBean> filePathList = fileService.selectFilePathTreeByUserid(fileBean);
        TreeNode resultTreeNode = new TreeNode();
        resultTreeNode.setNodeName("/");

        for (int i = 0; i < filePathList.size(); i++){
            String filePath = filePathList.get(i).getFilepath() + filePathList.get(i).getFilename() + "/";

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

    public TreeNode insertTreeNode(TreeNode treeNode, String filepath, Queue<String> nodeNameQueue){

        List<TreeNode> childrenTreeNodes = treeNode.getChildren();
        String currentNodeName = nodeNameQueue.peek();
        if (currentNodeName == null){
            return treeNode;
        }

        Map<String, String> map = new HashMap<>();
        filepath = filepath + currentNodeName + "/";
        map.put("filepath", filepath);

        if (!isExistPath(childrenTreeNodes, currentNodeName)){  //1、判断有没有该子节点，如果没有则插入
            //插入
            TreeNode resultTreeNode = new TreeNode();


            resultTreeNode.setAttributes(map);
            resultTreeNode.setNodeName(nodeNameQueue.poll());
            resultTreeNode.setId(treeid++);

            childrenTreeNodes.add(resultTreeNode);

        }else{  //2、如果有，则跳过
            nodeNameQueue.poll();
        }

        if (nodeNameQueue.size() != 0) {
            for (int i = 0; i < childrenTreeNodes.size(); i++) {

                TreeNode childrenTreeNode = childrenTreeNodes.get(i);
                if (currentNodeName.equals(childrenTreeNode.getLabel())){
                    childrenTreeNode = insertTreeNode(childrenTreeNode, filepath, nodeNameQueue);
                    childrenTreeNodes.remove(i);
                    childrenTreeNodes.add(childrenTreeNode);
                    treeNode.setChildNode(childrenTreeNodes);
                }

            }
        }else{
            treeNode.setChildNode(childrenTreeNodes);
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
