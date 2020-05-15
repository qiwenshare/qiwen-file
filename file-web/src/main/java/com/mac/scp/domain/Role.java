package com.mac.scp.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

/**
 * 角色实体信息类
 */
@Data
@Accessors(chain = true)
@Table(name = "role")
@Entity
public class Role {
	/**
	 * 角色id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long roleid;

	/**
	 * 角色名
	 * // 角色标识程序中判断使用,如"admin",这个是唯一的:
	 */
	private String role;

	/**
	 * 角色描述
	 * // 角色描述,UI界面显示使用
	 */
	private String description;

	/**
	 * 是否可用
	 * // 是否可用,如果不可用将不会添加给用户
	 */
	private Boolean available = Boolean.FALSE;

	/**
	 * 权限列表
	 * //立即从数据库中进行加载数据
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "role_permission",
			joinColumns = {@JoinColumn(name = "roleid")},
			inverseJoinColumns = {@JoinColumn(name = "permissionid")})
	private List<Permission> permissions;

//    @ManyToMany
//    @JoinTable(name = "role_permission",
//            joinColumns = {@JoinColumn(name="roleid")},
//            inverseJoinColumns = {@JoinColumn(name="permissionid")})
//    private List<UserBean> userList;// 一个角色对应多个用户

}