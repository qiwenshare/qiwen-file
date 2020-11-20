package com.qiwenshare.common.cbb;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.UUID;

public class ChunkUploader {

    public String chunkUploadByMappedByteBuffer(MultipartFileParam param, String filePath) throws IOException, NotSameFileExpection {

        if (param.getTaskId() == null || "".equals(param.getTaskId())) {
            param.setTaskId(UUID.randomUUID().toString());
        }

        String fileName = param.getFile().getOriginalFilename();
        String tempFileName = param.getTaskId() + fileName.substring(fileName.lastIndexOf(".")) + "_tmp";
        File fileDir = new File(filePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File tempFile = new File(filePath, tempFileName);
        //第一步 打开将要写入的文件
        RandomAccessFile raf = new RandomAccessFile(tempFile, "rw");
        //第二步 打开通道
        FileChannel fileChannel = raf.getChannel();
        //第三步 计算偏移量
        long position = (param.getChunkNumber() - 1) * param.getChunkSize();
        //第四步 获取分片数据
        byte[] fileData = param.getFile().getBytes();
        //第五步 写入数据
        fileChannel.position(position);
        fileChannel.write(ByteBuffer.wrap(fileData));
        fileChannel.force(true);
        fileChannel.close();
        raf.close();
        //判断是否完成文件的传输并进行校验与重命名
        boolean isComplete = checkUploadStatus(param, fileName, filePath);
        if (isComplete) {
            FileInputStream fileInputStream = new FileInputStream(tempFile.getPath());
            String md5 = DigestUtils.md5Hex(fileInputStream);
            fileInputStream.close();
            if (StringUtils.isNotBlank(md5) && !md5.equals(param.getIdentifier())) {
                throw new NotSameFileExpection();
            }
            renameFile(tempFile, fileName);
            return fileName;
        }
        return null;
    }

    public void renameFile(File toBeRenamed, String toFileNewName) {
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
            System.err.println("文件不存在");
            return;
        }
        String p = toBeRenamed.getParent();
        File newFile = new File(p + File.separatorChar + toFileNewName);
        toBeRenamed.renameTo(newFile);
    }

    public boolean checkUploadStatus(MultipartFileParam param, String fileName, String filePath) throws IOException {
        File confFile = new File(filePath, fileName + ".conf");
        confFile.createNewFile();
        RandomAccessFile confAccessFile = new RandomAccessFile(confFile, "rw");
        //设置文件长度
        confAccessFile.setLength(param.getTotalChunks());
        //设置起始偏移量
        confAccessFile.seek(param.getChunkNumber() - 1);
        //将指定的一个字节写入文件中 127，
        confAccessFile.write(Byte.MAX_VALUE);
        byte[] completeStatusList = FileUtils.readFileToByteArray(confFile);
        confAccessFile.close();//不关闭会造成无法占用
        //创建conf文件文件长度为总分片数，每上传一个分块即向conf文件中写入一个127，那么没上传的位置就是默认的0,已上传的就是127
        for (int i = 0; i < completeStatusList.length; i++) {
            if (completeStatusList[i] != Byte.MAX_VALUE) {
                return false;
            }
        }
        confFile.delete();
        return true;
    }
}
