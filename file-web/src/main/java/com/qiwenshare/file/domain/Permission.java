package com.qiwenshare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 权限实体类
 */
@Data
@Table(name = "permission")
@Entity
@TableName("permission")
public class Permission {
    /**
     * 权限id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long permissionId;//主键.
    /**
     * 权限名称
     */
    @Column
    private String name;//名称.

    /**
     * 资源类型
     */
    @Column
    private String resourceType;//资源类型，[menu|button]

    /**
     * 资源路径
     */
    @Column
    private String url;//资源路径.
    /**
     * 权限字符串,menu例子：role:*，button例子：role:create,role:update,role:delete,role:view
     */
    @Column
    private String permission;

    /**
     * 父编号
     */
    @Column
    private Long parentId;

    /**
     * 父编号列表
     */
    @Column
    private String parentIds;

    /**
     * 是否生效
     */
    @Column
    private Boolean available = Boolean.FALSE;

    /**
     * 角色列表
     */
    @Transient
    @TableField(exist = false)
    private List<Role> roles;

}