package com.mac.scp.dto;

import com.mac.common.dto.PageDtoBase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 文件列表DTO
 *
 * @author WeiHongBin
 */
@Data
@Accessors(chain = true)
@Schema(name = "文件列表DTO")
public class FileListDTO extends PageDtoBase {


	@Schema(description = "文件名")
	private String fileName;

	@Schema(description = "父路径")
	private String parentPath;

	@Schema(description = "是否目录")
	private Boolean dir;

	@Schema(description = "类别 0 未知类型; 1 图片; 2 视频; 3 音乐")
	private Integer category;
}
