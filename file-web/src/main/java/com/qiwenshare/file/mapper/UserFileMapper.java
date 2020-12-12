package com.qiwenshare.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.domain.UserFile;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface UserFileMapper extends BaseMapper<UserFile> {
    void replaceFilePath(@Param("filePath") String filePath, @Param("oldFilePath") String oldFilePath, @Param("userId") Long userId);
    Map<String, Object> userFileList(UserFile userFile);
}
