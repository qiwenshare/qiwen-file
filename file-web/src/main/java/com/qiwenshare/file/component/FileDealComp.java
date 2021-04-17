package com.qiwenshare.file.component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qiwenshare.common.constant.FileConstant;
import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.common.util.PathUtil;
import com.qiwenshare.file.domain.TreeNode;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.mapper.UserFileMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Component
public class FileDealComp {
    @Resource
    UserFileMapper userFileMapper;

    public String getRepeatFileName(UserFile userFile, String savefilePath) {
        String fileName = userFile.getFileName();
        String extendName = userFile.getExtendName();
        Integer deleteFlag = userFile.getDeleteFlag();
        Long userId = userFile.getUserId();
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFilePath, savefilePath)
                .eq(UserFile::getDeleteFlag, deleteFlag)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getFileName, fileName);
        if (userFile.getIsDir() == 0) {
            lambdaQueryWrapper.eq(UserFile::getExtendName, extendName);
        }
        List<UserFile> list = userFileMapper.selectList(lambdaQueryWrapper);
        if (list == null) {
            return fileName;
        }
        if (list.isEmpty()) {
            return fileName;
        }
        int i = 0;

        while (list != null && !list.isEmpty()) {
            i++;
            LambdaQueryWrapper<UserFile> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(UserFile::getFilePath, savefilePath)
                    .eq(UserFile::getDeleteFlag, deleteFlag)
                    .eq(UserFile::getUserId, userId)
                    .eq(UserFile::getFileName, fileName + "(" + i + ")");
            if (userFile.getIsDir() == 0) {
                lambdaQueryWrapper1.eq(UserFile::getExtendName, extendName);
            }
            list = userFileMapper.selectList(lambdaQueryWrapper1);
        }

        return fileName + "(" + i + ")";

    }

    public void restoreParentFilePath(String filePath, Long sessionUserId) {
        String parentFilePath = PathUtil.getParentPath(filePath);
        while(parentFilePath.contains("/")) {
            String fileName = parentFilePath.substring(parentFilePath.lastIndexOf("/") + 1);
            parentFilePath = PathUtil.getParentPath(parentFilePath);

            LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UserFile::getFilePath, parentFilePath + FileConstant.pathSeparator)
                    .eq(UserFile::getFileName, fileName)
                    .eq(UserFile::getDeleteFlag, 0)
                    .eq(UserFile::getUserId, sessionUserId);
            List<UserFile> userFileList = userFileMapper.selectList(lambdaQueryWrapper);
            if (userFileList.size() == 0) {
                UserFile userFile = new UserFile();
                userFile.setUserId(sessionUserId);
                userFile.setFileName(fileName);
                userFile.setFilePath(parentFilePath + FileConstant.pathSeparator);
                userFile.setDeleteFlag(0);
                userFile.setIsDir(1);
                userFile.setUploadTime(DateUtil.getCurrentTime());

                userFileMapper.insert(userFile);
            }

        }
    }

    public void deleteRepeatSubDirFile(String filePath, Long sessionUserId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.select(UserFile::getFileName, UserFile::getFilePath)
                .likeRight(UserFile::getFilePath, filePath)
                .eq(UserFile::getIsDir, 1)
                .eq(UserFile::getDeleteFlag, 0)
                .eq(UserFile::getUserId, sessionUserId)
                .groupBy(UserFile::getFilePath, UserFile::getFileName)
                .having("count(fileName) >= 2");
        List<UserFile> repeatList = userFileMapper.selectList(lambdaQueryWrapper);

        for (UserFile userFile : repeatList) {
            LambdaQueryWrapper<UserFile> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.eq(UserFile::getFilePath, userFile.getFilePath())
                    .eq(UserFile::getFileName, userFile.getFileName())
                    .eq(UserFile::getDeleteFlag, "0");
            List<UserFile> userFiles = userFileMapper.selectList(lambdaQueryWrapper1);
            for (int i = 0; i < userFiles.size() - 1; i ++) {
                userFileMapper.deleteById(userFiles.get(i).getUserFileId());
            }
        }
    }
//
//    public TreeNode insertTreeNode(TreeNode treeNode, String filePath, Queue<String> nodeNameQueue){
//
//        List<TreeNode> childrenTreeNodes = treeNode.getChildren();
//        String currentNodeName = nodeNameQueue.peek();
//        if (currentNodeName == null){
//            return treeNode;
//        }
//
//        Map<String, String> map = new HashMap<>();
//        filePath = filePath + currentNodeName + "/";
//        map.put("filePath", filePath);
//
//        if (!isExistPath(childrenTreeNodes, currentNodeName)){  //1、判断有没有该子节点，如果没有则插入
//            //插入
//            TreeNode resultTreeNode = new TreeNode();
//
//
//            resultTreeNode.setAttributes(map);
//            resultTreeNode.setLabel(nodeNameQueue.poll());
//            resultTreeNode.setId(treeid++);
//
//            childrenTreeNodes.add(resultTreeNode);
//
//        }else{  //2、如果有，则跳过
//            nodeNameQueue.poll();
//        }
//
//        if (nodeNameQueue.size() != 0) {
//            for (int i = 0; i < childrenTreeNodes.size(); i++) {
//
//                TreeNode childrenTreeNode = childrenTreeNodes.get(i);
//                if (currentNodeName.equals(childrenTreeNode.getLabel())){
//                    childrenTreeNode = insertTreeNode(childrenTreeNode, filePath, nodeNameQueue);
//                    childrenTreeNodes.remove(i);
//                    childrenTreeNodes.add(childrenTreeNode);
//                    treeNode.setChildren(childrenTreeNodes);
//                }
//
//            }
//        }else{
//            treeNode.setChildren(childrenTreeNodes);
//        }
//
//        return treeNode;
//
//    }
//
//    public boolean isExistPath(List<TreeNode> childrenTreeNodes, String path){
//        boolean isExistPath = false;
//
//        try {
//            for (int i = 0; i < childrenTreeNodes.size(); i++){
//                if (path.equals(childrenTreeNodes.get(i).getLabel())){
//                    isExistPath = true;
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//
//        return isExistPath;
//    }
}
