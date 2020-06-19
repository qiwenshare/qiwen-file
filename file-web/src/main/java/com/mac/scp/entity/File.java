package com.mac.scp.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件
 *
 * @author ma116
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Table(name = "file", uniqueConstraints = {
		@UniqueConstraint(name = "uk_file_name_parent_path", columnNames = {"fileName", "parentPath"})
})
@Entity
@TableName("file")
public class File extends Model<File> {
	/**
	 * id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 文件存储ID
	 */
	private Long fileStoreId;

	/**
	 * 关联用户
	 */
	private Long userId;

	/**
	 * 内容类型
	 */
	private String contentType;

	/**
	 * 文件名
	 * <p>
	 * 例如: 测试.png
	 */
	private String fileName;

	/**
	 * 父路径
	 * <p>
	 * '/' 开头和结尾
	 * <br/>
	 * 例如
	 * <br/>
	 * abc/
	 * <br/>
	 * /
	 * <br/>
	 * def/ad/asdf/
	 */
	private String parentPath;
	/**
	 * 目录
	 */
	@Column(columnDefinition = "boolean default false")
	private Boolean dir;

	/**
	 * 类别
	 * <br/>
	 * 0 未知类型; 1 图片; 2 视频; 3 音乐
	 */
	@Column(columnDefinition = "int default 0")
	private Integer category;

	/**
	 * 删除标识
	 */
	@Column(columnDefinition = "boolean default false")
	private Boolean delete;

	/**
	 * 删除时间
	 */
	@TableField(fill = FieldFill.UPDATE)
	private LocalDateTime deleteTime;

	/**
	 * 存储JSON格式
	 * 元数据
	 * 在下载的时候写入 response 的 Header中
	 * H2 中定义数据类似 json 格式会转义处理，所以这个存储 text 格式
	 */
	@Column(columnDefinition = "text")
	private String metadata;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	/**
	 * 修改时间
	 */
	@TableField(fill = FieldFill.UPDATE)
	private LocalDateTime modifyTime;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}

