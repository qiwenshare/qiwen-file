package com.qiwenshare.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.UserBean;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.vo.file.FileListVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserFileMapper extends BaseMapper<UserFile> {
    void replaceFilePath(@Param("filePath") String filePath, @Param("oldFilePath") String oldFilePath, @Param("userId") Long userId);
    List<FileListVo> userFileList(@Param("userFile") UserFile userFile, Long beginCount, Long pageCount);

    void updateFilepathByPathAndName(String oldfilePath, String newfilePath, String fileName, String extendName, long userId);
    void updateFilepathByFilepath(String oldfilePath, String newfilePath, long userId);

    void batchInsertByPathAndName(@Param("oldFilePath") String oldFilePath,
                                  @Param("newFilePath") String newfilePath,
                                  @Param("fileName") String fileName,
                                  @Param("extendName") String extendName,
                                  @Param("userId") long userId);

    void batchInsertByFilepath(@Param("oldFilePath") String oldFilePath,
                               @Param("newFilePath") String newfilePath,
                               @Param("userId") long userId);

    List<FileListVo> selectFileByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, long userId);
    Long selectCountByExtendName(List<String> fileNameList, Long beginCount, Long pageCount, long userId);
    List<FileListVo> selectFileNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, long userId);
    Long selectCountNotInExtendNames(List<String> fileNameList, Long beginCount, Long pageCount, long userId);
    Long selectStorageSizeByUserId(@Param("userId") Long userId);
}
