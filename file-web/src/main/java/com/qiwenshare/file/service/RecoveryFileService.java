package com.qiwenshare.file.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiwenshare.file.api.IRecoveryFileService;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.RecoveryFile;
import com.qiwenshare.file.mapper.FileMapper;
import com.qiwenshare.file.mapper.RecoveryFileMapper;
import org.springframework.stereotype.Service;

@Service
public class RecoveryFileService  extends ServiceImpl<RecoveryFileMapper, RecoveryFile> implements IRecoveryFileService {
}
