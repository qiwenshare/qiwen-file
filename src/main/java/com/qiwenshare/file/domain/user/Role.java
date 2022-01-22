package com.qiwenshare.file.domain.user;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long roleId; // 编号

    @Column(columnDefinition="varchar(20) comment '角色名'")
    private String roleName;

    @Column(columnDefinition="varchar(100) comment '角色描述'")
    private String description;

    @Column(columnDefinition="int(2) comment '是否可用(0-不可用,1-可用)'")
    private Integer available; // 是否可用,如果不可用将不会添加给用户

    @Column(columnDefinition="varchar(30) comment '创建时间'")
    private String createTime;
    @Column(columnDefinition="bigint(20) comment '创建用户id'")
    private Long createUserId;
    @Column(columnDefinition="varchar(30) comment '修改时间'")
    private String modifyTime;
    @Column(columnDefinition="bigint(20) comment '修改用户id'")
    private Long modifyUserId;

//    /**
//     * 权限列表
//     */
//    @ManyToMany(fetch = FetchType.EAGER)//立即从数据库中进行加载数据
//    @JoinTable(name = "role_permission",
//            joinColumns = {@JoinColumn(name = "roleid")},
//            inverseJoinColumns = {@JoinColumn(name = "permissionid")})
//    @TableField(exist = false)
//    private List<Permission> permissions;

}