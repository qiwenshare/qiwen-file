package com.qiwenshare.file.dao.mapper;

import com.qiwenshare.file.dao.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @desc  Mapper 接口
 * @author dehui dou
 * @time 2020-10-21
 */
public interface UserMapper extends BaseMapper<User> {

    User getOneByUserName(String username);
}
