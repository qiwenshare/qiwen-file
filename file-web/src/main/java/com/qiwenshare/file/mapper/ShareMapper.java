package com.qiwenshare.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.Share;
import com.qiwenshare.file.domain.ShareFile;

import java.util.List;

public interface ShareMapper  extends BaseMapper<Share> {
    void batchInsertShareFile(List<ShareFile> shareFiles);
}
