package com.qiwenshare.file.api;

import com.qiwenshare.file.domain.FileBean;
import com.qiwenshare.file.domain.UserFile;
import com.qiwenshare.file.dto.file.DownloadFileDTO;
import com.qiwenshare.file.dto.file.PreviewDTO;
import com.qiwenshare.file.dto.file.UploadFileDTO;
import com.qiwenshare.file.vo.file.UploadFileVo;
import com.qiwenshare.ufop.constant.StorageTypeEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface IFiletransferService {

    UploadFileVo uploadFileSpeed(UploadFileDTO uploadFileDTO);

    void uploadFile(HttpServletRequest request, UploadFileDTO UploadFileDto, Long userId);

    void downloadFile(HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO);
    void downloadUserFileList(HttpServletResponse httpServletResponse, String filePath, String fileName, List<String> userFileIds);
    void previewFile(HttpServletResponse httpServletResponse, PreviewDTO previewDTO);
    void previewPictureFile(HttpServletResponse httpServletResponse, PreviewDTO previewDTO);
    void deleteFile(FileBean fileBean);

    Long selectStorageSizeByUserId(Long userId);
}
