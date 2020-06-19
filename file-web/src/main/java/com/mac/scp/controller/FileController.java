package com.mac.scp.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mac.common.annotations.PassToken;
import com.mac.common.exception.UnifiedException;
import com.mac.scp.dto.FileListDTO;
import com.mac.scp.dto.FileSpeedDTO;
import com.mac.scp.dto.FileUploadDTO;
import com.mac.scp.entity.File;
import com.mac.scp.entity.FileStore;
import com.mac.scp.service.FileService;
import com.mac.scp.service.FileStoreService;
import com.mac.scp.session.SessionFactory;
import com.mac.scp.util.CategoryUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 文件 前端控制器
 *
 * @author WeiHongBin
 */
@Tag(name = "文件 前端控制器")
@RestController
@RequestMapping("/file")
public class FileController {

	@Autowired
	private FileService fileService;
	@Autowired
	private FileStoreService fileStoreService;

	@Operation(summary = "查询文件")
	@GetMapping
	public void list(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token, @Validated FileListDTO dto) {

	}

	@Operation(summary = "极速上传")
	@PostMapping(value = "speed", consumes = "application/x-www-form-urlencoded")
	public void speed(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token, @Validated FileSpeedDTO dto) {
		FileStore fileStore = new FileStore().selectOne(new LambdaQueryWrapper<FileStore>()
				.eq(FileStore::getMd5, dto.getMd5())
				.eq(FileStore::getSize, dto.getSize()));
		if (Objects.isNull(fileStore)) {
			throw new UnifiedException(HttpStatus.FORBIDDEN, "非极速上传,请调用上传接口");
		}
		Long userId = SessionFactory.getSession().get(token);
		int count = fileService.count(new LambdaQueryWrapper<File>().eq(File::getUserId, userId)
				.eq(File::getParentPath, dto.getParentPath())
				.eq(File::getFileName, dto.getFileName())
		);
		if (count != 0) {
			throw new UnifiedException(HttpStatus.FORBIDDEN, "文件已存在");
		}
		File file = new File();
		BeanUtil.copyProperties(dto, file);
		file.setFileStoreId(fileStore.getId())
				.setUserId(userId)
				.setCategory(CategoryUtil.getFileCategory(dto.getFileName()))
				.insert();

	}

	@PassToken
	@Operation(summary = "正常上传")
	@PostMapping(value = "upload", consumes = "multipart/form-data")
	public void upload(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token, @Validated FileUploadDTO dto) {
		fileStoreService.saveFile(dto.getFile());
		speed(token, dto);
	}


}