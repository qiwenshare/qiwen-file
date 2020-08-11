package com.qiwenshare.file.domain;

import javax.persistence.*;
import javax.websocket.Session;
import java.util.List;

/**
 * 用户基础信息类
 *
 * @author ma116
 */
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(name = "openIdIndex", columnNames = {"openId"})
})
@Entity
public class UserBean {
    /**
     * 用户id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

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
    private int notReadCount;

    /**
     * 验证码
     */
    @Transient
    private String verificationCode;

    /**
     * 角色列表
     */
    @ManyToMany(fetch = FetchType.EAGER)//立即从数据库中进行加载数据
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "userId")},
            inverseJoinColumns = {@JoinColumn(name = "roleid")})
    private List<Role> roleList;// 一个用户具有多个角色

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getQqPassword() {
        return qqPassword;
    }

    public void setQqPassword(String qqPassword) {
        this.qqPassword = qqPassword;
    }

    public String getAddrProvince() {
        return addrProvince;
    }

    public void setAddrProvince(String addrProvince) {
        this.addrProvince = addrProvince;
    }

    public String getAddrCity() {
        return addrCity;
    }

    public void setAddrCity(String addrCity) {
        this.addrCity = addrCity;
    }

    public String getAddrArea() {
        return addrArea;
    }

    public void setAddrArea(String addrArea) {
        this.addrArea = addrArea;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getOpenid() {
        return openId;
    }

    public void setOpenid(String openId) {
        this.openId = openId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQqpassword() {
        return qqPassword;
    }

    public void setQqpassword(String qqPassword) {
        this.qqPassword = qqPassword;
    }

    public String getPasswordAgain() {
        return passwordAgain;
    }

    public void setPasswordAgain(String passwordAgain) {
        this.passwordAgain = passwordAgain;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddrprovince() {
        return addrProvince;
    }

    public void setAddrprovince(String addrProvince) {
        this.addrProvince = addrProvince;
    }

    public String getAddrcity() {
        return addrCity;
    }

    public void setAddrcity(String addrCity) {
        this.addrCity = addrCity;
    }

    public String getAddrarea() {
        return addrArea;
    }

    public void setAddrarea(String addrArea) {
        this.addrArea = addrArea;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getImageurl() {
        return imageUrl;
    }

    public void setImageurl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRegistertime() {
        return registerTime;
    }

    public void setRegistertime(String registerTime) {
        this.registerTime = registerTime;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public int getNotReadCount() {
        return notReadCount;
    }

    public void setNotReadCount(int notReadCount) {
        this.notReadCount = notReadCount;
    }

    public String getVerificationcode() {
        return verificationCode;
    }

    public void setVerificationcode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
