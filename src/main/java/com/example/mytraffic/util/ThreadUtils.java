package com.example.mytraffic.util;

import javafx.application.Platform;

/**
 * ThreadUtils：线程辅助工具类
 * 提供常用线程操作的封装
 */
public class ThreadUtils {

    private ThreadUtils() {
        // 私有构造器，禁止实例化
    }

    /**
     * 在JavaFX应用线程中安全地执行任务
     *
     * @param runnable 要执行的任务
     */
    public static void runOnUIThread(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }

    /**
     * 线程休眠指定毫秒数
     *
     * @param millis 毫秒数
     */
    public static void sleepMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 保持中断状态
            System.err.println("线程休眠被中断！");
        }
    }

    /**
     * 判断当前线程是否是JavaFX应用线程
     *
     * @return true如果是UI线程，false否则
     */
    public static boolean isUIThread() {
        return Platform.isFxApplicationThread();
    }
}
