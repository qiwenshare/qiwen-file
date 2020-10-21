package com.qiwenshare.file.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.qiwenshare.file.dao.entity.Storage;
import com.qiwenshare.file.dao.mapper.StorageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author dehui dou
 * @date 2020/10/21 11:44
 * @description
 */
@Component
public class StorageDao {
    @Autowired
    private StorageMapper storageMapper;

    /**
     * @author dehui dou
     * @description 获取存储信息
     * @param userId
     *            用户id
     * @return com.qiwenshare.file.dao.entity.Storage
     */
    public Storage getOneByUserId(Long userId) {
        QueryWrapper<Storage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        List<Storage> storageList = storageMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(storageList)) {
            return storageList.get(0);
        }
        return null;
    }

    /**
     * @author dehui dou
     * @description 保存
     * @param storage
     * @return int
     */
    public int save(Storage storage) {
        return storageMapper.insert(storage);
    }

    /**
     * @author dehui dou
     * @description 修改存储大小
     * @param storageSize
     * @param userId
     * @param storageId
     * @return int
     */
    public int editStorageSizeByIdAndUserId(Long storageSize, Long userId, Long storageId) {
        if (userId == null || storageId == null) {
            return -1;
        }
        UpdateWrapper<Storage> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("storageSize", storageSize);
        updateWrapper.eq("storageId", storageId);
        updateWrapper.eq("userId", userId);
        return storageMapper.update(new Storage(), updateWrapper);
    }
}
