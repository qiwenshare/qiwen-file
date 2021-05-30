package com.qiwenshare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiwenshare.file.domain.RecoveryFile;
import com.qiwenshare.file.domain.Share;
import com.qiwenshare.file.domain.ShareFile;
import com.qiwenshare.file.dto.sharefile.ShareListDTO;
import com.qiwenshare.file.vo.share.ShareFileListVO;
import com.qiwenshare.file.vo.share.ShareListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IShareService  extends IService<Share> {
    List<ShareListVO> selectShareList(ShareListDTO shareListDTO, Long userId);
    int selectShareListTotalCount(ShareListDTO shareListDTO, Long userId);
}
