package com.qiwenshare.file.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiwenshare.file.api.IShareFileService;
import com.qiwenshare.file.api.IShareService;
import com.qiwenshare.file.domain.Share;
import com.qiwenshare.file.domain.ShareFile;
import com.qiwenshare.file.mapper.ShareFileMapper;
import com.qiwenshare.file.mapper.ShareMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ShareFileService extends ServiceImpl<ShareFileMapper, ShareFile> implements IShareFileService {

}
