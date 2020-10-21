package com.qiwenshare.file.domain;

// import com.baomidou.mybatisplus.annotation.IdType;
// import com.baomidou.mybatisplus.annotation.TableId;
// import com.baomidou.mybatisplus.annotation.TableName;

import java.util.List;

/**
 * 角色实体信息类
 */

public class Role {
    /**
     * 角色id
     */

    private Long roleId; // 编号

    /**
     * 角色名
     */
    private String role; // 角色标识程序中判断使用,如"admin",这个是唯一的:

    /**
     * 角色描述
     */
    private String description; // 角色描述,UI界面显示使用

    /**
     * 是否可用
     */
    private Boolean available = Boolean.FALSE; // 是否可用,如果不可用将不会添加给用户

    private List<Permission> permissions;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}