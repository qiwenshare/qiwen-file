package com.qiwenshare.file.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @desc
 * @author dehui dou
 * @time 2020-10-21
 */
@TableName("role_permission")
public class RolePermission {

    private Long roleId;

    private Long permissionId;

    public RolePermission() {}

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }
}
