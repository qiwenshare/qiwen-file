package com.qiwenshare.file.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiwenshare.file.api.IFileService;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.mapper.FileMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class FileService extends ServiceImpl<FileMapper, FileBean> implements IFileService {

    @Resource
    FileMapper fileMapper;


    @Override
    public void increaseFilePointCount(Long fileId) {
        FileBean fileBean = fileMapper.selectById(fileId);
        fileBean.setPointCount(fileBean.getPointCount()+1);
        fileMapper.updateById(fileBean);
    }

    @Override
    public void decreaseFilePointCount(Long fileId) {
        FileBean fileBean = fileMapper.selectById(fileId);
        fileBean.setPointCount(fileBean.getPointCount()-1);
        fileMapper.updateById(fileBean);
    }


}
