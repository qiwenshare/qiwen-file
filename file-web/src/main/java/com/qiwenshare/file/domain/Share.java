package com.qiwenshare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "share")
@Entity
@TableName("share")
public class Share {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long shareId;
    private Long userId;
    private String shareTime;
    private String endTime;
    private String extractionCode;
    private String shareBatchNum;
    private Integer shareType;//0公共，1私密，2好友
    private Integer shareStatus;//0正常，1已失效，2已撤销
}
