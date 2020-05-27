package com.mac.scp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mac.common.operation.FileOperation;
import com.mac.common.util.PathUtil;
import com.mac.scp.api.IFileService;
import com.mac.scp.domain.FileBean;
import com.mac.scp.domain.StorageBean;
import com.mac.scp.mapper.FileMapper;
import com.mac.scp.session.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		fileMapper.insert(fileBean);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchInsertFile(List<FileBean> fileBeanList, String token) {
		StorageBean storageBean = filetransferService.selectStorageBean(new StorageBean(SessionFactory.getSession().get(token)));
		long fileSizeSum = 0;
		for (FileBean fileBean : fileBeanList) {
			if (fileBean.getIsdir() == false) {
				fileSizeSum += fileBean.getFilesize();
			}
		}
		fileBeanList.forEach(f -> fileMapper.insert(f));
		if (storageBean != null) {
			long updateFileSize = storageBean.getStoragesize() + fileSizeSum;

			storageBean.setStoragesize(updateFileSize);
			filetransferService.updateStorageBean(storageBean);
		}
	}


	@Override
	public List<FileBean> selectFilePathTreeByUserid(FileBean fileBean) {
		return fileMapper.selectList(new LambdaQueryWrapper<FileBean>().eq(FileBean::getIsdir, 1).eq(FileBean::getUserid, fileBean.getUserid()));
	}

	@Override
	public List<FileBean> selectFileList(FileBean fileBean) {
		return fileMapper.selectList(new LambdaQueryWrapper<FileBean>().eq(FileBean::getFilepath, fileBean.getFilepath())
				.eq(FileBean::getUserid, fileBean.getUserid()).orderByDesc(FileBean::getIsdir));
	}


	@Override
	public List<FileBean> selectFileTreeListLikeFilePath(String filePath) {
		FileBean fileBean = new FileBean();
		filePath = filePath.replace("\\", "\\\\\\\\");
		filePath = filePath.replace("'", "\\'");
		filePath = filePath.replace("%", "\\%");
		filePath = filePath.replace("_", "\\_");

		fileBean.setFilepath(filePath);
		return fileMapper.selectList(new LambdaQueryWrapper<FileBean>()
				.likeRight(FileBean::getFilepath, fileBean.getFilepath())
		);
	}

	@Override
	public void deleteFile(FileBean fileBean, String token) {
		StorageBean storageBean = filetransferService.selectStorageBean(new StorageBean(SessionFactory.getSession().get(token)));
		long deleteSize = 0;
		String fileUrl = PathUtil.getStaticPath() + fileBean.getFileurl();
		if (fileBean.getIsdir() == true) {
			//1、先删除子目录
			String filePath = fileBean.getFilepath() + fileBean.getFilename() + "/";
			List<FileBean> fileList = selectFileTreeListLikeFilePath(filePath);

			for (FileBean file : fileList) {
				//1.1、删除数据库文件
				fileMapper.deleteById(file.getFileid());
				//1.2、如果是文件，需要记录文件大小
				if (file.getIsdir() != true) {
					deleteSize += file.getFilesize();
					//1.3、删除服务器文件，只删除文件，目录是虚拟的
					if (file.getFileurl() != null && file.getFileurl().contains("upload")) {
						FileOperation.deleteFile(PathUtil.getStaticPath() + file.getFileurl());
					}
				}
			}
			//2、根目录单独删除
			fileMapper.deleteById(fileBean.getFileid());
		} else {
			fileMapper.deleteById(fileBean.getFileid());
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
		LambdaUpdateWrapper<FileBean> updateWrapper = new LambdaUpdateWrapper<FileBean>()
				.set(FileBean::getFilepath, newfilepath)
				.eq(FileBean::getFilepath, oldfilepath)
				.eq(FileBean::getFilename, filename);
		updateWrapper = Objects.isNull(extendname) ?
				updateWrapper.isNull(FileBean::getExtendname) :
				updateWrapper.eq(FileBean::getExtendname, extendname);

		fileMapper.update(null, updateWrapper);
		//移动子目录
		oldfilepath = oldfilepath + filename + "/";
		newfilepath = newfilepath + filename + "/";

		oldfilepath = oldfilepath.replace("\\", "\\\\\\\\");
		oldfilepath = oldfilepath.replace("'", "\\'");
		oldfilepath = oldfilepath.replace("%", "\\%");
		oldfilepath = oldfilepath.replace("_", "\\_");
		//为null说明是目录，则需要移动子目录
		if (Objects.isNull(extendname)) {
			fileMapper.update(null, new LambdaUpdateWrapper<FileBean>()
					.setSql("filepath=REPLACE(filepath,'" + oldfilepath + "', '" + newfilepath + "')")
					.likeRight(FileBean::getFilepath, oldfilepath)
			);
		}

	}

	@Override
	public List<FileBean> selectFileByExtendName(List<String> filenameList, long userid) {
		return fileMapper.selectList(new LambdaQueryWrapper<FileBean>()
				.eq(FileBean::getUserid, userid)
				.in(FileBean::getExtendname, filenameList));
	}
}
