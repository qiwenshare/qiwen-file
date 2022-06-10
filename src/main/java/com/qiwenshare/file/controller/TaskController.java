package com.qiwenshare.file.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qiwenshare.file.api.IShareFileService;
import com.qiwenshare.file.component.FileDealComp;
import com.qiwenshare.file.domain.ShareFile;
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
    @Resource
    IShareFileService shareFileService;
    @Autowired
    private ElasticsearchClient elasticsearchClient;


    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    public void updateElasticSearch() {
        List<UserFile> userfileList = userFileService.list(new QueryWrapper<UserFile>().eq("deleteFlag", 0));
        for (int i = 0; i < userfileList.size(); i++) {
            try {

                QiwenFile ufopFile = new QiwenFile(userfileList.get(i).getFilePath(), userfileList.get(i).getFileName(), userfileList.get(i).getIsDir() == 1);
                fileDealComp.restoreParentFilePath(ufopFile, userfileList.get(i).getUserId());
                if (i % 1000 == 0 || i == userfileList.size() - 1) {
                    log.info("目录健康检查进度：" + (i + 1) + "/" + userfileList.size());
                }

            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        userfileList = userFileService.list(new QueryWrapper<UserFile>().eq("deleteFlag", 0));
        for (UserFile userFile : userfileList) {
            fileDealComp.uploadESByUserFileId(userFile.getUserFileId());
        }

    }

    @Scheduled(fixedRate = Long.MAX_VALUE)
    public void updateFilePath() {
        List<UserFile> list = userFileService.list();
        for (UserFile userFile : list) {
            try {
                String path = QiwenFile.formatPath(userFile.getFilePath());
                if (!userFile.getFilePath().equals(path)) {
                    userFile.setFilePath(path);
                    userFileService.updateById(userFile);
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }

    @Scheduled(fixedRate = Long.MAX_VALUE)
    public void updateShareFilePath() {
        List<ShareFile> list = shareFileService.list();
        for (ShareFile shareFile : list) {
            try {
                String path = QiwenFile.formatPath(shareFile.getShareFilePath());
                shareFile.setShareFilePath(path);
                shareFileService.updateById(shareFile);
            } catch (Exception e) {
                //ignore
            }
        }
    }
}
