package com.mac.scp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * 文件上传DTO
 *
 * @author WeiHongBin
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Schema(name = "文件预备上传DTO")
public class FileUploadDTO extends FileSpeedDTO {

	@Schema(description = "文件")
	@NotNull
	private MultipartFile file;
}
