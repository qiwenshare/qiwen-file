package com.qiwenshare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "sysparam")
@Entity
@TableName("sysparam")
public class SysParam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition="bigint(20)")
    @TableId(type = IdType.AUTO)
    private Long sysParamId;
    @Column(columnDefinition="varchar(50) comment '系统参数key'")
    private String sysParamKey;
    @Column(columnDefinition="varchar(50) comment '系统参数值'")
    private String sysParamValue;
    @Column(columnDefinition="varchar(50) comment '系统参数描述'")
    private String sysParamDesc;
}
