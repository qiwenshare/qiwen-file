package com.qiwenshare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "userfile", uniqueConstraints = {
        @UniqueConstraint(name = "fileindex", columnNames = {"fileName", "filePath", "extendName", "deleteFlag", "userId"})})
@Entity
@TableName("userfile")
public class UserFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition = "bigint(20)")
    private Long userFileId;

    @Column(columnDefinition = "bigint(20)")
    private Long userId;

    @Column(columnDefinition="bigint(20)")
    private Long fileId;

    @Column(columnDefinition="varchar(100)")
    private String fileName;

    @Column(columnDefinition="varchar(500)")
    private String filePath;

    @Column(columnDefinition="varchar(100)")
    private String extendName;

    @Column(columnDefinition="int(1)")
    private Integer isDir;

    @Column(columnDefinition="varchar(25)")
    private String uploadTime;

    @Column(columnDefinition="int(11)")
    private Integer deleteFlag;

    @Column(columnDefinition="varchar(25)")
    private String deleteTime;

    @Column(columnDefinition = "varchar(50)")
    private String deleteBatchNum;

}
