package com.mac.scp.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户基础信息类
 *
 * @author ma116
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Table(name = "user")
@Entity
@TableName("user")
public class User extends Model<User> {


	/**
	 * 用户id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 用户名称 可以作为登录
	 */
	private String username;

	/**
	 * 昵称 用户显示
	 */
	private String nickname;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 手机号码
	 */
	private String phone;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 性别
	 */
	private String sex;

	/**
	 * 生日
	 */
	private LocalDate birthday;

	/**
	 * 用户头像URL
	 */
	private String avatarUrl;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	/**
	 * 修改时间
	 */
	@TableField(fill = FieldFill.UPDATE)
	private LocalDateTime modifyTime;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}