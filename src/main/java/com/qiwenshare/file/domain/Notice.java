package com.qiwenshare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * @author MAC
 * @version 1.0
 * @description: 公告
 * @date 2021/11/22 22:16
 */
@Data
@Table(name = "notice")
@Entity
@TableName("notice")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition="bigint(20)", unique = true)
    private Long noticeId;

    @Column(columnDefinition="varchar(100) comment '标题'", nullable = false)
    private String title;
    @Column(columnDefinition="int(2) comment '平台(1-社区,2-管理端,3-网盘,4-股票)'")
    private Integer platform;

    @Column(columnDefinition = "longtext comment 'markdown原文'")
    private String markdownContent;
    @Column(columnDefinition = "longtext comment 'html内容'")
    private String content;
    @Column(columnDefinition="varchar(25) comment '有效时间'")
    private String validDateTime;
    @Column(columnDefinition="int(1) comment '是否长期有效(0-否,1-是)'")
    private int isLongValidData;

    @Column(columnDefinition="varchar(25) comment '创建时间'")
    private String createTime;
    @Column(columnDefinition="bigint(20) comment '创建用户id'")
    private Long createUserId;
    @Column(columnDefinition="varchar(25) comment '修改时间'")
    private String modifyTime;
    @Column(columnDefinition="bigint(20) comment '修改用户id'")
    private Long modifyUserId;
}
