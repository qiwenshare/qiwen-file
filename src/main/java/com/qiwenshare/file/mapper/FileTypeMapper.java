package com.qiwenshare.file.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiwenshare.file.domain.FileType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileTypeMapper extends BaseMapper<FileType> {
    List<String> selectExtendNameByFileType(@Param("fileTypeId") Integer fileTypeId);

}
