package com.qiwenshare.file.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiwenshare.file.api.IShareService;
import com.qiwenshare.file.api.IUserLoginInfoService;
import com.qiwenshare.file.domain.Share;
import com.qiwenshare.file.domain.UserLoginInfo;
import com.qiwenshare.file.dto.sharefile.ShareListDTO;
import com.qiwenshare.file.mapper.ShareMapper;
import com.qiwenshare.file.mapper.UserLoginInfoMapper;
import com.qiwenshare.file.vo.share.ShareListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class UserLoginInfoService extends ServiceImpl<UserLoginInfoMapper, UserLoginInfo> implements IUserLoginInfoService {


}
