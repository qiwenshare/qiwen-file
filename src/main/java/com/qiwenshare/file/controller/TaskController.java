package com.qiwenshare.file.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.qiwenshare.file.component.FileDealComp;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.io.QiwenFile;
import com.qiwenshare.file.service.UserFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Controller
public class TaskController {

    @Resource
    UserFileService userFileService;
    @Resource
    FileDealComp fileDealComp;
    @Autowired
    private ElasticsearchClient elasticsearchClient;


    @Scheduled(initialDelay = 1000 * 60 * 60 * 24, fixedRate = Long.MAX_VALUE)
    public void updateElasticSearch() {

        try {
            elasticsearchClient.delete(d -> d.index("filesearch"));
        } catch (Exception e) {
            log.debug("删除ES失败:" + e);
        }

        List<UserFile> userfileList = userFileService.list();
        for (UserFile userFile : userfileList) {
            fileDealComp.uploadESByUserFileId(userFile.getUserFileId());
        }

    }

    @Scheduled(fixedRate = Long.MAX_VALUE)
    public void updateFilePath() {
        List<UserFile> list = userFileService.list();
        for (UserFile userFile : list) {
            QiwenFile qiwenFile = new QiwenFile(userFile.getFilePath(), true);
            String path = QiwenFile.formatPath(userFile.getFilePath());
            userFile.setFilePath(path);
            userFileService.updateById(userFile);
        }
    }
}
