package com.qiwenshare.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.Share;
import com.qiwenshare.file.domain.ShareFile;
import com.qiwenshare.file.vo.share.ShareFileListVO;
import com.qiwenshare.file.vo.share.ShareListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShareMapper  extends BaseMapper<Share> {

    List<ShareListVO> selectShareList(String shareFilePath,String shareBatchNum, Long beginCount, Long pageCount, Long userId);
    int selectShareListTotalCount(String shareFilePath,String shareBatchNum, Long userId);
}
