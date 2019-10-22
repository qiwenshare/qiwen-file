package com.mac.scp.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Table(name = "permission")
@Entity
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long permissionid;//主键.
    @Column
    private String name;//名称.
    @Column
    private String resourceType;//资源类型，[menu|button]
    @Column
    private String url;//资源路径.
    @Column
    private String permission; //权限字符串,menu例子：role:*，button例子：role:create,role:update,role:delete,role:view
    @Column
    private Long parentId; //父编号
    @Column
    private String parentIds; //父编号列表
    @Column
    private Boolean available = Boolean.FALSE;

    @Transient
    private List<Role> roles;

    public long getPermissionid() {
        return permissionid;
    }

    public void setPermissionid(long permissionid) {
        this.permissionid = permissionid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}