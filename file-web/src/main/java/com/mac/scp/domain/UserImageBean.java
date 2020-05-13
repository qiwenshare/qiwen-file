package com.mac.scp.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 用户头像实体类
 *
 * @author ma116
 */
@Data
@Accessors(chain = true)
@Table(name = "userimage")
@Entity
public class UserImageBean implements Serializable {
    /**
     * 序列id
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long imageid;
    @Column
    private long userid;
    @Column
    private String imageurl;
    @Column
    private String uploadtime;


}
