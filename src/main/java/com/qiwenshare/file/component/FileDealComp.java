package com.qiwenshare.file.component;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qiwenshare.common.constant.FileConstant;
import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.file.api.IElasticSearchService;
import com.qiwenshare.file.config.es.FileSearch;
import com.qiwenshare.file.domain.TreeNode;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.mapper.UserFileMapper;
import com.qiwenshare.file.vo.file.FileListVo;
import com.qiwenshare.ufo.factory.UFOFactory;
import com.qiwenshare.ufo.operation.read.Reader;
import com.qiwenshare.ufo.operation.read.domain.ReadFile;
import com.qiwenshare.ufo.util.PathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 文件逻辑处理组件
 */
@Slf4j
@Component
public class FileDealComp {
    @Resource
    UserFileMapper userFileMapper;
    @Autowired
    private IElasticSearchService elasticSearchService;

    @Resource
    UFOFactory ufoFactory;
    /**
     * 获取重复文件名
     *
     * 场景: 文件还原时，在 savefilePath 路径下，保存 测试.txt 文件重名，则会生成 测试(1).txt
     *
     * @param userFile
     * @param savefilePath
     * @return
     */
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

    /**
     * 还原父文件路径
     *
     * 回收站文件还原操作会将文件恢复到原来的路径下,当还原文件的时候，如果父目录已经不存在了，则需要把父母录给还原
     * @param filePath
     * @param sessionUserId
     */
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


    /**
     * 删除重复的子目录文件
     *
     * 当还原目录的时候，如果其子目录在文件系统中已存在，则还原之后进行去重操作
     * @param filePath
     * @param sessionUserId
     */
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

    /**
     * 组织一个树目录节点，文件移动的时候使用
     * @param treeNode
     * @param id
     * @param filePath
     * @param nodeNameQueue
     * @return
     */
    public TreeNode insertTreeNode(TreeNode treeNode, long id,  String filePath, Queue<String> nodeNameQueue){

        List<TreeNode> childrenTreeNodes = treeNode.getChildren();
        String currentNodeName = nodeNameQueue.peek();
        if (currentNodeName == null){
            return treeNode;
        }

        filePath = filePath + currentNodeName + "/";

        if (!isExistPath(childrenTreeNodes, currentNodeName)){  //1、判断有没有该子节点，如果没有则插入
            //插入
            TreeNode resultTreeNode = new TreeNode();

            resultTreeNode.setFilePath(filePath);
            resultTreeNode.setLabel(nodeNameQueue.poll());
            resultTreeNode.setId(++id);

            childrenTreeNodes.add(resultTreeNode);

        }else{  //2、如果有，则跳过
            nodeNameQueue.poll();
        }

        if (nodeNameQueue.size() != 0) {
            for (int i = 0; i < childrenTreeNodes.size(); i++) {

                TreeNode childrenTreeNode = childrenTreeNodes.get(i);
                if (currentNodeName.equals(childrenTreeNode.getLabel())){
                    childrenTreeNode = insertTreeNode(childrenTreeNode, id * 10, filePath, nodeNameQueue);
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

    /**
     * 判断该路径在树节点中是否已经存在
     * @param childrenTreeNodes
     * @param path
     * @return
     */
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


    public void uploadESByUserFileId(Long userFileId) {
        try {
            UserFile userFile = new UserFile();
            userFile.setUserFileId(userFileId);
            List<FileListVo> userfileResult = userFileMapper.userFileList(userFile, null, null);
            if (userfileResult != null && userfileResult.size() > 0) {
                FileSearch fileSearch = new FileSearch();
                BeanUtil.copyProperties(userfileResult.get(0), fileSearch);
                if (fileSearch.getIsDir() == 0) {

                    Reader reader = ufoFactory.getReader(fileSearch.getStorageType());
                    ReadFile readFile = new ReadFile();
                    readFile.setFileUrl(fileSearch.getFileUrl());
                    String content = reader.read(readFile);
                    //全文搜索
    //                fileSearch.setContent(content);

                }
                elasticSearchService.save(fileSearch);
            }
        } catch (Exception e) {
            log.error("ES更新操作失败，请检查配置");
        }

    }

    public void deleteESByUserFileId(Long userFileId) {
        try {
            elasticSearchService.deleteById(userFileId);
        } catch (Exception e) {
            log.error("ES删除操作失败，请检查配置");
        }

    }
}
