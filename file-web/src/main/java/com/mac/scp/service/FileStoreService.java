package com.mac.scp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mac.scp.entity.File;
import com.mac.scp.entity.FileStore;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 文件存储 服务类
 *
 * @author WeiHongBin
 */
public interface FileStoreService extends IService<FileStore> {

	/**
	 * 保存文件
	 *
	 * @param file 文件对象
	 */
	void saveFile(MultipartFile file);

	/**
	 * 下载文件
	 *
	 * @param file     文件对象
	 * @param response Servlet 响应
	 */
	void downloadFile(File file, HttpServletResponse response);
}
