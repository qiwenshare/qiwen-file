package com.mac.scp.controller;

import com.mac.common.cbb.RestResult;
import com.mac.common.exception.UnifiedException;
import com.mac.common.operation.FileOperation;
import com.mac.common.util.PathUtil;
import com.mac.scp.api.IFiletransferService;
import com.mac.scp.domain.FileBean;
import com.mac.scp.domain.StorageBean;
import com.mac.scp.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@RestController
@RequestMapping("/filetransfer")
public class FiletransferController {

	@Resource
	IFiletransferService filetransferService;
	@Autowired
	private FileController fileController;

	/**
	 * 上传文件
	 *
	 * @param request
	 * @return
	 */
	@PostMapping("/uploadfile")
	@ResponseBody
	public RestResult<String> uploadFile(HttpServletRequest request, FileBean fileBean, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
		RestResult<String> restResult = new RestResult<String>();
		Long s = SessionFactory.getSession().get(token);
		if (Objects.isNull(s)) {
			throw new UnifiedException("token错误");
		}
		RestResult<String> operationCheckResult = fileController.operationCheck(s);
		if (!operationCheckResult.isSuccess()) {
			return operationCheckResult;
		}

		fileBean.setUserid(s);

		filetransferService.uploadFile(request, fileBean);

		restResult.setSuccess(true);
		return restResult;
	}

	/**
	 * 下载文件
	 *
	 * @return
	 */
	// TODO 下载文件
	@GetMapping("/downloadfile")
	public String downloadFile(HttpServletResponse response, FileBean fileBean) {
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
	// TODO 获取存储占用
	@GetMapping("/getstorage")
	@ResponseBody
	public RestResult<StorageBean> getStorage(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
		RestResult<StorageBean> restResult = new RestResult<StorageBean>();
		Long id = SessionFactory.getSession().get(token);
		if (Objects.isNull(id)) {
			throw new UnifiedException("token 错误");
		}
		StorageBean storageBean = new StorageBean();
		if (FileController.isShareFile) {
			storageBean.setUserid(2);
		} else {
			storageBean.setUserid(id);
		}

		StorageBean storage = filetransferService.selectStorageByUser(storageBean);
		restResult.setData(storage);
		restResult.setSuccess(true);
		return restResult;
	}


}
