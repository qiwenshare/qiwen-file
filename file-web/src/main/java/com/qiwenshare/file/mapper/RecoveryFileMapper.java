package com.qiwenshare.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiwenshare.file.domain.RecoveryFile;
import com.qiwenshare.file.vo.file.RecoveryFileListVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface RecoveryFileMapper extends BaseMapper<RecoveryFile> {
    List<RecoveryFileListVo> selectRecoveryFileList(@Param("userId") Long userId);
}
