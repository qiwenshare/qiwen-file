package com.mac.scp.service;

import com.mac.common.operation.FileOperation;
import com.mac.common.util.PathUtil;
import com.mac.scp.api.IFileService;
import com.mac.scp.domain.FileBean;
import com.mac.scp.domain.StorageBean;
import com.mac.scp.mapper.FileMapper;
import com.mac.scp.session.SessionFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;


@Service
public class FileServiceImpl implements IFileService {

	@Resource
	FileMapper fileMapper;
	@Resource
	FiletransferServiceImpl filetransferService;

	@Override
	public void insertFile(FileBean fileBean) {
		fileMapper.insertFile(fileBean);
	}

	@Override
	public void batchInsertFile(List<FileBean> fileBeanList, String token) {
		StorageBean storageBean = filetransferService.selectStorageBean(new StorageBean(SessionFactory.getSession().get(token)));
		long fileSizeSum = 0;
		for (FileBean fileBean : fileBeanList) {
			if (fileBean.getIsdir() == 0) {
				fileSizeSum += fileBean.getFilesize();
			}
		}
		fileMapper.batchInsertFile(fileBeanList);
		if (storageBean != null) {
			long updateFileSize = storageBean.getStoragesize() + fileSizeSum;

			storageBean.setStoragesize(updateFileSize);
			filetransferService.updateStorageBean(storageBean);
		}
	}


	@Override
	public List<FileBean> selectFilePathTreeByUserid(FileBean fileBean) {
		return fileMapper.selectFilePathTreeByUserid(fileBean);
	}

	@Override
	public List<FileBean> selectFileList(FileBean fileBean) {
		return fileMapper.selectFileList(fileBean);
	}


	@Override
	public List<FileBean> selectFileTreeListLikeFilePath(String filePath) {
		FileBean fileBean = new FileBean();
		filePath = filePath.replace("\\", "\\\\\\\\");
		filePath = filePath.replace("'", "\\'");
		filePath = filePath.replace("%", "\\%");
		filePath = filePath.replace("_", "\\_");

		fileBean.setFilepath(filePath);

		return fileMapper.selectFileTreeListLikeFilePath(fileBean);
	}

	@Override
	public void deleteFile(FileBean fileBean, String token) {
		StorageBean storageBean = filetransferService.selectStorageBean(new StorageBean(SessionFactory.getSession().get(token)));
		long deleteSize = 0;
		String fileUrl = PathUtil.getStaticPath() + fileBean.getFileurl();
		if (fileBean.getIsdir() == 1) {
			//1、先删除子目录
			String filePath = fileBean.getFilepath() + fileBean.getFilename() + "/";
			List<FileBean> fileList = selectFileTreeListLikeFilePath(filePath);

			for (FileBean file : fileList) {
				//1.1、删除数据库文件
				fileMapper.deleteFileById(file);
				//1.2、如果是文件，需要记录文件大小
				if (file.getIsdir() != 1) {
					deleteSize += file.getFilesize();
					//1.3、删除服务器文件，只删除文件，目录是虚拟的
					if (file.getFileurl() != null && file.getFileurl().contains("upload")) {
						FileOperation.deleteFile(PathUtil.getStaticPath() + file.getFileurl());
					}
				}
			}
			//2、根目录单独删除
			fileMapper.deleteFileById(fileBean);
		} else {
			fileMapper.deleteFileById(fileBean);
			deleteSize = FileOperation.getFileSize(fileUrl);
			//删除服务器文件
			if (fileBean.getFileurl() != null && fileBean.getFileurl().contains("upload")) {
				FileOperation.deleteFile(fileUrl);
			}
		}

		if (storageBean != null) {
			long updateFileSize = storageBean.getStoragesize() - deleteSize;
			if (updateFileSize < 0) {
				updateFileSize = 0;
			}
			storageBean.setStoragesize(updateFileSize);
			filetransferService.updateStorageBean(storageBean);
		}
	}


	@Override
	public void updateFilepathByFilepath(String oldfilepath, String newfilepath, String filename, String extendname) {
		if ("null".equals(extendname)) {
			extendname = null;
		}
		//移动根目录
		fileMapper.updateFilepathByPathAndName(oldfilepath, newfilepath, filename, extendname);

		//移动子目录
		oldfilepath = oldfilepath + filename + "/";
		newfilepath = newfilepath + filename + "/";

		oldfilepath = oldfilepath.replace("\\", "\\\\\\\\");
		oldfilepath = oldfilepath.replace("'", "\\'");
		oldfilepath = oldfilepath.replace("%", "\\%");
		oldfilepath = oldfilepath.replace("_", "\\_");
		//为null说明是目录，则需要移动子目录
		if (Objects.isNull(extendname)) {
			fileMapper.updateFilepathByFilepath(oldfilepath, newfilepath);
		}

	}

	@Override
	public List<FileBean> selectFileByExtendName(List<String> filenameList, long userid) {
		return fileMapper.selectFileByExtendName(filenameList, userid);
	}
}
