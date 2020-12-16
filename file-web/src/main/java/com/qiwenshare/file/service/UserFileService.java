package com.qiwenshare.file.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiwenshare.common.cbb.DateUtil;
import com.qiwenshare.common.operation.FileOperation;
import com.qiwenshare.common.oss.AliyunOSSDelete;
import com.qiwenshare.common.util.FileUtil;
import com.qiwenshare.common.util.PathUtil;
import com.qiwenshare.file.api.IUserFileService;
import com.qiwenshare.file.config.QiwenFileConfig;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.StorageBean;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.mapper.FileMapper;
import com.qiwenshare.file.mapper.UserFileMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class UserFileService  extends ServiceImpl<UserFileMapper, UserFile> implements IUserFileService {
    @Resource
    UserFileMapper userFileMapper;
    @Resource
    FileMapper fileMapper;
    @Resource
    FiletransferService filetransferService;
    @Resource
    QiwenFileConfig qiwenFileConfig;

    public static Executor executor = Executors.newFixedThreadPool(20);


    @Override
    public List<UserFile> selectUserFileByNameAndPath(String fileName, String filePath, Long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileName, fileName).eq(UserFile::getFilePath, filePath).eq(UserFile::getUserId, userId);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public void replaceUserFilePath(String filePath, String oldFilePath, Long userId) {
        userFileMapper.replaceFilePath(filePath, oldFilePath, userId);
    }

    @Override
    public List<Map<String, Object>> userFileList(UserFile userFile, Long beginCount, Long pageCount) {
        return userFileMapper.userFileList(userFile, beginCount, pageCount);
    }



    @Override
    public void updateFilepathByFilepath(String oldfilePath, String newfilePath, String fileName, String extendName) {
        if ("null".equals(extendName)){
            extendName = null;
        }
        //移动根目录
        userFileMapper.updateFilepathByPathAndName(oldfilePath, newfilePath, fileName, extendName);

        //移动子目录
        oldfilePath = oldfilePath + fileName + "/";
        newfilePath = newfilePath + fileName + "/";

        oldfilePath = oldfilePath.replace("\\", "\\\\\\\\");
        oldfilePath = oldfilePath.replace("'", "\\'");
        oldfilePath = oldfilePath.replace("%", "\\%");
        oldfilePath = oldfilePath.replace("_", "\\_");

        if (extendName == null) { //为null说明是目录，则需要移动子目录
            userFileMapper.updateFilepathByFilepath(oldfilePath, newfilePath);
        }

    }


    @Override
    public List<Map<String, Object>> selectFileByExtendName(List<String> fileNameList, long userId) {
//        LambdaQueryWrapper<FileBean> wrapper = new LambdaQueryWrapper<>();
//        wrapper.in(FileBean::getExtendName, fileNameList).eq(FileBean::getUserId, userId);
//        List<FileBean> fileBeans = fileMapper.selectList(wrapper);
//        return fileBeans;
        return userFileMapper.selectFileByExtendName(fileNameList, userId);
    }

    @Override
    public List<Map<String, Object>> selectFileNotInExtendNames(List<String> fileNameList, long userId) {
//        LambdaQueryWrapper<FileBean> wrapper = new LambdaQueryWrapper<>();
//        wrapper.notIn(FileBean::getExtendName, fileNameList).eq(FileBean::getUserId, userId);
//        List<FileBean> fileBeans = fileMapper.selectList(wrapper);
        return userFileMapper.selectFileNotInExtendNames(fileNameList, userId);
    }

    @Override
    public List<UserFile> selectFileTreeListLikeFilePath(String filePath) {
        UserFile userFile = new UserFile();
        filePath = filePath.replace("\\", "\\\\\\\\");
        filePath = filePath.replace("'", "\\'");
        filePath = filePath.replace("%", "\\%");
        filePath = filePath.replace("_", "\\_");

        userFile.setFilePath(filePath);

        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        log.info("删除文件路径：" + filePath);

        lambdaQueryWrapper.likeRight(UserFile::getFilePath, filePath);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<UserFile> selectFilePathTreeByUserId(Long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getUserId, userId)
                .eq(UserFile::getIsDir, 1);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }


    @Override
    public void deleteUserFile(UserFile userFile, UserBean sessionUserBean) {
        StorageBean storageBean = filetransferService.selectStorageBean(new StorageBean(sessionUserBean.getUserId()));

        if (userFile.getIsDir() == 1) {

            LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<UserFile>();
            userFileLambdaUpdateWrapper.set(UserFile::getDeleteFlag, 1).set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                    .eq(UserFile::getUserFileId, userFile.getUserFileId());
            userFileMapper.update(null, userFileLambdaUpdateWrapper);

            String filePath = userFile.getFilePath() + userFile.getFileName() + "/";
            updateFileDeleteStateByFilePath(filePath);

        }else{
            //userFileMapper.deleteById(userFile.getUserFileId());
            UserFile userFileTemp = userFileMapper.selectById(userFile.getUserFileId());
            FileBean fileBean = fileMapper.selectById(userFileTemp.getFileId());





            LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            userFileLambdaUpdateWrapper.set(UserFile::getDeleteFlag, 1)
                    .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                    .eq(UserFile::getUserFileId, userFileTemp.getUserFileId());
            userFileMapper.update(null, userFileLambdaUpdateWrapper);

            LambdaUpdateWrapper<FileBean> fileBeanLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            fileBeanLambdaUpdateWrapper.set(FileBean::getPointCount, fileBean.getPointCount() -1)
                    .eq(FileBean::getFileId, fileBean.getFileId());
        }

    }

    private void updateFileDeleteStateByFilePath(String filePath) {
        new Thread(()->{
            List<UserFile> fileList = selectFileTreeListLikeFilePath(filePath);
            for (int i = 0; i < fileList.size(); i++){
                UserFile userFileTemp = fileList.get(i);
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (userFileTemp.getIsDir() != 1){
                            FileBean fileBean = fileMapper.selectById(userFileTemp.getFileId());
                            if (fileBean.getPointCount() != null) {

                                LambdaUpdateWrapper<FileBean> fileBeanLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                                fileBeanLambdaUpdateWrapper.set(FileBean::getPointCount, fileBean.getPointCount() -1)
                                        .eq(FileBean::getFileId, fileBean.getFileId());
                                fileMapper.update(null, fileBeanLambdaUpdateWrapper);

                            }
                        }
                        //标记删除标志
                        LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper1 = new LambdaUpdateWrapper<>();
                        userFileLambdaUpdateWrapper1.set(UserFile::getDeleteFlag, 1)
                                .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                                .eq(UserFile::getUserFileId, userFileTemp.getUserFileId());
                        userFileMapper.update(null, userFileLambdaUpdateWrapper1);
                    }
                });

            }
        }).start();
    }



}
