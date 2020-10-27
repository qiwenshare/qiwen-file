package com.qiwenshare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiwenshare.common.cbb.RestResult;
import com.qiwenshare.file.domain.UserBean;

import java.util.List;

public interface IUserService extends IService<UserBean> {

    UserBean getUserBeanByToken(String token);

    UserBean selectUserByopenid(String openid);

    /**
     * 用户注册
     *
     * @param userBean 用户信息
     * @return 结果
     */
    RestResult<String> registerUser(UserBean userBean);

    /**
     * 添加用户
     * @param userBean
     * @return
     */
    UserBean addUser(UserBean userBean);

    /**
     * 用户登陆
     *
     * @param userBean 用户信息
     * @return 结果
     */
    UserBean loginUser(UserBean userBean);

    /**
     * 选择admin用户
     *
     * @return 用户列表
     */
    List<UserBean> selectAdminUserList();

    /**
     * 删除用户信息
     *
     * @param userBean 用户信息
     */
    void deleteUserInfo(UserBean userBean);

    /**
     * 获取用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    UserBean getUserInfoById(long userId);

    /**
     * 通過openId得到用戶信息
     * @param openId
     * @return
     */
    UserBean selectUserByopenId(String openId);

    /**
     * 通过用户名获取用户信息
     *
     * @param UserName 用户名
     * @return 用户信息
     */
    UserBean findUserInfoByName(String UserName);

    UserBean findUserInfoByTelephone(String telephone);

    /**
     * 通过用户名和密码获取用户信息
     *
     * @param userName 用户名
     * @param password 密码
     * @return 用户信息
     */
    UserBean findUserInfoByNameAndPassword(String userName, String password);

    /**
     * 修改用户信息
     *
     * @param userBean 用户信息
     * @return 结果
     */
    RestResult<String> updateUserInfo(UserBean userBean);

    void updateEmail(UserBean userBean);
    void updataImageUrl(UserBean userBean);
    /**
     * 选择所有用户列表
     *
     * @return 所有用户列表
     */
    List<UserBean> selectAllUserList();




}
