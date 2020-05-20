package com.mac.scp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mac.common.cbb.DateUtil;
import com.mac.common.cbb.Uploader;
import com.mac.common.domain.UploadFile;
import com.mac.scp.api.IFiletransferService;
import com.mac.scp.domain.FileBean;
import com.mac.scp.domain.StorageBean;
import com.mac.scp.mapper.FileMapper;
import com.mac.scp.mapper.FiletransferMapper;
import com.mac.scp.session.SessionFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * @author WeiHongBin
 */
@Service
public class FiletransferServiceImpl implements IFiletransferService {

	@Resource
	FiletransferMapper filetransferMapper;
	@Resource
	FileMapper fileMapper;

	@Override
	public void uploadFile(HttpServletRequest request, FileBean fileBean) {
		Uploader uploader = new Uploader(request);
		List<UploadFile> uploadFileList = uploader.upload();
		for (UploadFile uploadFile : uploadFileList) {
			if (uploadFile.getSuccess() == 1) {
				fileBean.setFileurl(uploadFile.getUrl());
				fileBean.setFilesize(uploadFile.getFileSize());
				fileBean.setFilename(uploadFile.getFileName());
				fileBean.setExtendname(uploadFile.getFileType());
				fileBean.setTimestampname(uploadFile.getTimeStampName());
				fileBean.setUploadtime(DateUtil.getCurrentTime());
				fileMapper.insert(fileBean);
			}
			synchronized (FiletransferServiceImpl.class) {
				String token = request.getHeader(HttpHeaders.AUTHORIZATION);
				Long sessionUserId = SessionFactory.getSession().get(token);
				StorageBean storageBean = selectStorageBean(new StorageBean(sessionUserId));
				if (storageBean == null) {
					StorageBean storage = new StorageBean(sessionUserId);
					storage.setStoragesize(fileBean.getFilesize());
					insertStorageBean(storage);
				} else {
					storageBean.setStoragesize(storageBean.getStoragesize() + uploadFile.getFileSize());
					updateStorageBean(storageBean);
				}
			}

		}
	}

	@Override
	public StorageBean selectStorageBean(StorageBean storageBean) {
		return filetransferMapper.selectOne(new LambdaQueryWrapper<StorageBean>().eq(StorageBean::getUserid, storageBean.getUserid()));
	}

	@Override
	public void insertStorageBean(StorageBean storageBean) {
		filetransferMapper.insert(storageBean);
	}

	@Override
	public void updateStorageBean(StorageBean storageBean) {
		filetransferMapper.updateById(storageBean);
	}

	@Override
	public StorageBean selectStorageByUser(StorageBean storageBean) {
		return filetransferMapper.selectOne(new LambdaQueryWrapper<StorageBean>().eq(StorageBean::getUserid, storageBean.getUserid()));
	}
}
