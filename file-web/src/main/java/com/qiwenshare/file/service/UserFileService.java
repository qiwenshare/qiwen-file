package com.qiwenshare.file.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiwenshare.file.api.IUserFileService;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.mapper.UserFileMapper;

public class UserFileService  extends ServiceImpl<UserFileMapper, UserFile> implements IUserFileService {
}
