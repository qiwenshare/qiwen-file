package com.qiwenshare.file.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.service.FileService;
import com.qiwenshare.file.service.UserFileService;
import com.qiwenshare.file.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Slf4j
@Controller
public class TaskController {

    @Resource
    UserFileService userFileService;

    @Resource
    FileService fileService;

    @Scheduled(cron = "* * * 0/1 * ?")
    public void deleteFile() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -3);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String threeDaysAgo = sdf.format(calendar.getTime());
        LambdaQueryWrapper<UserFile> userFileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userFileLambdaQueryWrapper.eq(UserFile::getDeleteFlag, 1)
                .lt(UserFile::getDeleteTime, threeDaysAgo + " 00:00:00");
        List<UserFile> userFiles = userFileService.list(userFileLambdaQueryWrapper);
        for (UserFile userFile : userFiles) {
            userFileService.removeById(userFile.getUserFileId());
            FileBean fileBean = fileService.getById(userFile.getFileId());
            Integer pointCount = fileBean.getPointCount();
            if (pointCount <= 1) {
                fileService.removeById(fileBean.getFileId());
                fileService.deleteLocalFile(fileBean);
            } else {
                fileBean.setPointCount(fileBean.getPointCount() - 1);
                fileService.updateById(fileBean);
            }
        }

        log.info("11111");
    }
}
