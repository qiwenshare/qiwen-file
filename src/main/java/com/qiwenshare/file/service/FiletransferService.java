package com.qiwenshare.file.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.file.api.IFiletransferService;
import com.qiwenshare.file.component.FileDealComp;
import com.qiwenshare.file.domain.*;
import com.qiwenshare.file.dto.file.DownloadFileDTO;
import com.qiwenshare.file.dto.file.UploadFileDTO;
import com.qiwenshare.file.dto.file.PreviewDTO;
import com.qiwenshare.file.mapper.*;
import com.qiwenshare.file.vo.file.FileListVo;
import com.qiwenshare.ufop.constant.StorageTypeEnum;
import com.qiwenshare.ufop.constant.UploadFileStatusEnum;
import com.qiwenshare.ufop.exception.DownloadException;
import com.qiwenshare.ufop.exception.UploadException;
import com.qiwenshare.ufop.factory.UFOPFactory;
import com.qiwenshare.ufop.operation.delete.Deleter;
import com.qiwenshare.ufop.operation.delete.domain.DeleteFile;
import com.qiwenshare.ufop.operation.download.Downloader;
import com.qiwenshare.ufop.operation.download.domain.DownloadFile;
import com.qiwenshare.ufop.operation.preview.Previewer;
import com.qiwenshare.ufop.operation.preview.domain.PreviewFile;
import com.qiwenshare.ufop.operation.upload.Uploader;
import com.qiwenshare.ufop.operation.upload.domain.UploadFile;
import com.qiwenshare.ufop.operation.upload.domain.UploadFileResult;
import com.qiwenshare.ufop.util.UFOPUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class FiletransferService implements IFiletransferService {

    @Resource
    StorageMapper storageMapper;
    @Resource
    FileMapper fileMapper;

    @Resource
    UserFileMapper userFileMapper;

    @Resource
    UFOPFactory ufopFactory;
    @Resource
    FileDealComp fileDealComp;
    @Resource
    UploadTaskDetailMapper UploadTaskDetailMapper;
    @Resource
    UploadTaskMapper uploadTaskMapper;

    @Override
    public void uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto, Long userId) {

        UploadFile uploadFile = new UploadFile();
        uploadFile.setChunkNumber(uploadFileDto.getChunkNumber());
        uploadFile.setChunkSize(uploadFileDto.getChunkSize());
        uploadFile.setTotalChunks(uploadFileDto.getTotalChunks());
        uploadFile.setIdentifier(uploadFileDto.getIdentifier());
        uploadFile.setTotalSize(uploadFileDto.getTotalSize());
        uploadFile.setCurrentChunkSize(uploadFileDto.getCurrentChunkSize());

        Uploader uploader = ufopFactory.getUploader();
        if (uploader == null) {
            log.error("上传失败，请检查storageType是否配置正确");
            throw new UploadException("上传失败");
        }

        List<UploadFileResult> uploadFileResultList = uploader.upload(request, uploadFile);
        for (int i = 0; i < uploadFileResultList.size(); i++){
            UploadFileResult uploadFileResult = uploadFileResultList.get(i);
            FileBean fileBean = new FileBean();
            BeanUtil.copyProperties(uploadFileDto, fileBean);
            String relativePath = uploadFileDto.getRelativePath();

            if (UploadFileStatusEnum.SUCCESS.equals(uploadFileResult.getStatus())){
                fileBean.setFileUrl(uploadFileResult.getFileUrl());
                fileBean.setFileSize(uploadFileResult.getFileSize());
                fileBean.setStorageType(uploadFileResult.getStorageType().getCode());
                fileBean.setPointCount(1);
                fileMapper.insert(fileBean);
                UserFile userFile = new UserFile();

                if (relativePath.contains("/")) {
                    userFile.setFilePath(uploadFileDto.getFilePath() + UFOPUtils.getParentPath(relativePath) + "/");
                    fileDealComp.restoreParentFilePath(uploadFileDto.getFilePath() + UFOPUtils.getParentPath(relativePath) + "/", userId);
                    fileDealComp.deleteRepeatSubDirFile(uploadFileDto.getFilePath(), userId);

                } else {
                    userFile.setFilePath(uploadFileDto.getFilePath());
                }
                userFile.setUserId(userId);
                userFile.setFileName(uploadFileResult.getFileName());
                userFile.setExtendName(uploadFileResult.getExtendName());
                userFile.setDeleteFlag(0);
                userFile.setIsDir(0);
                List<FileListVo> userFileList = userFileMapper.userFileList(userFile, null, null);
                if (userFileList.size() > 0) {
                    String fileName = fileDealComp.getRepeatFileName(userFile, uploadFileDto.getFilePath());
                    userFile.setFileName(fileName);
                }
                userFile.setFileId(fileBean.getFileId());

                userFile.setUploadTime(DateUtil.getCurrentTime());
                userFileMapper.insert(userFile);
                fileDealComp.uploadESByUserFileId(userFile.getUserFileId());


                LambdaQueryWrapper<UploadTaskDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UploadTaskDetail::getIdentifier, uploadFileDto.getIdentifier());
                UploadTaskDetailMapper.delete(lambdaQueryWrapper);

                LambdaUpdateWrapper<UploadTask> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.set(UploadTask::getUploadStatus, UploadFileStatusEnum.SUCCESS.getCode())
                        .eq(UploadTask::getIdentifier, uploadFileDto.getIdentifier());
                uploadTaskMapper.update(null, lambdaUpdateWrapper);

            } else if (UploadFileStatusEnum.UNCOMPLATE.equals(uploadFileResult.getStatus())) {
                UploadTaskDetail UploadTaskDetail = new UploadTaskDetail();
                UploadTaskDetail.setFilePath(uploadFileDto.getFilePath());
                if (relativePath.contains("/")) {
                    UploadTaskDetail.setFilePath(uploadFileDto.getFilePath() + UFOPUtils.getParentPath(relativePath) + "/");
                } else {
                    UploadTaskDetail.setFilePath(uploadFileDto.getFilePath());
                }
                UploadTaskDetail.setFilename(uploadFileDto.getFilename());
                UploadTaskDetail.setChunkNumber(uploadFileDto.getChunkNumber());
                UploadTaskDetail.setChunkSize((int)uploadFileDto.getChunkSize());
                UploadTaskDetail.setRelativePath(uploadFileDto.getRelativePath());
                UploadTaskDetail.setTotalChunks(uploadFileDto.getTotalChunks());
                UploadTaskDetail.setTotalSize((int)uploadFileDto.getTotalSize());
                UploadTaskDetail.setIdentifier(uploadFileDto.getIdentifier());
                UploadTaskDetailMapper.insert(UploadTaskDetail);

            } else if (UploadFileStatusEnum.FAIL.equals(uploadFileResult.getStatus())) {
                LambdaQueryWrapper<UploadTaskDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UploadTaskDetail::getIdentifier, uploadFileDto.getIdentifier());
                UploadTaskDetailMapper.delete(lambdaQueryWrapper);

                LambdaUpdateWrapper<UploadTask> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.set(UploadTask::getUploadStatus, UploadFileStatusEnum.FAIL.getCode())
                        .eq(UploadTask::getIdentifier, uploadFileDto.getIdentifier());
                uploadTaskMapper.update(null, lambdaUpdateWrapper);
            }
        }

    }

    @Override
    public void downloadFile(HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO) {
        UserFile userFile = userFileMapper.selectById(downloadFileDTO.getUserFileId());

        if (userFile.getIsDir() == 0) {

            FileBean fileBean = fileMapper.selectById(userFile.getFileId());
            Downloader downloader = ufopFactory.getDownloader(fileBean.getStorageType());
            if (downloader == null) {
                log.error("下载失败，文件存储类型不支持下载，storageType:{}", fileBean.getStorageType());
                throw new DownloadException("下载失败");
            }
            DownloadFile downloadFile = new DownloadFile();

            downloadFile.setFileUrl(fileBean.getFileUrl());
            downloadFile.setFileSize(fileBean.getFileSize());
            httpServletResponse.setContentLengthLong(fileBean.getFileSize());
            downloader.download(httpServletResponse, downloadFile);
        } else {
            LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.likeRight(UserFile::getFilePath, userFile.getFilePath() + userFile.getFileName() + "/")
                    .eq(UserFile::getUserId, userFile.getUserId())
                    .eq(UserFile::getIsDir, 0)
                    .eq(UserFile::getDeleteFlag, 0);
            List<UserFile> userFileList = userFileMapper.selectList(lambdaQueryWrapper);

            String staticPath = UFOPUtils.getStaticPath();
            String tempPath = staticPath + "temp" + File.separator;
            File tempDirFile = new File(tempPath);
            if (!tempDirFile.exists()) {
                tempDirFile.mkdirs();
            }

            FileOutputStream f = null;
            try {
                f = new FileOutputStream(tempPath + userFile.getFileName() + ".zip");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            CheckedOutputStream csum = new CheckedOutputStream(f, new Adler32());
            ZipOutputStream zos = new ZipOutputStream(csum);
            BufferedOutputStream out = new BufferedOutputStream(zos);

            try {
                for (UserFile userFile1 : userFileList) {
                    FileBean fileBean = fileMapper.selectById(userFile1.getFileId());
                    Downloader downloader = ufopFactory.getDownloader(fileBean.getStorageType());
                    if (downloader == null) {
                        log.error("下载失败，文件存储类型不支持下载，storageType:{}", fileBean.getStorageType());
                        throw new UploadException("下载失败");
                    }
                    DownloadFile downloadFile = new DownloadFile();
                    downloadFile.setFileUrl(fileBean.getFileUrl());
                    downloadFile.setFileSize(fileBean.getFileSize());
                    InputStream inputStream = downloader.getInputStream(downloadFile);
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    try {
                        zos.putNextEntry(new ZipEntry(userFile1.getFilePath().replace(userFile.getFilePath(), "/") + userFile1.getFileName() + "." + userFile1.getExtendName()));

                        byte[] buffer = new byte[1024];
                        int i = bis.read(buffer);
                        while (i != -1) {
                            out.write(buffer, 0, i);
                            i = bis.read(buffer);
                        }
                    } catch (IOException e) {
                        log.error("" + e);
                        e.printStackTrace();
                    } finally {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                log.error("压缩过程中出现异常:"+ e);
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Downloader downloader = ufopFactory.getDownloader(StorageTypeEnum.LOCAL.getCode());
            DownloadFile downloadFile = new DownloadFile();
            downloadFile.setFileUrl("temp" + File.separator+userFile.getFileName() + ".zip");
            File tempFile = new File(UFOPUtils.getStaticPath() + downloadFile.getFileUrl());
            httpServletResponse.setContentLengthLong(tempFile.length());
            downloader.download(httpServletResponse, downloadFile);
            String zipPath = UFOPUtils.getStaticPath() + "temp" + File.separator+userFile.getFileName() + ".zip";
            File file = new File(zipPath);
            if (file.exists()) {
                file.delete();
            }

        }
    }

    @Override
    public void previewFile(HttpServletResponse httpServletResponse, PreviewDTO previewDTO) {
        UserFile userFile = userFileMapper.selectById(previewDTO.getUserFileId());
        FileBean fileBean = fileMapper.selectById(userFile.getFileId());
        Previewer previewer = ufopFactory.getPreviewer(fileBean.getStorageType());
        if (previewer == null) {
            log.error("预览失败，文件存储类型不支持预览，storageType:{}", fileBean.getStorageType());
            throw new UploadException("预览失败");
        }
        PreviewFile previewFile = new PreviewFile();
        previewFile.setFileUrl(fileBean.getFileUrl());
        previewFile.setFileSize(fileBean.getFileSize());
        if ("true".equals(previewDTO.getIsMin())) {
            previewer.imageThumbnailPreview(httpServletResponse, previewFile);
        } else {
            previewer.imageOriginalPreview(httpServletResponse, previewFile);
        }

    }

    @Override
    public void deleteFile(FileBean fileBean) {
        Deleter deleter = null;

        deleter = ufopFactory.getDeleter(fileBean.getStorageType());
        DeleteFile deleteFile = new DeleteFile();
        deleteFile.setFileUrl(fileBean.getFileUrl());
//        deleteFile.setTimeStampName(fileBean.getTimeStampName());
        deleter.delete(deleteFile);
    }

    @Override
    public StorageBean selectStorageBean(StorageBean storageBean) {
        LambdaQueryWrapper<StorageBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StorageBean::getUserId, storageBean.getUserId());
        return storageMapper.selectOne(lambdaQueryWrapper);

    }

    @Override
    public void insertStorageBean(StorageBean storageBean) {
        storageMapper.insert(storageBean);
    }

    @Override
    public void updateStorageBean(StorageBean storageBean) {
        LambdaUpdateWrapper<StorageBean> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(StorageBean::getStorageSize, storageBean.getStorageSize())
                .eq(StorageBean::getStorageId, storageBean.getStorageId())
                .eq(StorageBean::getUserId, storageBean.getUserId());
        storageMapper.update(null, lambdaUpdateWrapper);
    }

    @Override
    public StorageBean selectStorageByUser(StorageBean storageBean) {
        LambdaQueryWrapper<StorageBean> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StorageBean::getUserId, storageBean.getUserId());
        return storageMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public Long selectStorageSizeByUserId(Long userId){
        return userFileMapper.selectStorageSizeByUserId(userId);
    }
}
