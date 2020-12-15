package com.qiwenshare.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.domain.UserFile;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserFileMapper extends BaseMapper<UserFile> {
    void replaceFilePath(@Param("filePath") String filePath, @Param("oldFilePath") String oldFilePath, @Param("userId") Long userId);
    List<Map<String, Object>> userFileList(UserFile userFile, Long beginCount, Long pageCount);

    void updateFilepathByPathAndName(String oldfilePath, String newfilePath, String fileName, String extendName);
    void updateFilepathByFilepath(String oldfilePath, String newfilePath);

    List<Map<String, Object>> selectFileByExtendName(List<String> fileNameList, long userId);
    List<Map<String, Object>> selectFileNotInExtendNames(List<String> fileNameList, long userId);

    Long selectStorageSizeByUserId(Long userId);
}
