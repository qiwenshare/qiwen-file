package com.qiwenshare.file.domain.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * 用户基础信息类
 *
 * @author ma116
 */
@Data
@Table(name = "user")
@Entity
@TableName("user")
public class UserBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint(20)")
    @TableId(type = IdType.AUTO)
    private Long userId;

    @Column(columnDefinition = "varchar(30) comment '用户名'")
    private String username;

    @Column(columnDefinition = "varchar(35) comment '密码'")
    private String password;

    @Column(columnDefinition = "varchar(15) comment '手机号'")
    private String telephone;

    @Column(columnDefinition = "varchar(100) comment '邮箱'")
    private String email;

    @Column(columnDefinition = "varchar(3) comment '性别'")
    private String sex;

    @Column(columnDefinition = "varchar(30) comment '生日'")
    private String birthday;

    @Column(columnDefinition = "varchar(10) comment '省'")
    private String addrProvince;

    @Column(columnDefinition = "varchar(10) comment '市'")
    private String addrCity;

    @Column(columnDefinition = "varchar(10) comment '区'")
    private String addrArea;

    @Column(columnDefinition = "varchar(50) comment '行业'")
    private String industry;

    @Column(columnDefinition = "varchar(50) comment '地区'")
    private String position;

    @Column(columnDefinition = "varchar(5000) comment '个人简介'")
    private String intro;

    @Column(columnDefinition = "varchar(20) comment '盐'")
    private String salt;

    @Column(columnDefinition = "varchar(100) comment '头像'")
    private String imageUrl;

    @Column(columnDefinition = "varchar(30) comment '注册时间'")
    private String registerTime;

    @Column(columnDefinition = "varchar(30) comment '最后登录时间'")
    private String lastLoginTime;

    @Column(columnDefinition = "int(2) comment '是否可用(0-不可用,1-可用)'")
    private Integer available;
    @Column(columnDefinition = "varchar(30) comment '修改时间'")
    private String modifyTime;
    @Column(columnDefinition = "bigint(20) comment '修改用户id'")
    private Long modifyUserId;


}
