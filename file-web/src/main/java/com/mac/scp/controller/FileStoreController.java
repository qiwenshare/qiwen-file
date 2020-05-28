package com.mac.scp.controller;

import com.mac.common.annotations.PassToken;
import com.mac.scp.service.FileStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * 文件存储 前端控制器
 *
 * @author ma116
 */
@Tag(name = "文件存储 前端控制器")
@RestController
@RequestMapping("/file-store")
public class FileStoreController {

	@Autowired
	private FileStoreService fileStoreService;

	@Operation(summary = "测试文件上传")
	@PassToken
	@PostMapping
	public void test(MultipartFile file) {
		fileStoreService.saveFile(file, new HashMap<>(0));
	}

	@Operation(summary = "测试文件下载")
	@PassToken
	@GetMapping("{id}")
	public void down(@PathVariable long id, HttpServletResponse response) {
		fileStoreService.downloadFile(id, response);
	}
}
