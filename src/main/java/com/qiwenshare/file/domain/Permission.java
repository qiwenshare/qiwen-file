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
    private Long permissionId;//主键

    /**
     * 父编号
     */
    @Column
    private Long parentId;

    /**
     * 权限名称
     */
    @Column
    private String permissionName;//名称.

    /**
     * 资源类型
     */
    @Column
    private Integer resourceType;//资源类型

    /**
     * 权限字符串
     */
    @Column
    private String permissionCode;

    @Column
    private Integer orderNum;


    private String createTime;

    private Long createUserId;

    private String modifyTime;

    private Long modifyUserId;


}