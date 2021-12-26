package com.qiwenshare.file.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qiwenshare.file.domain.Notice;
import com.qiwenshare.file.dto.notice.NoticeListDTO;

public interface INoticeService extends IService<Notice> {


    IPage<Notice> selectUserPage(NoticeListDTO noticeListDTO);

}
