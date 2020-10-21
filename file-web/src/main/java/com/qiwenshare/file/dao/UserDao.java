package com.qiwenshare.file.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qiwenshare.file.dao.entity.User;
import com.qiwenshare.file.dao.mapper.UserMapper;

/**
 * @author dehui dou
 * @date 2020/10/21 11:18
 * @description
 */
@Component
public class UserDao {
    @Autowired
    private UserMapper userMapper;

    /**
     * @author dehui dou
     * @description 获取用户
     * @param userId
     *            主键id
     * @return com.qiwenshare.file.dao.entity.User
     */
    public User getOne(Long userId) {
        return userMapper.selectById(userId);
    }

    /**
     * @author dehui dou
     * @description 获取用户
     * @param username
     *            用户名称
     * @param telephone
     *            电话
     * @param password
     *            密码
     * @return com.qiwenshare.file.dao.entity.User
     */
    public User getOneByNameAndPassWord(String username, String telephone, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq("username", username).or().eq("telephone", telephone));
        queryWrapper.eq("password", password);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * @author dehui dou
     * @description 获取用户
     * @param openId
     *            openId
     * @return com.qiwenshare.file.dao.entity.User
     */
    public User getOneByOpenId(String openId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openId", openId);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * @author dehui dou
     * @description 验证用户是否存在,关联表user,user_role,role
     * @param username
     * @return com.qiwenshare.file.dao.entity.User
     */
    public User getOneByUserName(String username) {
        return userMapper.getOneByUserName(username);
    }

    /**
     * @author dehui dou
     * @description 获取用户
     * @param telephone
     *            电话
     * @return com.qiwenshare.file.dao.entity.User
     */
    public User getOneByPhone(String telephone) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("telephone", telephone);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * @author dehui dou
     * @description 保存
     * @param user
     * @return int
     */
    public int save(User user) {
        return userMapper.insert(user);
    }
}
