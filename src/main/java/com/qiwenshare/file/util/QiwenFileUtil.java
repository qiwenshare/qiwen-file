package com.qiwenshare.file.util;

import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.file.domain.UserFile;

public class QiwenFileUtil {

    public static UserFile getQiwenDir(long userId, String filePath, String fileName) {
        UserFile userFile = new UserFile();
        userFile.setUserId(userId);
        userFile.setFileId(null);
        userFile.setFileName(fileName);
        userFile.setFilePath(filePath);
        userFile.setExtendName(null);
        userFile.setIsDir(1);
        userFile.setUploadTime(DateUtil.getCurrentTime());
        userFile.setDeleteFlag(0);
        userFile.setDeleteBatchNum(null);
        return userFile;
    }
}
