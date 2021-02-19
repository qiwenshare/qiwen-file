package com.qiwenshare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * 操作日志基础信息类
 *
 * @author ma116
 */
@Data
@Table(name = "operationlog")
@Entity
@TableName("operationlog")
public class OperationLogBean {
    /**
     * 操作日志id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long operationLogId;

    /**
     * 用户id
     */
    private long userId;

    /**
     * 操作
     */
    private String operation;

    /**
     * 操作对象
     */
    private String operationObj;

    /**
     * 终端IP
     */
    private String terminal;

    /**
     * 操作结果
     */
    private String result;

    /**
     * 操作详情
     */
    private String detail;

    /**
     * 操作源
     */
    private String source;

    /**
     * 时间
     */
    private String time;

    /**
     * 日志级别
     */
    private String logLevel;

}
