package com.mac.scp.api;

import com.mac.scp.domain.FileBean;

import java.util.List;

public interface IFileService {

	void insertFile(FileBean fileBean);

	void batchInsertFile(List<FileBean> fileBeanList, String token);

	List<FileBean> selectFilePathTreeByUserid(FileBean fileBean);

	List<FileBean> selectFileList(FileBean fileBean);

	List<FileBean> selectFileTreeListLikeFilePath(String filePath);

	void deleteFile(FileBean fileBean, String token);

	void updateFilepathByFilepath(String oldfilepath, String newfilepath, String filename, String extendname);

	List<FileBean> selectFileByExtendName(List<String> filenameList, long userid);
}
