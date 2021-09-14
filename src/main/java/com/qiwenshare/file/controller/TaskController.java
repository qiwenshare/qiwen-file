package com.qiwenshare.file.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qiwenshare.file.api.IElasticSearchService;
import com.qiwenshare.file.component.FileDealComp;
import com.qiwenshare.file.config.es.FileSearch;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.service.FileService;
import com.qiwenshare.file.service.FiletransferService;
import com.qiwenshare.file.service.UserFileService;
import com.qiwenshare.file.service.UserService;
import com.qiwenshare.file.vo.file.FileListVo;
import com.qiwenshare.ufop.factory.UFOPFactory;
import com.qiwenshare.ufop.operation.read.Reader;
import com.qiwenshare.ufop.operation.read.domain.ReadFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
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
    FileService fileService;
    @Resource
    UserFileService userFileService;
    @Resource
    FiletransferService filetransferService;
    @Autowired
    private IElasticSearchService elasticSearchService;
    @Resource
    FileDealComp fileDealComp;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void deleteFile() {

        LambdaQueryWrapper<FileBean> fileBeanLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fileBeanLambdaQueryWrapper.eq(FileBean::getPointCount, 0);

        List<FileBean> fileBeanList = fileService.list(fileBeanLambdaQueryWrapper);
        for (int i = 0; i < fileBeanList.size(); i++) {
            FileBean fileBean = fileBeanList.get(i);
            log.info("删除本地文件：" + JSON.toJSONString(fileBean));
            try {
                filetransferService.deleteFile(fileBean);
                fileService.removeById(fileBean.getFileId());
            } catch (Exception e) {
                log.error("删除本地文件失败：" + JSON.toJSONString(fileBean));
            }
        }

    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    public void updateElasticSearch() {

        try {
            elasticSearchService.deleteAll();
        } catch (Exception e) {
            log.debug("删除ES失败:" + e);
        }

        List<UserFile> userfileList = userFileService.list();
        for (UserFile userFile : userfileList) {
            fileDealComp.uploadESByUserFileId(userFile.getUserFileId());
        }

    }
}
