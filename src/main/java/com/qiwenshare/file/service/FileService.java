package com.qiwenshare.file.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiwenshare.common.operation.FileOperation;
import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.file.advice.QiwenException;
import com.qiwenshare.file.api.IFileService;
import com.qiwenshare.file.component.FileDealComp;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.mapper.FileMapper;
import com.qiwenshare.file.mapper.UserFileMapper;
import com.qiwenshare.file.util.QiwenFileUtil;
import com.qiwenshare.ufop.factory.UFOPFactory;
import com.qiwenshare.ufop.operation.copy.domain.CopyFile;
import com.qiwenshare.ufop.operation.download.Downloader;
import com.qiwenshare.ufop.operation.download.domain.DownloadFile;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class FileService extends ServiceImpl<FileMapper, FileBean> implements IFileService {
    public static Executor executor = Executors.newFixedThreadPool(20);
    @Resource
    FileMapper fileMapper;
    @Resource
    UserFileMapper userFileMapper;
    @Resource
    UFOPFactory ufopFactory;
    @Value("${ufop.storage-type}")
    private Integer storageType;

    @Resource
    FileDealComp fileDealComp;

    @Override
    public void increaseFilePointCount(Long fileId) {
        FileBean fileBean = fileMapper.selectById(fileId);
        if (fileBean == null) {
            log.error("文件不存在，fileId : {}", fileId );
            return;
        }
        fileBean.setPointCount(fileBean.getPointCount()+1);
        fileMapper.updateById(fileBean);
    }

    @Override
    public void decreaseFilePointCount(Long fileId) {
        FileBean fileBean = fileMapper.selectById(fileId);
        fileBean.setPointCount(fileBean.getPointCount()-1);
        fileMapper.updateById(fileBean);
    }

    @Override
    public void unzipFile(long userFileId, int unzipMode, String filePath) {
        UserFile userFile = userFileMapper.selectById(userFileId);
        FileBean fileBean = fileMapper.selectById(userFile.getFileId());
        File destFile = new File(UFOPUtils.getStaticPath() + "temp" + File.separator + fileBean.getFileUrl());


        Downloader downloader = ufopFactory.getDownloader(fileBean.getStorageType());
        DownloadFile downloadFile = new DownloadFile();
        downloadFile.setFileUrl(fileBean.getFileUrl());
        downloadFile.setFileSize(fileBean.getFileSize());
        InputStream inputStream = downloader.getInputStream(downloadFile);

        try {
            FileUtils.copyInputStreamToFile(inputStream, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }


        String extendName = userFile.getExtendName();

        String unzipUrl = UFOPUtils.getTempFile(fileBean.getFileUrl()).getAbsolutePath().replace("." + extendName, "");

        List<String> fileEntryNameList = new ArrayList<>();
        if ("zip".equals(extendName)) {
            fileEntryNameList = FileOperation.unzip(destFile, unzipUrl);
        } else if ("rar".equals(extendName)) {
            try {
                fileEntryNameList = FileOperation.unrar(destFile, unzipUrl);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("rar解压失败" + e);
                throw new QiwenException(500001, "rar解压异常：" + e.getMessage());
            }
        } else {
            throw new QiwenException(500002, "不支持的文件格式！");
        }
        if (destFile.exists()) {
            destFile.delete();
        }

        if (!fileEntryNameList.isEmpty() && unzipMode == 1) {
            UserFile qiwenDir = QiwenFileUtil.getQiwenDir(userFile.getUserId(), userFile.getFilePath(), userFile.getFileName());
            userFileMapper.insert(qiwenDir);
        }
        for (int i = 0; i < fileEntryNameList.size(); i++){
            String entryName = fileEntryNameList.get(i);
            log.info("文件名："+ entryName);
            executor.execute(() -> {
                String totalFileUrl = unzipUrl + entryName;
                File currentFile = new File(totalFileUrl);

                FileBean tempFileBean = new FileBean();
                UserFile saveUserFile = new UserFile();

                saveUserFile.setUploadTime(DateUtil.getCurrentTime());
                saveUserFile.setUserId(userFile.getUserId());
                saveUserFile.setFilePath(UFOPUtils.pathSplitFormat(userFile.getFilePath() + entryName.replace(currentFile.getName(), "")).replace("\\", "/"));

                if (currentFile.isDirectory()){
                    saveUserFile.setIsDir(1);
                    saveUserFile.setFileName(currentFile.getName());
                }else{

                    FileInputStream fileInputStream = null;
                    FileInputStream fileInputStream1 = null;
                    try {
                        fileInputStream = new FileInputStream(currentFile);
                        String md5Str = DigestUtils.md5Hex(fileInputStream);
                        Map<String, Object> param = new HashMap<String, Object>();
                        param.put("identifier", md5Str);

                        List<FileBean> list = fileMapper.selectByMap(param);
                        if (list != null && !list.isEmpty()) { //文件已存在
                            increaseFilePointCount(list.get(0).getFileId());
                            saveUserFile.setFileId(list.get(0).getFileId());
                        } else { //文件不存在
                            fileInputStream1 = new FileInputStream(currentFile);
                            CopyFile createFile = new CopyFile();
                            createFile.setExtendName(UFOPUtils.getFileExtendName(totalFileUrl));
                            String saveFileUrl = ufopFactory.getCopier().copy(fileInputStream1, createFile);
                            tempFileBean.setFileSize(currentFile.length());
                            tempFileBean.setFileUrl(saveFileUrl);
                            tempFileBean.setPointCount(1);
                            tempFileBean.setStorageType(storageType);
                            tempFileBean.setIdentifier(md5Str);
                            fileMapper.insert(tempFileBean);

                            saveUserFile.setFileId(tempFileBean.getFileId());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (fileInputStream != null) {
                            try {
                                log.info("关闭流");
                                fileInputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (fileInputStream1 != null) {
                            try {
                                log.info("关闭流");
                                fileInputStream1.close();

                                System.gc();
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            currentFile.delete();
                        }
                    }

                    saveUserFile.setIsDir(0);
                    saveUserFile.setExtendName(UFOPUtils.getFileExtendName(totalFileUrl));
                    saveUserFile.setFileName(UFOPUtils.getFileNameNotExtend(currentFile.getName()));

                }


                saveUserFile.setDeleteFlag(0);

                if (unzipMode == 1) {
//                     String destFilePath = "/" + userFile.getFilePath() + saveUserFile.getFilePath();
//                     saveUserFile.setFilePath(destFilePath);

                    saveUserFile.setFilePath(UFOPUtils.pathSplitFormat(userFile.getFilePath() + userFile.getFileName() + "/" + entryName.replace(currentFile.getName(), "")).replace("\\", "/"));
                } else if(unzipMode == 2) {
                    saveUserFile.setFilePath(UFOPUtils.pathSplitFormat(filePath + entryName.replace(currentFile.getName(), "")).replace("\\", "/"));
                }

                String fileName = fileDealComp.getRepeatFileName(saveUserFile, saveUserFile.getFilePath());

                if (saveUserFile.getIsDir() == 1 && !fileName.equals(saveUserFile.getFileName())) {
                    //如果是目录，而且重复，什么也不做
                } else {
                    saveUserFile.setFileName(fileName);
                    userFileMapper.insert(saveUserFile);
                }

            });

        }
    }


}
