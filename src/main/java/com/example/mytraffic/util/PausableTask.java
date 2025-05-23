package com.example.mytraffic.util;

/**
 * PausableTask：可暂停、恢复、停止的任务接口
 * 提供标准化控制线程行为的方法
 */
public interface PausableTask {

    /**
     * 暂停任务
     */
    void pause();

    /**
     * 恢复任务
     */
    void resume();

    /**
     * 停止任务
     */
    void stop();
}
