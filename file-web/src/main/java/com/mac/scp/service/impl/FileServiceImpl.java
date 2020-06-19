package com.mac.scp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mac.scp.entity.File;
import com.mac.scp.mapper.FileMapper;
import com.mac.scp.service.FileService;
import org.springframework.stereotype.Service;

/**
 * 文件 服务实现类
 *
 * @author WeiHongBin
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

}
