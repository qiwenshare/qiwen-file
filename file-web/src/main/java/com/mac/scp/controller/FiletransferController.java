package com.mac.scp.controller;

import com.alibaba.fastjson.JSON;
import com.mac.common.cbb.RestResult;
import com.mac.common.operation.FileOperation;
import com.mac.common.operation.ImageOperation;
import com.mac.common.util.PathUtil;
import com.mac.scp.api.IFileService;
import com.mac.scp.api.IFiletransferService;
import com.mac.scp.domain.FileBean;
import com.mac.scp.domain.StorageBean;
import com.mac.scp.domain.UserBean;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/filetransfer")
public class FiletransferController {

	@Resource
	IFiletransferService filetransferService;
	@Resource
	IFileService fileService;


	/**
	 * 旋转图片
	 *
	 * @param direction 方向
	 * @param imageid   图片id
	 * @return 返回结果
	 */
	@PostMapping("/totationimage")
	@ResponseBody
	public RestResult<String> totationImage(@RequestBody String direction, @RequestBody int imageid) {
		RestResult<String> result = new RestResult<String>();
		FileBean fileBean = new FileBean();
		fileBean.setFileid(imageid);
		fileBean = fileService.selectFileById(fileBean);
		String imageUrl = fileBean.getFileurl();
		String extendName = fileBean.getExtendname();
		File file = FileOperation.newFile(PathUtil.getStaticPath() + imageUrl);
		File minfile = FileOperation.newFile(PathUtil.getStaticPath() + imageUrl.replace("." + extendName, "_min." + extendName));
		if ("left".equals(direction)) {
			try {
				ImageOperation.leftTotation(file, file, 90);
				ImageOperation.leftTotation(minfile, minfile, 90);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ("right".equals(direction)) {
			try {
				ImageOperation.rightTotation(file, file, 90);
				ImageOperation.rightTotation(minfile, minfile, 90);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		result.setSuccess(true);
		return result;
	}

	/**
	 * 批量删除图片
	 *
	 * @return
	 */
	@PostMapping("/deleteimagebyids")
	@ResponseBody
	public String deleteImageByIds(@RequestBody String imageids) {
		RestResult<String> result = new RestResult<String>();
		List<Integer> imageidList = JSON.parseArray(imageids, Integer.class);
		UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();

		long sessionUserId = sessionUserBean.getUserId();

//        List<ImageBean> imageBeanList = filetransferService.selectUserImageByIds(imageidList);
//        filetransferService.deleteUserImageByIds(imageidList);
		List<FileBean> fileList = fileService.selectFileListByIds(imageidList);
		fileService.deleteFileByIds(imageidList);
		long totalFileSize = 0;
		for (FileBean fileBean : fileList) {
			String imageUrl = PathUtil.getStaticPath() + fileBean.getFileurl();
			String minImageUrl = imageUrl.replace("." + fileBean.getExtendname(), "_min." + fileBean.getExtendname());
			totalFileSize += FileOperation.getFileSize(imageUrl);
			FileOperation.deleteFile(imageUrl);
			FileOperation.deleteFile(minImageUrl);
		}
		StorageBean storageBean = filetransferService.selectStorageBean(new StorageBean(sessionUserId));
		if (storageBean != null) {
			long updateFileSize = storageBean.getStoragesize() - totalFileSize;
			if (updateFileSize < 0) {
				updateFileSize = 0;
			}
			storageBean.setStoragesize(updateFileSize);
			filetransferService.updateStorageBean(storageBean);

		}

		result.setData("删除文件成功");
		result.setSuccess(true);
		String resultJson = JSON.toJSONString(result);
		return resultJson;
	}

	/**
	 * 删除图片
	 *
	 * @param request
	 * @return
	 */
	@PostMapping("/deleteimage")
	@ResponseBody
	public String deleteImage(HttpServletRequest request, @RequestBody FileBean fileBean) {
		RestResult<String> result = new RestResult<String>();
		UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
		long sessionUserId = sessionUserBean.getUserId();
		String imageUrl = PathUtil.getStaticPath() + fileBean.getFileurl();
		String minImageUrl = imageUrl.replace("." + fileBean.getExtendname(), "_min." + fileBean.getExtendname());
		long fileSize = FileOperation.getFileSize(imageUrl);
		fileBean.setIsdir(0);
		//filetransferService.deleteImageById(fileBean);
		fileService.deleteFile(fileBean);

		FileOperation.deleteFile(imageUrl);
		FileOperation.deleteFile(minImageUrl);


		StorageBean storageBean = filetransferService.selectStorageBean(new StorageBean(sessionUserId));
		if (storageBean != null) {
			long updateFileSize = storageBean.getStoragesize() - fileSize;
			if (updateFileSize < 0) {
				updateFileSize = 0;
			}
			storageBean.setStoragesize(updateFileSize);
			filetransferService.updateStorageBean(storageBean);

		}

		String resultJson = JSON.toJSONString(result);
		return resultJson;
	}

	/**
	 * 上传头像
	 *
	 * @param request
	 * @return
	 */
	@PostMapping("/uploadimg")
	@ResponseBody
	public String uploadImg(HttpServletRequest request) {
		RestResult<String> restResult = filetransferService.uploadUserImage(request);
		String resultJson = JSON.toJSONString(restResult);
		return resultJson;
	}

	/**
	 * 上传文件
	 *
	 * @param request
	 * @return
	 */
	@PostMapping("/uploadfile")
	@ResponseBody
	public String uploadFile(HttpServletRequest request, FileBean fileBean) {
		RestResult<String> restResult = new RestResult<String>();
		UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
		RestResult<String> operationCheckResult = new FileController().operationCheck();
		if (!operationCheckResult.isSuccess()) {
			return JSON.toJSONString(operationCheckResult);
		}

		fileBean.setUserid(sessionUserBean.getUserId());

		filetransferService.uploadFile(request, fileBean);

		restResult.setSuccess(true);
		return JSON.toJSONString(restResult);
	}

	/**
	 * 下载文件
	 *
	 * @return
	 */
	@GetMapping("/downloadfile")
	public String downloadFile(HttpServletResponse response, FileBean fileBean) {
		RestResult<String> restResult = new RestResult<>();
		// 文件名
		String fileName = new String(fileBean.getFilename().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
		fileName = fileName + "." + fileBean.getExtendname();
		//设置文件路径
		File file = FileOperation.newFile(PathUtil.getStaticPath() + fileBean.getFileurl());
		if (file.exists()) {
			// 设置强制下载不打开
			response.setContentType("application/force-download");
			// 设置文件名
			response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
			byte[] buffer = new byte[1024];
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			try {
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				OutputStream os = response.getOutputStream();
				int i = bis.read(buffer);
				while (i != -1) {
					os.write(buffer, 0, i);
					i = bis.read(buffer);
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (bis != null) {
					try {
						bis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;

	}


	/**
	 * 获取存储信息
	 *
	 * @return
	 */
	@GetMapping("/getstorage")
	@ResponseBody
	public RestResult<StorageBean> getStorage() {
		RestResult<StorageBean> restResult = new RestResult<StorageBean>();
		UserBean sessionUserBean = (UserBean) SecurityUtils.getSubject().getPrincipal();
		StorageBean storageBean = new StorageBean();
		if (FileController.isShareFile) {
			storageBean.setUserid(2);
		} else {
			storageBean.setUserid(sessionUserBean.getUserId());
		}

		StorageBean storage = filetransferService.selectStorageByUser(storageBean);
		restResult.setData(storage);
		restResult.setSuccess(true);
		return restResult;
	}


}
