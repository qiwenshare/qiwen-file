package com.qiwenshare.file.mapper;


import com.qiwenshare.file.domain.StorageBean;

import java.util.List;

public interface StorageMapper {



    StorageBean selectStorageBean(StorageBean storageBean);

    void insertStorageBean(StorageBean storageBean);

    void updateStorageBean(StorageBean storageBean);

    StorageBean selectStorageByUser(StorageBean storageBean);
}
