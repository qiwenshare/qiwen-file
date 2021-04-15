package com.qiwenshare.common.operation.download;

import com.qiwenshare.common.operation.download.domain.DownloadFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

public abstract class Downloader {
    public abstract void download(HttpServletResponse httpServletResponse, DownloadFile uploadFile);
    public abstract InputStream getInputStream(DownloadFile downloadFile);
}
