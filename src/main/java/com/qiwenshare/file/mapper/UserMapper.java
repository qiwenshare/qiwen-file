package com.qiwenshare.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.qiwenshare.file.domain.Role;
import com.qiwenshare.file.domain.UserBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends BaseMapper<UserBean> {
    int insertUser(UserBean userBean);

    int insertUserRole(long userId, long roleId);

    List<Role>  selectRoleListByUserId(@Param("userId") long userId);

    String selectSaltByTelephone(@Param("telephone") String telephone);

    UserBean selectUserByTelephoneAndPassword(@Param("telephone") String telephone, @Param("password") String password);

}
