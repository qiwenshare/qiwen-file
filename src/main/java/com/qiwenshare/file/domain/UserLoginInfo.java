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
 * @date 2021/11/18 22:36
 */
@Data
@Table(name = "userlogininfo")
@Entity
@TableName("userlogininfo")
public class UserLoginInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long userLoginId;
    private String userloginDate;
    private Long userId;
}
