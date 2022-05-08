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
 * @date 2022/4/27 23:44
 */
@Data
@Table(name = "music")
@Entity
@TableName("music")
public class Music {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition="bigint(20)")
    private String musicId;
    @Column(columnDefinition = "bigint(20) comment '文件id'")
    private String fileId;
    private String track;
    @Column
    private String artist;
    @Column
    private String title;
    @Column
    private String album;
    @Column
    private String year;
    @Column
    private String genre;
    @Column
    private String comment;
    @Column(columnDefinition="varchar(10000) comment '歌词'")
    private String lyrics;
    @Column
    private String composer;
    @Column
    private String publicer;
    @Column
    private String originalArtist;
    @Column
    private String albumArtist;
    @Column
    private String copyright;
    @Column
    private String url;
    @Column
    private String encoder;
    @Column(columnDefinition = "mediumblob")
    private String albumImage;

    @Column
    private Float trackLength;
}
