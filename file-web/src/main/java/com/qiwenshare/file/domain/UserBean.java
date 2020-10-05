package com.qiwenshare.file.domain;

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
    /**
     * 用户id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    /**
     * openId qq登录使用
     */
    private String openId;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 真实名
     */
    private String realname;

    /**
     * 密码
     */
    private String password;

    /**
     * qq密码
     */
    private String qqPassword;

    /**
     * 重复密码
     */
    @Transient
    private String passwordAgain;

    /**
     * 手机号码
     */
    private String telephone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 年龄
     */
    private String sex;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 省
     */
    private String addrProvince;

    /**
     * 市
     */
    private String addrCity;

    /**
     * 区
     */
    private String addrArea;

    /**
     * 行业
     */
    private String industry;

    /**
     * 职位
     */
    private String position;

    /**
     * 介绍
     */
    private String intro;

    /**
     * 盐值
     */
    private String salt;//加密密码的盐
    //private byte state;//用户状态,0:创建未认证（比如没有激活，没有输入验证码等等）--等待验证的用户 , 1:正常状态,2：用户被锁定.

    /**
     * 用户头像URL
     */
    private String imageUrl;

    /**
     * 注册时间
     */
    private String registerTime;


    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @Transient
    private Session session;

    /**
     * 消息未读数
     */
    @Transient
    private Integer notReadCount;

    /**
     * 验证码
     */
    @Transient
    private String verificationCode;

    @Transient
    private String token;

    /**
     * 角色列表
     */
    @ManyToMany(fetch = FetchType.EAGER)//立即从数据库中进行加载数据
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "userId")},
            inverseJoinColumns = {@JoinColumn(name = "roleid")})
    private List<Role> roleList;// 一个用户具有多个角色

}
