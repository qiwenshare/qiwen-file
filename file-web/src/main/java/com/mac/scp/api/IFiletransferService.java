package com.mac.scp.api;

import com.mac.scp.domain.FileBean;
import com.mac.scp.domain.StorageBean;

import javax.servlet.http.HttpServletRequest;

public interface IFiletransferService {


	/**
	 * 上传文件
	 *
	 * @param request  请求
	 * @param fileBean 文件信息
	 */
	void uploadFile(HttpServletRequest request, FileBean fileBean);

	StorageBean selectStorageBean(StorageBean storageBean);

	void insertStorageBean(StorageBean storageBean);

	void updateStorageBean(StorageBean storageBean);

	StorageBean selectStorageByUser(StorageBean storageBean);
}
