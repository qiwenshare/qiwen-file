package com.qiwenshare.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiwenshare.file.domain.CommonFile;
import com.qiwenshare.file.vo.commonfile.CommonFileListVo;
import com.qiwenshare.file.vo.commonfile.CommonFileUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommonFileMapper extends BaseMapper<CommonFile> {
    List<CommonFileUser> selectCommonFileUser(@Param("userId") String userId);
    List<CommonFileListVo> selectCommonFileByUser(@Param("userId") String userId, @Param("sessionUserId") String sessionUserId);

}
