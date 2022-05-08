package com.qiwenshare.file.service;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.common.util.MimeUtils;
import com.qiwenshare.common.util.security.JwtUser;
import com.qiwenshare.common.util.security.SessionUtil;
import com.qiwenshare.file.api.IFiletransferService;
import com.qiwenshare.file.component.FileDealComp;
import com.qiwenshare.file.domain.*;
import com.qiwenshare.file.dto.file.DownloadFileDTO;
import com.qiwenshare.file.dto.file.PreviewDTO;
import com.qiwenshare.file.dto.file.UploadFileDTO;
import com.qiwenshare.file.io.QiwenFile;
import com.qiwenshare.file.mapper.*;
import com.qiwenshare.file.util.HttpsUtils;
import com.qiwenshare.file.util.QiwenFileUtil;
import com.qiwenshare.file.vo.file.UploadFileVo;
import com.qiwenshare.ufop.constant.StorageTypeEnum;
import com.qiwenshare.ufop.constant.UploadFileStatusEnum;
import com.qiwenshare.ufop.exception.operation.DownloadException;
import com.qiwenshare.ufop.exception.operation.UploadException;
import com.qiwenshare.ufop.factory.UFOPFactory;
import com.qiwenshare.ufop.operation.copy.Copier;
import com.qiwenshare.ufop.operation.copy.domain.CopyFile;
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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.nio.cs.ext.GBK;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class FiletransferService implements IFiletransferService {

    @Resource
    FileMapper fileMapper;

    @Resource
    UserFileMapper userFileMapper;

    @Resource
    UFOPFactory ufopFactory;
    @Resource
    FileDealComp fileDealComp;
    @Resource
    UploadTaskDetailMapper uploadTaskDetailMapper;
    @Resource
    UploadTaskMapper uploadTaskMapper;
    @Resource
    ImageMapper imageMapper;
    @Resource
    MusicMapper musicMapper;

    @Resource
    PictureFileMapper pictureFileMapper;


    @Override
    public UploadFileVo uploadFileSpeed(UploadFileDTO uploadFileDTO) {
        UploadFileVo uploadFileVo = new UploadFileVo();
        JwtUser sessionUserBean = SessionUtil.getSession();
        Map<String, Object> param = new HashMap<>();
        param.put("identifier", uploadFileDTO.getIdentifier());
        List<FileBean> list = fileMapper.selectByMap(param);

        String filePath = uploadFileDTO.getFilePath();
        String relativePath = uploadFileDTO.getRelativePath();
        QiwenFile qiwenFile = null;
        if (relativePath.contains("/")) {
            qiwenFile = new QiwenFile(filePath, relativePath, false);
        } else {
            qiwenFile = new QiwenFile(filePath, uploadFileDTO.getFilename(), false);
        }

        if (list != null && !list.isEmpty()) {
            FileBean file = list.get(0);

            if (relativePath.contains("/")) {
                fileDealComp.restoreParentFilePath(qiwenFile, sessionUserBean.getUserId());
                fileDealComp.deleteRepeatSubDirFile(uploadFileDTO.getFilePath(), sessionUserBean.getUserId());
            }

            UserFile userFile = new UserFile(qiwenFile, sessionUserBean.getUserId(), file.getFileId());
            UserFile param1 = QiwenFileUtil.searchQiwenFileParam(userFile);
            List<UserFile> userFileList = userFileMapper.selectList(new QueryWrapper<>(param1));
            if (userFileList.size() <= 0) {
                userFileMapper.insert(userFile);
                fileDealComp.uploadESByUserFileId(userFile.getUserFileId());
            }

            uploadFileVo.setSkipUpload(true);
        } else {
            uploadFileVo.setSkipUpload(false);

            List<Integer> uploaded = uploadTaskDetailMapper.selectUploadedChunkNumList(uploadFileDTO.getIdentifier());
            if (uploaded != null && !uploaded.isEmpty()) {
                uploadFileVo.setUploaded(uploaded);
            } else {

                LambdaQueryWrapper<UploadTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UploadTask::getIdentifier, uploadFileDTO.getIdentifier());
                List<UploadTask> rslist = uploadTaskMapper.selectList(lambdaQueryWrapper);
                if (rslist == null || rslist.isEmpty()) {
                    UploadTask uploadTask = new UploadTask();
                    uploadTask.setIdentifier(uploadFileDTO.getIdentifier());
                    uploadTask.setUploadTime(DateUtil.getCurrentTime());
                    uploadTask.setUploadStatus(UploadFileStatusEnum.UNCOMPLATE.getCode());
                    uploadTask.setFileName(qiwenFile.getNameNotExtend());
                    uploadTask.setFilePath(qiwenFile.getParent());
                    uploadTask.setExtendName(qiwenFile.getExtendName());
                    uploadTask.setUserId(sessionUserBean.getUserId());
                    uploadTaskMapper.insert(uploadTask);
                }
            }

        }
        return uploadFileVo;
    }

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
        List<UploadFileResult> uploadFileResultList;
        try {
            uploadFileResultList = uploader.upload(request, uploadFile);
        } catch (Exception e) {
            log.error("上传失败，请检查UFOP连接配置是否正确");
            throw new UploadException("上传失败");
        }
        for (int i = 0; i < uploadFileResultList.size(); i++){
            UploadFileResult uploadFileResult = uploadFileResultList.get(i);
            String relativePath = uploadFileDto.getRelativePath();
            QiwenFile qiwenFile = null;
            if (relativePath.contains("/")) {
                qiwenFile = new QiwenFile(uploadFileDto.getFilePath(), relativePath, false);
            } else {
                qiwenFile = new QiwenFile(uploadFileDto.getFilePath(), uploadFileDto.getFilename(), false);
            }

            if (UploadFileStatusEnum.SUCCESS.equals(uploadFileResult.getStatus())){
                FileBean fileBean = new FileBean(uploadFileResult);
                fileBean.setCreateUserId(userId);
                fileMapper.insert(fileBean);


                UserFile userFile = new UserFile(qiwenFile, userId, fileBean.getFileId());

                if (relativePath.contains("/")) {
                    fileDealComp.restoreParentFilePath(qiwenFile, userId);
                    fileDealComp.deleteRepeatSubDirFile(uploadFileDto.getFilePath(), userId);
                }

                UserFile param = QiwenFileUtil.searchQiwenFileParam(userFile);
                List<UserFile> userFileList = userFileMapper.selectList(new QueryWrapper<>(param));
                if (userFileList.size() > 0) {
                    String fileName = fileDealComp.getRepeatFileName(userFile, userFile.getFilePath());
                    userFile.setFileName(fileName);
                }
                userFileMapper.insert(userFile);

                fileDealComp.uploadESByUserFileId(userFile.getUserFileId());


                LambdaQueryWrapper<UploadTaskDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UploadTaskDetail::getIdentifier, uploadFileDto.getIdentifier());
                uploadTaskDetailMapper.delete(lambdaQueryWrapper);

                LambdaUpdateWrapper<UploadTask> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.set(UploadTask::getUploadStatus, UploadFileStatusEnum.SUCCESS.getCode())
                        .eq(UploadTask::getIdentifier, uploadFileDto.getIdentifier());
                uploadTaskMapper.update(null, lambdaUpdateWrapper);
                try {
                    if (UFOPUtils.isImageFile(uploadFileResult.getExtendName())) {
                        BufferedImage src = uploadFileResult.getBufferedImage();
                        Image image = new Image();
                        image.setImageWidth(src.getWidth());
                        image.setImageHeight(src.getHeight());
                        image.setFileId(fileBean.getFileId());
                        imageMapper.insert(image);
                    }
                    if ("mp3".equalsIgnoreCase(uploadFileResult.getExtendName())) {
                        Downloader downloader = ufopFactory.getDownloader(uploadFileResult.getStorageType().getCode());
                        DownloadFile downloadFile = new DownloadFile();
                        downloadFile.setFileUrl(uploadFileResult.getFileUrl());
                        InputStream inputStream = downloader.getInputStream(downloadFile);
                        File outFile = UFOPUtils.getTempFile(uploadFileResult.getFileUrl());
                        if (!outFile.exists()) {
                            outFile.createNewFile();
                        }
                        FileOutputStream fileOutputStream = new FileOutputStream(outFile);
                        IOUtils.copy(inputStream, fileOutputStream);
                        fileOutputStream.close();
                        Mp3File mp3file = new Mp3File(outFile);
                        Music music = new Music();
                        music.setMusicId(IdUtil.getSnowflakeNextIdStr());
                        music.setFileId(fileBean.getFileId());
                        if (mp3file.hasId3v1Tag()) {
                            ID3v1 id3v1Tag = mp3file.getId3v1Tag();
                            music.setTrack(formatChatset(id3v1Tag.getTrack()));
                            music.setArtist(formatChatset(id3v1Tag.getArtist()));
                            music.setTitle(formatChatset(id3v1Tag.getTitle()));
                            music.setAlbum(formatChatset(id3v1Tag.getAlbum()));
                            music.setYear(formatChatset(id3v1Tag.getYear()));
                            music.setGenre(formatChatset(id3v1Tag.getGenre() + " (" + id3v1Tag.getGenreDescription() + ")"));
                            music.setComment(formatChatset(id3v1Tag.getComment()));
                        }
                        Mp3File mp3file2 = new Mp3File(outFile);
                        if (mp3file2.hasId3v2Tag()) {
                            ID3v2 id3v2Tag = mp3file2.getId3v2Tag();
                            if (StringUtils.isEmpty(music.getTrack())) {
                                music.setTrack(formatChatset(id3v2Tag.getTrack()));
                            }
                            if (StringUtils.isEmpty(music.getArtist())) {
                                music.setArtist(formatChatset(id3v2Tag.getArtist()));
                            }
                            if (StringUtils.isEmpty(music.getTitle())) {
                                music.setTitle(formatChatset(id3v2Tag.getTitle()));
                            }
                            if (StringUtils.isEmpty(music.getAlbum())) {
                                music.setAlbum(formatChatset(id3v2Tag.getAlbum()));
                            }
                            if (StringUtils.isEmpty(music.getYear())) {
                                music.setYear(formatChatset(id3v2Tag.getYear()));
                            }
                            if (StringUtils.isEmpty(music.getGenre())) {
                                music.setGenre(formatChatset(id3v2Tag.getGenre() + " (" + id3v2Tag.getGenreDescription() + ")"));
                            }
                            if (StringUtils.isEmpty(music.getComment())) {
                                music.setComment(formatChatset(id3v2Tag.getComment()));
                            }
                            music.setLyrics(formatChatset(id3v2Tag.getLyrics()));
                            music.setComposer(formatChatset(id3v2Tag.getComposer()));
                            music.setPublicer(formatChatset(id3v2Tag.getPublisher()));
                            music.setOriginalArtist(formatChatset(id3v2Tag.getOriginalArtist()));
                            music.setAlbumArtist(formatChatset(id3v2Tag.getAlbumArtist()));
                            music.setCopyright(formatChatset(id3v2Tag.getCopyright()));
                            music.setUrl(formatChatset(id3v2Tag.getUrl()));
                            music.setEncoder(formatChatset(id3v2Tag.getEncoder()));

                            byte[] albumImageData = id3v2Tag.getAlbumImage();

                            if (albumImageData != null) {
                                File outFile1 = UFOPUtils.getTempFile(uploadFileResult.getFileName() + ".png");
                                if (!outFile1.exists()) {
                                    outFile1.createNewFile();
                                }
                                music.setAlbumImage(Base64.getEncoder().encodeToString(albumImageData));
                                System.out.println("Have album image data, length: " + albumImageData.length + " bytes");
                                System.out.println("Album image mime type: " + id3v2Tag.getAlbumImageMimeType());
                            }
                        }
                        if (StringUtils.isEmpty(music.getLyrics())) {
                            try {
                                String lyc = getLyc(music.getArtist(), music.getTitle());
                                music.setLyrics(lyc);
                            } catch (Exception e) {
                                log.info(e.getMessage());
                            }
                        }
                        MP3File f = (MP3File) AudioFileIO.read(outFile);
                        MP3AudioHeader audioHeader = (MP3AudioHeader) f.getAudioHeader();
                        music.setTrackLength(Float.parseFloat(audioHeader.getTrackLength() + ""));
                        musicMapper.insert(music);
                    }
                } catch (Exception e) {
                    log.error("生成图片缩略图失败！", e);
                }

            } else if (UploadFileStatusEnum.UNCOMPLATE.equals(uploadFileResult.getStatus())) {
                UploadTaskDetail uploadTaskDetail = new UploadTaskDetail();
                uploadTaskDetail.setFilePath(qiwenFile.getParent());
                uploadTaskDetail.setFilename(qiwenFile.getNameNotExtend());
                uploadTaskDetail.setChunkNumber(uploadFileDto.getChunkNumber());
                uploadTaskDetail.setChunkSize((int)uploadFileDto.getChunkSize());
                uploadTaskDetail.setRelativePath(uploadFileDto.getRelativePath());
                uploadTaskDetail.setTotalChunks(uploadFileDto.getTotalChunks());
                uploadTaskDetail.setTotalSize((int)uploadFileDto.getTotalSize());
                uploadTaskDetail.setIdentifier(uploadFileDto.getIdentifier());
                uploadTaskDetailMapper.insert(uploadTaskDetail);

            } else if (UploadFileStatusEnum.FAIL.equals(uploadFileResult.getStatus())) {
                LambdaQueryWrapper<UploadTaskDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UploadTaskDetail::getIdentifier, uploadFileDto.getIdentifier());
                uploadTaskDetailMapper.delete(lambdaQueryWrapper);

                LambdaUpdateWrapper<UploadTask> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.set(UploadTask::getUploadStatus, UploadFileStatusEnum.FAIL.getCode())
                        .eq(UploadTask::getIdentifier, uploadFileDto.getIdentifier());
                uploadTaskMapper.update(null, lambdaUpdateWrapper);
            }
        }

    }

    private String formatChatset(String str) {
        if (str == null) {
            return "";
        }
        if (java.nio.charset.Charset.forName("ISO-8859-1").newEncoder().canEncode(str)) {
            byte[] bytes = str.getBytes(StandardCharsets.ISO_8859_1);
            return new String(bytes, Charset.forName("GBK"));
        }
        return str;
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
            httpServletResponse.setContentLengthLong(fileBean.getFileSize());
            downloader.download(httpServletResponse, downloadFile);
        } else {

            List<UserFile> userFileList = userFileMapper.selectUserFileByLikeRightFilePath(userFile.getFilePath() + "/" + userFile.getFileName()
                    , userFile.getUserId());
            List<String> userFileIds = userFileList.stream().map(UserFile::getUserFileId).collect(Collectors.toList());

            downloadUserFileList(httpServletResponse, userFile.getFilePath(), userFile.getFileName(), userFileIds);
        }
    }
    
    public String getLyc(String singerName, String mp3Name) {
   
        String s = HttpsUtils.doGetString("https://c.y.qq.com/splcloud/fcgi-bin/smartbox_new.fcg?_=1651992748984&cv=4747474&ct=24&format=json&inCharset=utf-8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=1&uin=0&g_tk_new_20200303=5381&g_tk=5381&hostUin=0&is_xml=0&key=" + mp3Name);
        Map map = JSON.parseObject(s, Map.class);
        Map data = (Map) map.get("data");
        Map song = (Map) data.get("song");
        List<Map> list = (List<Map>) song.get("itemlist");
        String singer = "";
        String id = "";
        String mid = "";
        for (Map item : list) {
            singer = (String) item.get("singer");
            id = (String) item.get("id");
            mid = (String) item.get("mid");
            if (singer.equals(singerName)) {
                break;
            }
        }

        String s1 = HttpsUtils.doGetString("https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?_=1651993218842&cv=4747474&ct=24&format=json&inCharset=utf-8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=1&uin=0&g_tk_new_20200303=5381&g_tk=5381&loginUin=0&" +
                "songmid="+mid+"&" +
                "musicid=" + id);
        return s1;
    }

    @Override
    public void downloadUserFileList(HttpServletResponse httpServletResponse, String filePath, String fileName, List<String> userFileIds) {
        String staticPath = UFOPUtils.getStaticPath();
        String tempPath = staticPath + "temp" + File.separator;
        File tempDirFile = new File(tempPath);
        if (!tempDirFile.exists()) {
            tempDirFile.mkdirs();
        }

        FileOutputStream f = null;
        try {
            f = new FileOutputStream(tempPath + fileName + ".zip");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        CheckedOutputStream csum = new CheckedOutputStream(f, new Adler32());
        ZipOutputStream zos = new ZipOutputStream(csum);
        BufferedOutputStream out = new BufferedOutputStream(zos);

        try {
            for (String userFileId : userFileIds) {
                UserFile userFile1 = userFileMapper.selectById(userFileId);
                if (userFile1.getIsDir() == 0) {
                    FileBean fileBean = fileMapper.selectById(userFile1.getFileId());
                    Downloader downloader = ufopFactory.getDownloader(fileBean.getStorageType());
                    if (downloader == null) {
                        log.error("下载失败，文件存储类型不支持下载，storageType:{}", fileBean.getStorageType());
                        throw new UploadException("下载失败");
                    }
                    DownloadFile downloadFile = new DownloadFile();
                    downloadFile.setFileUrl(fileBean.getFileUrl());
                    InputStream inputStream = downloader.getInputStream(downloadFile);
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    try {
                        QiwenFile qiwenFile = new QiwenFile(userFile1.getFilePath().replaceFirst(filePath, ""), userFile1.getFileName() + "." + userFile1.getExtendName(), false);
                        zos.putNextEntry(new ZipEntry(qiwenFile.getPath()));

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
                        IOUtils.closeQuietly(bis);
                        try {
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    QiwenFile qiwenFile = new QiwenFile(userFile1.getFilePath(), userFile1.getFileName(), true);
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(qiwenFile.getPath() + QiwenFile.separator));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
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
        String zipPath = "";
        try {
            Downloader downloader = ufopFactory.getDownloader(StorageTypeEnum.LOCAL.getCode());
            DownloadFile downloadFile = new DownloadFile();
            downloadFile.setFileUrl("temp" + File.separator + fileName + ".zip");
            File tempFile = new File(UFOPUtils.getStaticPath() + downloadFile.getFileUrl());
            httpServletResponse.setContentLengthLong(tempFile.length());
            downloader.download(httpServletResponse, downloadFile);
            zipPath = UFOPUtils.getStaticPath() + "temp" + File.separator + fileName + ".zip";
        } catch (Exception e) {
            //org.apache.catalina.connector.ClientAbortException: java.io.IOException: Connection reset by peer
            if (e.getMessage().contains("ClientAbortException")) {
                //该异常忽略不做处理
            } else {
                log.error("下传zip文件出现异常：{}", e.getMessage());
            }

        } finally {
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
        try {
            if ("true".equals(previewDTO.getIsMin())) {
                previewer.imageThumbnailPreview(httpServletResponse, previewFile);
            } else {
                previewer.imageOriginalPreview(httpServletResponse, previewFile);
            }
        } catch (Exception e){
                //org.apache.catalina.connector.ClientAbortException: java.io.IOException: 你的主机中的软件中止了一个已建立的连接。
                if (e.getMessage().contains("ClientAbortException")) {
                //该异常忽略不做处理
            } else {
                log.error("预览文件出现异常：{}", e.getMessage());
            }

        }

    }

    @Override
    public void previewPictureFile(HttpServletResponse httpServletResponse, PreviewDTO previewDTO) {
        byte[] bytesUrl = Base64.getDecoder().decode(previewDTO.getUrl());
        PictureFile pictureFile = new PictureFile();
        pictureFile.setFileUrl(new String(bytesUrl));
        pictureFile = pictureFileMapper.selectOne(new QueryWrapper<>(pictureFile));
        Previewer previewer = ufopFactory.getPreviewer(pictureFile.getStorageType());
        if (previewer == null) {
            log.error("预览失败，文件存储类型不支持预览，storageType:{}", pictureFile.getStorageType());
            throw new UploadException("预览失败");
        }
        PreviewFile previewFile = new PreviewFile();
        previewFile.setFileUrl(pictureFile.getFileUrl());
//        previewFile.setFileSize(pictureFile.getFileSize());
        try {

            String mime= MimeUtils.getMime(pictureFile.getExtendName());
            httpServletResponse.setHeader("Content-Type", mime);

            String fileName = pictureFile.getFileName() + "." + pictureFile.getExtendName();
            try {
                fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            httpServletResponse.addHeader("Content-Disposition", "fileName=" + fileName);// 设置文件名

            previewer.imageOriginalPreview(httpServletResponse, previewFile);
        } catch (Exception e){
            //org.apache.catalina.connector.ClientAbortException: java.io.IOException: 你的主机中的软件中止了一个已建立的连接。
            if (e.getMessage().contains("ClientAbortException")) {
                //该异常忽略不做处理
            } else {
                log.error("预览文件出现异常：{}", e.getMessage());
            }

        }
    }

    @Override
    public void deleteFile(FileBean fileBean) {
        Deleter deleter = null;

        deleter = ufopFactory.getDeleter(fileBean.getStorageType());
        DeleteFile deleteFile = new DeleteFile();
        deleteFile.setFileUrl(fileBean.getFileUrl());
        deleter.delete(deleteFile);
    }



    @Override
    public Long selectStorageSizeByUserId(Long userId){
        return userFileMapper.selectStorageSizeByUserId(userId);
    }
}
