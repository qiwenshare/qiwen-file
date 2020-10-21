package com.qiwenshare.file.dao;

import com.qiwenshare.file.dao.entity.UserRole;
import com.qiwenshare.file.dao.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dehui dou
 * @date 2020/10/21 11:44
 * @description
 */
@Component
public class UserRoleDao {
    @Autowired
    private UserRoleMapper userRoleMapper;

    public int save(UserRole userRole){
        return userRoleMapper.insert(userRole);
    }
}
