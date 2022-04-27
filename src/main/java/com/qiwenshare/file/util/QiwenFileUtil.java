package com.qiwenshare.file.util;

import cn.hutool.core.util.IdUtil;
import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.io.QiwenFile;

public class QiwenFileUtil {


    public static UserFile getQiwenDir(long userId, String filePath, String fileName) {
        UserFile userFile = new UserFile();
        userFile.setUserFileId(IdUtil.getSnowflakeNextIdStr());
        userFile.setUserId(userId);
        userFile.setFileId(null);
        userFile.setFileName(fileName);
        userFile.setFilePath(QiwenFile.formatPath(filePath));
        userFile.setExtendName(null);
        userFile.setIsDir(1);
        userFile.setUploadTime(DateUtil.getCurrentTime());
        userFile.setDeleteFlag(0);
        userFile.setDeleteBatchNum(null);
        return userFile;
    }

    public static UserFile getQiwenFile(long userId, String fileId, String filePath, String fileName, String extendName) {
        UserFile userFile = new UserFile();
        userFile.setUserFileId(IdUtil.getSnowflakeNextIdStr());
        userFile.setUserId(userId);
        userFile.setFileId(fileId);
        userFile.setFileName(fileName);
        userFile.setFilePath(QiwenFile.formatPath(filePath));
        userFile.setExtendName(extendName);
        userFile.setIsDir(0);
        userFile.setUploadTime(DateUtil.getCurrentTime());
        userFile.setDeleteFlag(0);
        userFile.setDeleteBatchNum(null);
        return userFile;
    }

    public static UserFile searchQiwenFileParam(UserFile userFile) {
        UserFile param = new UserFile();
        userFile.setFilePath(QiwenFile.formatPath(userFile.getFilePath()));
        userFile.setFileName(userFile.getFileName());
        userFile.setExtendName(userFile.getExtendName());
        userFile.setDeleteFlag(0);
        userFile.setUserId(userFile.getUserId());
        userFile.setIsDir(0);
        return param;
    }

}
