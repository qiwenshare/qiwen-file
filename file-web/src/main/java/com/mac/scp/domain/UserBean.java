package com.mac.scp.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.websocket.Session;
import java.util.List;

/**
 * 用户基础信息类
 *
 * @author ma116
 */
@Data
@Accessors(chain = true)
@Table(name = "user", uniqueConstraints = {
		@UniqueConstraint(name = "openidIndex", columnNames = {"openid"})
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
	 * openid qq登录使用
	 */
	private String openid;

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
	private String qqpassword;

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
	private String addrprovince;

	/**
	 * 市
	 */
	private String addrcity;

	/**
	 * 区
	 */
	private String addrarea;

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
	private String salt;
	//private byte state;//用户状态,0:创建未认证（比如没有激活，没有输入验证码等等）--等待验证的用户 , 1:正常状态,2：用户被锁定.

	/**
	 * 用户头像URL
	 */
	private String imageurl;

	/**
	 * 注册时间
	 */
	private String registertime;

	/**
	 * 角色列表
	 *
	 * @ManyToMany(fetch = FetchType.EAGER) //立即从数据库中进行加载数据
	 * // 一个用户具有多个角色
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_role",
			joinColumns = {@JoinColumn(name = "userId")},
			inverseJoinColumns = {@JoinColumn(name = "roleid")})
	private List<Role> roleList;
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
	 * 文章数量
	 */
	@Transient
	private int essaycount;
	/**
	 * 验证码
	 */
	@Transient
	private String verificationcode;


	/**
	 * 密码盐.
	 *
	 * @return
	 */
	public String getCredentialsSalt() {
		return this.username + this.salt;
	}
	//重新对盐重新进行了定义，用户名+salt，这样就更加不容易被破解


}
