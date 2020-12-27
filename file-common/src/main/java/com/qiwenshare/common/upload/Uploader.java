package com.qiwenshare.common.upload;

import com.qiwenshare.common.domain.UploadFile;
import com.qiwenshare.common.util.PathUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
public abstract class Uploader {

    public static final String ROOT_PATH = "upload";
    public static final String FILE_SEPARATOR = "/";
    // 文件大小限制，单位KB
    public static final int maxSize = 10000000;

    protected StandardMultipartHttpServletRequest request = null;

    public abstract List<UploadFile> upload(HttpServletRequest request);

    /**
     * 根据字符串创建本地目录 并按照日期建立子目录返回
     *
     * @param path
     * @return
     */
    protected String getSaveFilePath() {
        String path = ROOT_PATH;
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
        path = FILE_SEPARATOR + path + FILE_SEPARATOR + formater.format(new Date());
        File dir = new File(PathUtil.getStaticPath() + path);
        //LOG.error(PathUtil.getStaticPath() + path);
        if (!dir.exists()) {
            try {
                boolean isSuccessMakeDir = dir.mkdirs();
                if (!isSuccessMakeDir) {
                    log.error("目录创建失败:" + PathUtil.getStaticPath() + path);
                }
            } catch (Exception e) {
                log.error("目录创建失败" + PathUtil.getStaticPath() + path);
                return "";
            }
        }
        return path;
    }

    /**
     * 依据原始文件名生成新文件名
     *
     * @return
     */
    protected String getTimeStampName() {
        try {
            SecureRandom number = SecureRandom.getInstance("SHA1PRNG");
            return "" + number.nextInt(10000)
                    + System.currentTimeMillis();
        } catch (NoSuchAlgorithmException e) {
            log.error("生成安全随机数失败");
        }
        return ""
                + System.currentTimeMillis();

    }

    public synchronized boolean checkUploadStatus(UploadFile param, File confFile) throws IOException {
        //File confFile = new File(savePath, timeStampName + ".conf");
        RandomAccessFile confAccessFile = new RandomAccessFile(confFile, "rw");
        //设置文件长度
        confAccessFile.setLength(param.getTotalChunks());
        //设置起始偏移量
        confAccessFile.seek(param.getChunkNumber() - 1);
        //将指定的一个字节写入文件中 127，
        confAccessFile.write(Byte.MAX_VALUE);
        byte[] completeStatusList = FileUtils.readFileToByteArray(confFile);
        confAccessFile.close();//不关闭会造成无法占用
        //创建conf文件文件长度为总分片数，每上传一个分块即向conf文件中写入一个127，那么没上传的位置就是默认的0,已上传的就是127
        for (int i = 0; i < completeStatusList.length; i++) {
            if (completeStatusList[i] != Byte.MAX_VALUE) {
                return false;
            }
        }
        confFile.delete();
        return true;
    }

    protected String getFileName(String fileName){
        if (!fileName.contains(".")) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf("."));
    }
}
