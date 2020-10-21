package com.qiwenshare.file.dao;

import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.qiwenshare.file.dao.entity.File;
import com.qiwenshare.file.dao.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author dehui dou
 * @date 2020/10/21 11:18
 * @description
 */
@Component
public class FileDao {
    @Autowired
    private FileMapper fileMapper;

    /**
     * @author dehui dou
     * @description 获取文件
     * @param fileId
     * @return com.qiwenshare.file.dao.entity.File
     */
    public File getOne(Long fileId) {
        return fileMapper.selectById(fileId);
    }

    /**
     * @author dehui dou
     * @description 保存文件
     * @param file
     * @return int
     */
    public int save(File file) {
        return fileMapper.insert(file);
    }

    /**
     * @author dehui dou
     * @description 批量保存
     * @param fileList
     * @return void
     */
    public void saveBatch(List<File> fileList) {
        fileMapper.saveBatch(fileList);
    }

    /**
     * @author dehui dou
     * @description 修改文件
     * @param file
     * @return void
     */
    public void edit(File file) {
        fileMapper.updateFile(file);
    }

    /**
     * @author dehui dou
     * @description 修改文件路径,通过旧路径like匹配
     * @param oldFilePath
     * @param newFilePath
     * @return void
     */
    public int editFilePathByFilePath(String oldFilePath, String newFilePath) {
        if (StringUtils.isEmpty(oldFilePath)) {
            return -1;
        }
        UpdateWrapper<File> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("filePath", newFilePath);
        updateWrapper.likeRight("filePath", oldFilePath);
        return fileMapper.update(new File(), updateWrapper);
    }

    /**
     * @author dehui dou
     * @description 修改文件路径
     * @param oldFilePath
     *            旧路径
     * @param newFilePath
     *            新路径
     * @param fileName
     *            文件名称
     * @param extendName
     *            扩展名
     * @return void
     */
    public void editFilepathByPathAndName(String oldFilePath, String newFilePath, String fileName, String extendName) {
        fileMapper.editFilepathByPathAndName(oldFilePath, newFilePath, fileName, extendName);
    }

    /**
     * @author dehui dou
     * @description 获取文件列表
     * @param fileIdList
     *            文件id列表
     * @return java.util.List<com.qiwenshare.file.dao.entity.File>
     */
    public List<File> getListByIdList(List<Long> fileIdList) {
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("fileId", fileIdList);
        return fileMapper.selectList(queryWrapper);
    }

    /**
     * @author dehui dou
     * @description 获取文件列表
     * @param userId
     *            用户id
     * @param extendNameList
     *            文件扩展名列表
     * @return java.util.List<com.qiwenshare.file.dao.entity.File>
     */
    public List<File> getListByUserIdExtendNameList(Long userId, List<String> extendNameList) {
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.in("extendName", extendNameList);
        return fileMapper.selectList(queryWrapper);
    }

    /**
     * @author dehui dou
     * @description 获取文件列表
     * @param fileName
     *            文件名称
     * @param filePath
     *            文件路径
     * @return java.util.List<com.qiwenshare.file.dao.entity.File>
     */
    public List<File> getListByNameAndPath(String fileName, String filePath) {
        QueryWrapper<File> qw = new QueryWrapper<>();
        qw.eq("fileName", fileName);
        qw.eq("filePath", filePath);
        return fileMapper.selectList(qw);
    }

    /**
     * @author dehui dou
     * @description 获取文件列表
     * @param userId
     *            用户id
     * @return java.util.List<com.qiwenshare.file.dao.entity.File>
     */
    public List<File> getListByUserIdAndIsDir(Long userId) {
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("isDir", 1);
        return fileMapper.selectList(queryWrapper);
    }

    /**
     * @author dehui dou
     * @description 获取文件列表
     * @param filePath
     *            文件路径
     * @param userId
     *            用户id
     * @return java.util.List<com.qiwenshare.file.dao.entity.File>
     */
    public List<File> getListByFilePathAndUserId(String filePath, Long userId) {
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("filePath", filePath);
        queryWrapper.orderByDesc("isDir").orderByDesc("fileName");
        return fileMapper.selectList(queryWrapper);
    }

    /**
     * @author dehui dou
     * @description 获取文件列表,路径有匹配查询
     * @param filePath
     *            文件路径
     * @return java.util.List<com.qiwenshare.file.dao.entity.File>
     */
    public List<File> getListByFilePathLike(String filePath) {
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("filePath", filePath);
        return fileMapper.selectList(queryWrapper);
    }

    /**
     * @author dehui dou
     * @description 删除文件
     * @param fileId
     * @return int
     */
    public int removeById(Long fileId) {
        return fileMapper.deleteById(fileId);
    }

    /**
     * @author dehui dou
     * @description 删除文件列表
     * @param fileIdList
     * @return int
     */
    public int removeByIdList(List<Long> fileIdList) {
        if (CollectionUtils.isEmpty(fileIdList)) {
            return -1;
        }
        return fileMapper.deleteBatchIds(fileIdList);
    }
}
