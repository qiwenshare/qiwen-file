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
 * @date 2021/12/23 19:48
 */
@Data
@Table(name = "fileclassification")
@Entity
@TableName("fileclassification")
public class FileClassification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition="bigint(20)")
    private Long fileClassificationId;
    @Column(columnDefinition="bigint(20) comment '文件类型id'")
    private Integer fileTypeId;
    @Column(columnDefinition="varchar(25) comment '文件扩展名'")
    private String fileExtendName;
}
