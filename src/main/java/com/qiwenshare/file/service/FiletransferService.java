package com.qiwenshare.file.service;

import java.io.*;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import com.qiwenshare.common.util.DateUtil;

import com.qiwenshare.common.util.FileUtil;
import com.qiwenshare.file.api.IFiletransferService;

import com.qiwenshare.file.component.FileDealComp;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.dto.DownloadFileDTO;
import com.qiwenshare.file.dto.UploadFileDTO;
import com.qiwenshare.file.dto.file.PreviewDTO;
import com.qiwenshare.file.mapper.FileMapper;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.StorageBean;
import com.qiwenshare.file.mapper.StorageMapper;
import com.qiwenshare.file.mapper.UserFileMapper;
import com.qiwenshare.file.vo.file.FileListVo;
import com.qiwenshare.ufo.exception.DownloadException;
import com.qiwenshare.ufo.exception.UploadException;
import com.qiwenshare.ufo.factory.StorageTypeEnum;
import com.qiwenshare.ufo.factory.UFOFactory;
import com.qiwenshare.ufo.operation.delete.Deleter;
import com.qiwenshare.ufo.operation.delete.domain.DeleteFile;
import com.qiwenshare.ufo.operation.download.Downloader;
import com.qiwenshare.ufo.operation.download.domain.DownloadFile;
import com.qiwenshare.ufo.operation.preview.Previewer;
import com.qiwenshare.ufo.operation.preview.domain.PreviewFile;
import com.qiwenshare.ufo.operation.upload.Uploader;
import com.qiwenshare.ufo.operation.upload.domain.UploadFile;
import com.qiwenshare.ufo.util.PathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    UFOFactory ufoFactory;
    @Resource
    FileDealComp fileDealComp;

    @Override
    public void uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto, Long userId) {


        UploadFile uploadFile = new UploadFile();
        uploadFile.setChunkNumber(uploadFileDto.getChunkNumber());
        uploadFile.setChunkSize(uploadFileDto.getChunkSize());
        uploadFile.setTotalChunks(uploadFileDto.getTotalChunks());
        uploadFile.setIdentifier(uploadFileDto.getIdentifier());
        uploadFile.setTotalSize(uploadFileDto.getTotalSize());
        uploadFile.setCurrentChunkSize(uploadFileDto.getCurrentChunkSize());

        Uploader uploader = ufoFactory.getUploader();
        if (uploader == null) {
            log.error("上传失败，请检查storageType是否配置正确，当前storageType为：");
            throw new UploadException("上传失败");
        }

        List<UploadFile> uploadFileList = uploader.upload(request, uploadFile);
        for (int i = 0; i < uploadFileList.size(); i++){
            uploadFile = uploadFileList.get(i);
            FileBean fileBean = new FileBean();
            BeanUtil.copyProperties(uploadFileDto, fileBean);
            fileBean.setTimeStampName(uploadFile.getTimeStampName());
            if (uploadFile.getSuccess() == 1){
                fileBean.setFileUrl(uploadFile.getUrl());
                fileBean.setFileSize(uploadFile.getFileSize());
                fileBean.setStorageType(uploadFile.getStorageType());
                fileBean.setPointCount(1);
                fileMapper.insert(fileBean);
                UserFile userFile = new UserFile();
                userFile.setFilePath(uploadFileDto.getFilePath());
                userFile.setUserId(userId);
                userFile.setFileName(uploadFile.getFileName());
                userFile.setExtendName(uploadFile.getFileType());
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

            }

        }
    }

    @Override
    public void downloadFile(HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO) {
        UserFile userFile = userFileMapper.selectById(downloadFileDTO.getUserFileId());

        if (userFile.getIsDir() == 0) {

            FileBean fileBean = fileMapper.selectById(userFile.getFileId());
            Downloader downloader = ufoFactory.getDownloader(fileBean.getStorageType());
            if (downloader == null) {
                log.error("下载失败，文件存储类型不支持下载，storageType:{}", fileBean.getStorageType());
                throw new DownloadException("下载失败");
            }
            DownloadFile downloadFile = new DownloadFile();

            downloadFile.setFileUrl(fileBean.getFileUrl());
            downloadFile.setFileSize(fileBean.getFileSize());

            downloader.download(httpServletResponse, downloadFile);
        } else {
            LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.likeRight(UserFile::getFilePath, userFile.getFilePath() + userFile.getFileName() + "/")
                    .eq(UserFile::getUserId, userFile.getUserId())
                    .eq(UserFile::getIsDir, 0)
                    .eq(UserFile::getDeleteFlag, 0);
            List<UserFile> userFileList = userFileMapper.selectList(lambdaQueryWrapper);

            String staticPath = PathUtil.getStaticPath();
            String tempPath = staticPath + "temp" + File.separator;
            File tempFile = new File(tempPath);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
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

//            zos.setComment("");
            try {
                for (UserFile userFile1 : userFileList) {
                    FileBean fileBean = fileMapper.selectById(userFile1.getFileId());
                    Downloader downloader = ufoFactory.getDownloader(fileBean.getStorageType());
                    if (downloader == null) {
                        log.error("下载失败，文件存储类型不支持下载，storageType:{}, isOSS:{}", fileBean.getStorageType());
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
            Downloader downloader = ufoFactory.getDownloader(StorageTypeEnum.LOCAL.getStorageType());
            DownloadFile downloadFile = new DownloadFile();
            downloadFile.setFileUrl("temp" + File.separator+userFile.getFileName() + ".zip");
            downloader.download(httpServletResponse, downloadFile);
            String zipPath = PathUtil.getStaticPath() + "temp" + File.separator+userFile.getFileName() + ".zip";
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
        Previewer previewer = ufoFactory.getPreviewer(fileBean.getStorageType());
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

        deleter = ufoFactory.getDeleter(fileBean.getStorageType());
        DeleteFile deleteFile = new DeleteFile();
        deleteFile.setFileUrl(fileBean.getFileUrl());
        deleteFile.setTimeStampName(fileBean.getTimeStampName());
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
