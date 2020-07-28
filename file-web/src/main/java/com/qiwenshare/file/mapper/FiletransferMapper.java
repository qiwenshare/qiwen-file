package com.qiwenshare.file.mapper;


import com.qiwenshare.file.domain.StorageBean;
import com.qiwenshare.file.domain.UserImageBean;

import java.util.List;

public interface FiletransferMapper {

    void deleteUserImageById(UserImageBean userImageBean);

    /**
     * 插入用户头像
     *
     * @param userImageBean
     */
    void insertUserImage(UserImageBean userImageBean);

    List<UserImageBean> selectUserImage(long userId);

    List<UserImageBean> selectUserImageByUrl(String url);

    void deleteUserImageByIds(List<Integer> imageidList);

    StorageBean selectStorageBean(StorageBean storageBean);

    void insertStorageBean(StorageBean storageBean);

    void updateStorageBean(StorageBean storageBean);

    StorageBean selectStorageByUser(StorageBean storageBean);
}
