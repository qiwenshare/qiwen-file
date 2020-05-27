package com.mac.scp.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.google.common.collect.Maps;
import com.mac.common.annotations.PassToken;
import com.mac.common.exception.UnifiedException;
import com.mac.scp.entity.FileStore;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 文件存储 前端控制器
 *
 * @author ma116
 */
@Tag(name = "文件存储 前端控制器")
@RestController
@RequestMapping("/file-store")
public class FileStoreController {

	@PassToken
	@PostMapping
	public void test(MultipartFile file) throws IOException {
		ReentrantLock reentrantLock = new ReentrantLock();
		reentrantLock.lock();
		try {
			String md5Hex = DigestUtil.md5Hex(file.getInputStream());
			long size = file.getSize();
			Integer selectCount = new FileStore().selectCount(new LambdaQueryWrapper<FileStore>()
					.eq(FileStore::getMd5, md5Hex)
					.eq(FileStore::getSize, size)
			);
			if (0 < selectCount) {
				throw new UnifiedException("文件已存在");
			}
			String name = file.getOriginalFilename();
			String contentType = file.getContentType();
			String suffix = name.substring(name.lastIndexOf(".") + 1);
			String time = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
			String fileKey = time + "\\" + IdWorker.getId() + "." + suffix;
			String pathname = "D:\\abc\\" + fileKey;
			File file1 = FileUtil.touch(pathname);
			file.transferTo(file1);
			Console.log("name:[{}],contentType:[{}],size:[{}],suffix:[{}]", name, contentType, size, suffix);
			Map<String, String> metadataMap = Maps.newHashMap();
			metadataMap.put("name_size", fileKey + size);
			metadataMap.put("ct_md5", contentType + md5Hex);
			String metadata = JSONUtil.parse(metadataMap).toString();
			boolean insert = new FileStore()
					.setMetadata(metadata)
					.setContentType(contentType)
					.setSize(size)
					.setName(fileKey)
					.setMd5(md5Hex).insert();
			if (!insert) {
				throw new UnifiedException("文件上传失败");
			}
		} finally {
			reentrantLock.unlock();
		}
	}

	@SneakyThrows
	@PassToken
	@GetMapping("{id}")
	public void down(@PathVariable long id, HttpServletResponse response) {
		try (ServletOutputStream outputStream = response.getOutputStream()) {
			FileStore fileStore = new FileStore().selectById(id);
			String name = fileStore.getName();
			response.setContentType(fileStore.getContentType());
			response.setHeader("Content-Disposition", "fileName=" + fileStore.getName());
			response.setContentLengthLong(fileStore.getSize());
			response.setHeader("ETag", fileStore.getMd5());
			String metadata = fileStore.getMetadata();
			Optional.ofNullable(metadata).ifPresent(m -> {
				Map<String, String> map = JSONUtil.parse(m).toBean(new TypeReference<Map<String, String>>() {
				});
				map.forEach(response::setHeader);
			});
			System.out.println(response.getHeaderNames());
			FileUtil.writeToStream("D:\\abc\\" + name, outputStream);
		}
	}
}
