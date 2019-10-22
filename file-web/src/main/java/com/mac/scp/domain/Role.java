package com.mac.scp.domain;

import javax.persistence.*;
import java.util.List;

@Table(name = "role")
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long roleid; // 编号

    private String role; // 角色标识程序中判断使用,如"admin",这个是唯一的:

    private String description; // 角色描述,UI界面显示使用

    private Boolean available = Boolean.FALSE; // 是否可用,如果不可用将不会添加给用户

    @ManyToMany(fetch = FetchType.EAGER)//立即从数据库中进行加载数据
    @JoinTable(name = "role_permission",
            joinColumns = {@JoinColumn(name = "roleid")},
            inverseJoinColumns = {@JoinColumn(name = "permissionid")})
    private List<Permission> permissions;

//    @ManyToMany
//    @JoinTable(name = "role_permission",
//            joinColumns = {@JoinColumn(name="roleid")},
//            inverseJoinColumns = {@JoinColumn(name="permissionid")})
//    private List<UserBean> userList;// 一个角色对应多个用户


    public long getRoleid() {
        return roleid;
    }

    public void setRoleid(long roleid) {
        this.roleid = roleid;
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

//    public List<UserBean> getUserList() {
//        return userList;
//    }
//
//    public void setUserList(List<UserBean> userList) {
//        this.userList = userList;
//    }
}