package com.qiwenshare.common.cbb;

import org.springframework.web.multipart.MultipartFile;
 
public class MultipartFileParam {
    private String taskId;
    private int chunkNumber;
    private long chunkSize;
    private int totalChunks;
    private String identifier;
    private MultipartFile file;
 
    public String getTaskId() {
        return taskId;
    }
 
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
 
    public int getChunkNumber() {
        return chunkNumber;
    }
 
    public void setChunkNumber(int chunkNumber) {
        this.chunkNumber = chunkNumber;
    }
 
    public long getChunkSize() {
        return chunkSize;
    }
 
    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }
 
    public int getTotalChunks() {
        return totalChunks;
    }
 
    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }
 
    public String getIdentifier() {
        return identifier;
    }
 
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
 
    public MultipartFile getFile() {
        return file;
    }
 
    public void setFile(MultipartFile file) {
        this.file = file;
    }
}