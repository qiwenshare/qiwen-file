package com.qiwenshare.file.controller;

import com.alibaba.fastjson.JSON;
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

    @Scheduled(cron = "0 0/1 * * * ?")
    public void deleteFile() {
        log.info("111112");
        LambdaQueryWrapper<FileBean> fileBeanLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fileBeanLambdaQueryWrapper.eq(FileBean::getPointCount, 0);

        List<FileBean> fileBeanList = fileService.list(fileBeanLambdaQueryWrapper);
        for (int i = 0; i < fileBeanList.size(); i++) {
            FileBean fileBean = fileBeanList.get(i);
            log.info("删除本地文件：" + JSON.toJSONString(fileBean));
            fileService.deleteLocalFile(fileBean);
            fileService.removeById(fileBean.getFileId());
        }
        fileService.remove(fileBeanLambdaQueryWrapper);

        log.info("11111");
    }
}
