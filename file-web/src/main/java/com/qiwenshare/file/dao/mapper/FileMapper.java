package com.qiwenshare.file.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiwenshare.file.dao.entity.File;

/**
 * @desc Mapper 接口
 * @author dehui dou
 * @time 2020-10-21
 */
public interface FileMapper extends BaseMapper<File> {

    void saveBatch(@Param("fileList") List<File> fileList);

    void updateFile(File file);

    void editFilepathByPathAndName(@Param("oldFilePath") String oldFilePath, @Param("newFilePath") String newFilePath,
        @Param("fileName") String fileName, @Param("extendName") String extendName);
}
