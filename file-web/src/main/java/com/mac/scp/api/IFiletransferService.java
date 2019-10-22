package com.mac.scp.api;

import com.mac.common.cbb.RestResult;
import com.mac.scp.domain.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

public interface IFiletransferService {


    /**
     * 删除用户头像通过id
     *
     * @param userImageBean 用户头像信息
     */
    void deleteUserImageById(UserImageBean userImageBean);


    /**
     * 用户头像插入数据库
     *
     * @param userImageBean 用户头像信息
     * @return 插入结果
     */
    RestResult<String> insertUserImage(UserImageBean userImageBean);


    /**
     * 上传头像
     *
     * @param request 请求
     * @return 结果
     */
    RestResult<String> uploadUserImage(HttpServletRequest request);

    /**
     * 上传文件
     * @param request 请求
     * @param fileBean 文件信息
     */
    void uploadFile(HttpServletRequest request, FileBean fileBean);

    /**
     * 选择用户头像
     *
     * @param userId 用户id
     * @return 返回用户头像列表
     */
    RestResult<List<UserImageBean>> selectUserImage(long userId);

    /**
     * 选择用户头像通过url
     *
     * @param url url路径
     * @return 头像列表
     */
    List<UserImageBean> selectUserImageByUrl(String url);


    void deleteUserImageByIds(List<Integer> imageidList);

    StorageBean selectStorageBean(StorageBean storageBean);

    void insertStorageBean(StorageBean storageBean);

    void updateStorageBean(StorageBean storageBean);

    StorageBean selectStorageByUser(StorageBean storageBean);
}
