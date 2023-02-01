package com.qiwenshare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiwenshare.file.domain.CommonFile;
import com.qiwenshare.file.vo.commonfile.CommonFileListVo;
import com.qiwenshare.file.vo.commonfile.CommonFileUser;

import java.util.List;

public interface ICommonFileService extends IService<CommonFile> {
    List<CommonFileUser> selectCommonFileUser(String userId);
    List<CommonFileListVo> selectCommonFileByUser(String userId, String sessionUserId);
}