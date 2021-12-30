package com.qiwenshare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Data
@Table(name = "share")
@Entity
@TableName("share")
public class Share {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long shareId;
    @Column(columnDefinition="bigint(20) comment '用户id'")
    private Long userId;
    @Column(columnDefinition="varchar(30) comment '分享时间'")
    private String shareTime;
    @Column(columnDefinition="varchar(30) comment '失效时间'")
    private String endTime;
    @Column(columnDefinition="varchar(10) comment '提取码'")
    private String extractionCode;
    @Column(columnDefinition="varchar(40) comment '分享批次号'")
    private String shareBatchNum;
    @Column(columnDefinition="int(2) comment '分享类型(0公共,1私密,2好友)'")
    private Integer shareType;
    @Column(columnDefinition="int(2) comment '分享状态(0正常,1已失效,2已撤销)'")
    private Integer shareStatus;

}
