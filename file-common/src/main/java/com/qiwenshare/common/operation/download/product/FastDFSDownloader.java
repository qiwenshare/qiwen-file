package com.qiwenshare.common.operation.download.product;

import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.qiwenshare.common.operation.download.domain.DownloadFile;
import com.qiwenshare.common.operation.download.Downloader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class FastDFSDownloader extends Downloader {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    @Override
    public void download(HttpServletResponse httpServletResponse, DownloadFile downloadFile) {
        String group = downloadFile.getFileUrl().substring(0, downloadFile.getFileUrl().indexOf("/"));
        String path = downloadFile.getFileUrl().substring(downloadFile.getFileUrl().indexOf("/") + 1);
        DownloadByteArray downloadByteArray = new DownloadByteArray();
        byte[] bytes = fastFileStorageClient.downloadFile(group, path, downloadByteArray);

        ServletOutputStream outputStream = null;
        try {
            outputStream = httpServletResponse.getOutputStream();
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public InputStream getInputStream(DownloadFile downloadFile) {
        String group = downloadFile.getFileUrl().substring(0, downloadFile.getFileUrl().indexOf("/"));
        String path = downloadFile.getFileUrl().substring(downloadFile.getFileUrl().indexOf("/") + 1);
        DownloadByteArray downloadByteArray = new DownloadByteArray();
        byte[] bytes = fastFileStorageClient.downloadFile(group, path, downloadByteArray);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        return inputStream;
    }
}
