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
 * @date 2021/12/23 22:11
 */
@Data
@Table(name = "filetype")
@Entity
@TableName("filetype")
public class FileType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @TableId(type = IdType.AUTO)
    private Integer fileTypeId;
    @Column(columnDefinition="varchar(50) comment '文件类型名'")
    private String fileTypeName;
    @Column(columnDefinition="int(2) comment '次序'")
    private Integer orderNum;
}
