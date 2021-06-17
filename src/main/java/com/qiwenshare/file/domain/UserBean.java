package com.qiwenshare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;
import javax.websocket.Session;
import java.util.List;

/**
 * 用户基础信息类
 *
 * @author ma116
 */
@Data
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(name = "openIdIndex", columnNames = {"openId"})
})
@Entity
@TableName("user")
public class UserBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint(20)")
    @TableId(type = IdType.AUTO)
    private Long userId;

    @Column(columnDefinition = "varchar(30)")
    private String openId;

    @Column(columnDefinition = "varchar(30)")
    private String username;

    @Column(columnDefinition = "varchar(30)")
    private String realname;

    @Column(columnDefinition = "varchar(35)")
    private String password;

    @Column(columnDefinition = "varchar(35)")
    private String qqPassword;

    @Column(columnDefinition = "varchar(15)")
    private String telephone;

    @Column(columnDefinition = "varchar(100)")
    private String email;

    @Column(columnDefinition = "varchar(3)")
    private String sex;


    @Column(columnDefinition = "varchar(30)")
    private String birthday;

    @Column(columnDefinition = "varchar(10)")
    private String addrProvince;

    @Column(columnDefinition = "varchar(10)")
    private String addrCity;

    @Column(columnDefinition = "varchar(10)")
    private String addrArea;

    @Column(columnDefinition = "varchar(50)")
    private String industry;

    @Column(columnDefinition = "varchar(50)")
    private String position;

    @Column(columnDefinition = "varchar(5000)")
    private String intro;

    @Column(columnDefinition = "varchar(20)")
    private String salt;//加密密码的盐
    //private byte state;//用户状态,0:创建未认证（比如没有激活，没有输入验证码等等）--等待验证的用户 , 1:正常状态,2：用户被锁定.

    @Column(columnDefinition = "varchar(100)")
    private String imageUrl;

    @Column(columnDefinition = "varchar(30)")
    private String registerTime;

    /**
     * 验证码
     */
    @Transient
    @TableField(exist = false)
    private String verificationCode;

    @Transient
    @TableField(exist = false)
    private String token;

    /**
     * 角色列表
     */
    @ManyToMany(fetch = FetchType.EAGER)//立即从数据库中进行加载数据
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "userId")},
            inverseJoinColumns = {@JoinColumn(name = "roleid")})
    @TableField(exist = false)
    private List<Role> roleList;// 一个用户具有多个角色

}
