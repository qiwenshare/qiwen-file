package com.qiwenshare.file.controller;

import com.qiwenshare.file.api.IElasticSearchService;
import com.qiwenshare.file.component.FileDealComp;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.service.UserFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Controller
public class TaskController {

    @Resource
    UserFileService userFileService;
    @Autowired
    @Lazy
    private IElasticSearchService elasticSearchService;
    @Resource
    FileDealComp fileDealComp;


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
