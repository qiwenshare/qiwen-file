package com.mac.scp.dto;

import com.mac.scp.constant.RegexConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 文件预备上传DTO
 *
 * @author WeiHongBin
 */
@Data
@Accessors(chain = true)
@Schema(name = "文件预备上传DTO")
public class FileSpeedDTO {

	@Schema(description = "文件大小")
	@NotNull
	private Integer size;

	@Schema(description = "文件md5")
	@NotBlank
	@Pattern(regexp = RegexConstant.MD5_REGEX)
	private String md5;

	@Schema(description = "文件名")
	@NotBlank
	@Pattern(regexp = RegexConstant.FILE_NAME_REGEX)
	private String fileName;

	@Schema(description = "父路径")
	@NotNull
	@Pattern(regexp = RegexConstant.PARENT_PATH_REGEX)
	private String parentPath;

	@Schema(description = "内容类型")
	@NotBlank
	private String contentType;
}
