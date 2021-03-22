package com.qiwenshare.common.download;

import com.qiwenshare.common.domain.DownloadFile;
import com.qiwenshare.common.domain.UploadFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public abstract class Downloader {
    public abstract void download(HttpServletResponse httpServletResponse, DownloadFile uploadFile);
}
