package com.mac.scp.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * 文件实体类
 *
 * @author ma116
 */
@Data
@Accessors(chain = true)
@Table(name = "file",
		uniqueConstraints = {
				@UniqueConstraint(name = "fileindex", columnNames = {"filename", "filepath", "extendname"})})
@Entity
@TableName("file")
public class FileBean {


	/**
	 * 文件id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@TableId(type = IdType.AUTO)
	private long fileid;

	/**
	 * 用户id
	 */
	private long userid;

	/**
	 * 文件URL
	 */
	private String fileurl;

	/**
	 * 文件路径
	 */
	private String filepath;

	/**
	 * 上传时间
	 */
	private String uploadtime;

	/**
	 * 时间戳名称
	 */
	private String timestampname;

	/**
	 * 扩展名
	 */
	private String extendname;

	/**
	 * 文件名
	 */
	private String filename;

	/**
	 * 文件大小
	 */
	private long filesize;

	/**
	 * 是否是目录
	 */
	private Boolean isdir;

	@TableField(exist = false)
	@Transient
	private String oldfilepath;
	@TableField(exist = false)
	@Transient
	private String newfilepath;
	@TableField(exist = false)
	@Transient
	private String files;
	@TableField(exist = false)
	@Transient
	private int filetype;
}
