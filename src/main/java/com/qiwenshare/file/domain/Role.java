package com.qiwenshare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 角色实体信息类
 */
@Data
@Table(name = "role")
@Entity
@TableName("role")
public class Role {
    /**
     * 角色id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
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

    /**
     * 权限列表
     */
    @ManyToMany(fetch = FetchType.EAGER)//立即从数据库中进行加载数据
    @JoinTable(name = "role_permission",
            joinColumns = {@JoinColumn(name = "roleId")},
            inverseJoinColumns = {@JoinColumn(name = "permissionId")})
    private List<Permission> permissions;

//    @ManyToMany
//    @JoinTable(name = "role_permission",
//            joinColumns = {@JoinColumn(name="roleId")},
//            inverseJoinColumns = {@JoinColumn(name="permissionId")})
//    private List<UserBean> userList;// 一个角色对应多个用户

}