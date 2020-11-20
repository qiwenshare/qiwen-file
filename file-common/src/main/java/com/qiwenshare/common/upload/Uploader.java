package com.qiwenshare.common.upload;

import com.qiwenshare.common.domain.UploadFile;
import com.qiwenshare.common.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public abstract class Uploader {
    private static final Logger logger = LoggerFactory.getLogger(Uploader.class);
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
                    logger.error("目录创建失败:" + PathUtil.getStaticPath() + path);
                }
            } catch (Exception e) {
                logger.error("目录创建失败" + PathUtil.getStaticPath() + path);
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
            logger.error("生成安全随机数失败");
        }
        return ""
                + System.currentTimeMillis();

    }

    protected String getFileName(String fileName){
        return fileName.substring(0, fileName.lastIndexOf("."));
    }
}
