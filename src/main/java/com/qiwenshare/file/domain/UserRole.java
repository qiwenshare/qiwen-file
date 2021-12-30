package com.qiwenshare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * @author MAC
 * @version 1.0
 * @description: TODO
 * @date 2021/12/30 16:14
 */
@Data
@Table(name = "user_role")
@Entity
@TableName("user_role")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long id;
    @Column(columnDefinition="bigint(20) comment '用户id'")
    private Long userId;

    @Column(columnDefinition="bigint(20) comment '角色id'")
    private Long roleId;
}
