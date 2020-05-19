package com.mac.scp.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * 用户基础信息类
 *
 * @author ma116
 */
@Data
@Accessors(chain = true)
@Table(name = "user")
@Entity
@TableName("user")
public class UserBean {
	/**
	 * 用户id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@TableId(type = IdType.AUTO)
	private long userId;

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
	 * 重复密码
	 */
	@TableField(exist = false)
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
	 * 用户头像URL
	 */
	private String imageurl;

	/**
	 * 注册时间
	 */
	private String registertime;


}
