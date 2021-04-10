package com.qiwenshare.file.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiwenshare.file.api.IShareService;
import com.qiwenshare.file.domain.RecoveryFile;
import com.qiwenshare.file.domain.Share;
import com.qiwenshare.file.domain.ShareFile;
import com.qiwenshare.file.dto.sharefile.ShareListDTO;
import com.qiwenshare.file.mapper.RecoveryFileMapper;
import com.qiwenshare.file.mapper.ShareMapper;
import com.qiwenshare.file.vo.share.ShareFileListVO;
import com.qiwenshare.file.vo.share.ShareListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class ShareService extends ServiceImpl<ShareMapper, Share> implements IShareService {

    @Resource
    ShareMapper shareMapper;

    @Override
    public List<ShareListVO> selectShareList(ShareListDTO shareListDTO, Long userId) {
        Long beginCount = (shareListDTO.getCurrentPage() - 1) * shareListDTO.getPageCount();
        return shareMapper.selectShareList(shareListDTO.getShareFilePath(),
                shareListDTO.getShareBatchNum(),
                beginCount, shareListDTO.getPageCount(), userId);
    }

    @Override
    public int selectShareListTotalCount(ShareListDTO shareListDTO, Long userId) {
        return shareMapper.selectShareListTotalCount(shareListDTO.getShareFilePath(), shareListDTO.getShareBatchNum(), userId);
    }
}
