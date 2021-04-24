package com.qiwenshare.common.util;

import cn.hutool.core.util.RandomUtil;
import com.qiwenshare.common.config.PropertiesUtil;
import com.qiwenshare.common.constant.FileConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PathUtil {
    /**
     * 获取项目所在的根目录路径 resources路径
     * @return
     */
    public static String getProjectRootPath() {
        String absolutePath = null;
        try {
            String url = ResourceUtils.getURL("classpath:").getPath();
            absolutePath = urlDecode(new File(url).getAbsolutePath()) + File.separator;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return absolutePath;
    }

    /**
     * 路径解码
     * @param url
     * @return
     */
    public static String urlDecode(String url){
        String decodeUrl = null;
        try {
            decodeUrl = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return  decodeUrl;
    }

    /**
     * 得到static路径
     *
     * @return
     */
    public static String getStaticPath() {
        String localStoragePath = PropertiesUtil.getProperty("qiwen-file.local-storage-path");
        if (StringUtils.isNotEmpty(localStoragePath)) {
            return localStoragePath;
        }else {
            String projectRootAbsolutePath = getProjectRootPath();

            int index = projectRootAbsolutePath.indexOf("file:");
            if (index != -1) {
                projectRootAbsolutePath = projectRootAbsolutePath.substring(0, index);
            }

            return projectRootAbsolutePath + "static" + File.separator;
        }


    }


    public static String getParentPath(String path) {
        return path.substring(0, path.lastIndexOf(FileConstant.pathSeparator));
    }

    public static void main(String[] args) {
        System.out.println(RandomUtil.randomLong(999999));
//        String path = "aaa/bbb/ccc/";
//        System.out.println(getParentPath(path));
//        String fileName = path.substring(path.lastIndexOf("/"));
//        System.out.println(fileName);
    }
}
