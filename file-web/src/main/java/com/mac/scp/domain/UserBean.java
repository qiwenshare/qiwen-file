package com.mac.scp.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.websocket.Session;

/**
 * 用户基础信息类
 *
 * @author ma116
 */
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(name = "openidIndex", columnNames = {"openid"})
})
@Entity
public class UserBean{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    private String openid; //qq登录使用

    private String username;

    private String realname;

    private String password;

    private String qqpassword;

    @Transient
    private String passwordAgain;

    private String telephone;

    private String email;

    private String sex;

    private String birthday;

    private String addrprovince;

    private String addrcity;

    private String addrarea;

    private String industry;

    private String position;

    private String intro;

    private String salt;//加密密码的盐
    //private byte state;//用户状态,0:创建未认证（比如没有激活，没有输入验证码等等）--等待验证的用户 , 1:正常状态,2：用户被锁定.

    private String imageurl;

    private String registertime;

    @ManyToMany(fetch = FetchType.EAGER)//立即从数据库中进行加载数据
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "userId")},
            inverseJoinColumns = {@JoinColumn(name = "roleid")})
    private List<Role> roleList;// 一个用户具有多个角色
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
    private String verificationcode;

    public int getNotReadCount() {
        return notReadCount;
    }

    public void setNotReadCount(int notReadCount) {
        this.notReadCount = notReadCount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddrcity() {
        return addrcity;
    }

    public void setAddrcity(String addrcity) {
        this.addrcity = addrcity;
    }

    public String getAddrarea() {
        return addrarea;
    }

    public void setAddrarea(String addrarea) {
        this.addrarea = addrarea;
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

    public String getAddrprovince() {
        return addrprovince;
    }

    public void setAddrprovince(String addrprovince) {
        this.addrprovince = addrprovince;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }


//    public byte getState() {
//        return state;
//    }
//
//    public void setState(byte state) {
//        this.state = state;
//    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public String getPasswordAgain() {
        return passwordAgain;
    }

    public void setPasswordAgain(String passwordAgain) {
        this.passwordAgain = passwordAgain;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getVerificationcode() {
        return verificationcode;
    }

    public void setVerificationcode(String verificationcode) {
        this.verificationcode = verificationcode;
    }

    /**
     * 密码盐.
     *
     * @return
     */
    public String getCredentialsSalt() {
        return this.username + this.salt;
    }
    //重新对盐重新进行了定义，用户名+salt，这样就更加不容易被破解


    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getRegistertime() {
        return registertime;
    }

    public void setRegistertime(String registertime) {
        this.registertime = registertime;
    }

    public String getQqpassword() {
        return qqpassword;
    }

    public void setQqpassword(String qqpassword) {
        this.qqpassword = qqpassword;
    }
}
