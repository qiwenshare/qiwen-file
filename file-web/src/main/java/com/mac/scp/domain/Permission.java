package com.mac.scp.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

/**
 * 权限实体类
 */
@Data
@Accessors(chain = true)
@Table(name = "permission")
@Entity
public class Permission {
    /**
     * 权限id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long permissionid;
    /**
     * 权限名称
     */
    @Column
    private String name;

    /**
     * 资源类型
     * //资源类型，[menu|button]
     */
    @Column
    private String resourceType;

    /**
     * 资源路径
     */
    @Column
    private String url;
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
    private List<Role> roles;
}