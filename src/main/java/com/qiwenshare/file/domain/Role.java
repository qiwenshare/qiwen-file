package com.qiwenshare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
    private String roleName; // 角色标识程序中判断使用,如"admin",这个是唯一的:

    /**
     * 角色描述
     */
    private String description; // 角色描述,UI界面显示使用

    /**
     * 是否可用
     */
    private Integer available; // 是否可用,如果不可用将不会添加给用户

    private String createTime;

    private Long createUserId;

    private String modifyTime;

    private Long modifyUserId;

    /**
     * 权限列表
     */
    @ManyToMany(fetch = FetchType.EAGER)//立即从数据库中进行加载数据
    @JoinTable(name = "role_permission",
            joinColumns = {@JoinColumn(name = "roleid")},
            inverseJoinColumns = {@JoinColumn(name = "permissionid")})
    @TableField(exist = false)
    private List<Permission> permissions;

}