package com.qiwenshare.file.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @desc
 * @author dehui dou
 * @time 2020-10-21
 */
@TableName("user_role")
public class UserRole {

    private Long userId;

    private Long roleid;

    public UserRole() {}

    public UserRole(Long userId, Long roleid) {
        this.userId = userId;
        this.roleid = roleid;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleid() {
        return roleid;
    }

    public void setRoleid(Long roleid) {
        this.roleid = roleid;
    }
}
