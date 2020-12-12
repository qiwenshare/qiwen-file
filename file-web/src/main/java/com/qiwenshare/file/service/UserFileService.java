package com.qiwenshare.file.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiwenshare.file.api.IUserFileService;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.mapper.FileMapper;
import com.qiwenshare.file.mapper.UserFileMapper;

import javax.annotation.Resource;
import java.util.List;

public class UserFileService  extends ServiceImpl<UserFileMapper, UserFile> implements IUserFileService {
    @Resource
    UserFileMapper userFileMapper;
    @Override
    public List<UserFile> selectUserFileByNameAndPath(String fileName, String filePath, Long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileName, fileName).eq(UserFile::getFilePath, filePath).eq(UserFile::getUserId, userId);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public void replaceUserFilePath(String filePath, String oldFilePath, Long userId) {
        userFileMapper.replaceFilePath(filePath, oldFilePath, userId);
    }
}
