package com.mac.common.domain;

public class TaskProcess {
    private int totalCount;
    private int completeCount;
    private String taskInfo;
    /**
     * 是否任务运行
     */
    private boolean isRunTask;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getCompleteCount() {
        return completeCount;
    }

    public void setCompleteCount(int completeCount) {
        this.completeCount = completeCount;
    }

    public String getTaskInfo() {
        return taskInfo;
    }

    public void setTaskInfo(String taskInfo) {
        this.taskInfo = taskInfo;
    }

    public boolean isRunTask() {
        return isRunTask;
    }

    public void setRunTask(boolean isRunTask) {
        this.isRunTask = isRunTask;
    }


}
