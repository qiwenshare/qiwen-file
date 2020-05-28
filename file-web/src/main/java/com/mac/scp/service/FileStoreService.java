package com.mac.scp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mac.scp.entity.FileStore;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 文件存储 服务类
 *
 * @author WeiHongBin
 */
public interface FileStoreService extends IService<FileStore> {

	/**
	 * 保存文件
	 *
	 * @param file     文件对象
	 * @param metadata 元数据
	 */
	void saveFile(MultipartFile file, Map<String, String> metadata);

	/**
	 * 下载文件
	 *
	 * @param fileStoreId 文件存储 ID
	 * @param response    Servlet 响应
	 */
	void downloadFile(Long fileStoreId, HttpServletResponse response);
}
