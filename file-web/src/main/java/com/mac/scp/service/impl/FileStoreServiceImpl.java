package com.mac.scp.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mac.common.exception.UnifiedException;
import com.mac.scp.entity.FileStore;
import com.mac.scp.mapper.FileStoreMapper;
import com.mac.scp.service.FileStoreService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
 * 文件存储 服务实现类
 *
 * @author WeiHongBin
 */
@Service
public class FileStoreServiceImpl extends ServiceImpl<FileStoreMapper, FileStore> implements FileStoreService {

	@Value("${file.store.path:D:\\file\\}")
	private String basePath;

	@Override
	public void saveFile(MultipartFile file) {
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
			String suffix = name.substring(name.lastIndexOf(".") + 1);
			String time = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
			String fileKey = time + "\\" + IdWorker.getId() + "." + suffix;
			File file1 = FileUtil.touch(basePath + fileKey);
			file.transferTo(file1);
			boolean insert = new FileStore()
					.setSize(size)
					.setName(fileKey)
					.setMd5(md5Hex).insert();
			if (!insert) {
				throw new UnifiedException("文件保存失败");
			}
		} catch (IOException e) {
			throw new UnifiedException("文件保存失败", e);
		} finally {
			reentrantLock.unlock();
		}
	}

	@SneakyThrows
	@Override
	public void downloadFile(com.mac.scp.entity.File file, HttpServletResponse response) {
		try (ServletOutputStream outputStream = response.getOutputStream()) {
			FileStore fileStore = new FileStore().selectById(file.getFileStoreId());
			response.setContentType(file.getContentType());
			response.setHeader("Content-Disposition", "fileName=" + fileStore.getName());
			response.setContentLengthLong(fileStore.getSize());
			response.setHeader("ETag", fileStore.getMd5());
			String metadata = file.getMetadata();
			Optional.ofNullable(metadata).ifPresent(m -> {
				Map<String, String> map = JSONUtil.parse(m).toBean(new TypeReference<Map<String, String>>() {
				});
				map.forEach(response::setHeader);
			});
			FileUtil.writeToStream(basePath + fileStore.getName(), outputStream);
		}
	}
}
