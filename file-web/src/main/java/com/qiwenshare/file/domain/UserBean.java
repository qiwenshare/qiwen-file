package com.qiwenshare.file.domain;

import java.util.List;

import javax.websocket.Session;

/**
 * 用户基础信息类
 *
 * @author ma116
 */

public class UserBean {
    /**
     * 用户id
     */

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
    private String salt;// 加密密码的盐

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
    private Session session;

    /**
     * 消息未读数
     */
    private Integer notReadCount;

    /**
     * 验证码
     */
    private String verificationCode;

    private String token;
    private String downloadDomain;
    private String viewDomain;

    /**
     * 角色列表
     */
    private List<Role> roleList;// 一个用户具有多个角色

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
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

    public String getQqPassword() {
        return qqPassword;
    }

    public void setQqPassword(String qqPassword) {
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

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Integer getNotReadCount() {
        return notReadCount;
    }

    public void setNotReadCount(Integer notReadCount) {
        this.notReadCount = notReadCount;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDownloadDomain() {
        return downloadDomain;
    }

    public void setDownloadDomain(String downloadDomain) {
        this.downloadDomain = downloadDomain;
    }

    public String getViewDomain() {
        return viewDomain;
    }

    public void setViewDomain(String viewDomain) {
        this.viewDomain = viewDomain;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }
}
