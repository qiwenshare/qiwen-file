package com.mac.scp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mac.scp.entity.FileStore;
import com.mac.scp.mapper.FileStoreMapper;
import com.mac.scp.service.FileStoreService;
import org.springframework.stereotype.Service;

/**
 * 文件存储 服务实现类
 *
 * @author WeiHongBin
 */
@Service
public class FileStoreServiceImpl extends ServiceImpl<FileStoreMapper, FileStore> implements FileStoreService {

}
