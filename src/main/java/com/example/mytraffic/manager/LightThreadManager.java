package com.example.mytraffic.manager;

import java.util.concurrent.*;

/**
 * LightThreadManager：交通灯线程池管理器。
 * 负责调度交通灯（TrafficLight）的并发任务执行与安全关闭。
 */
public class LightThreadManager {

    private static final int MAX_LIGHT_THREADS = 15; // 最大交通灯线程数
    private static ExecutorService lightExecutor;

    private LightThreadManager() {
        // 私有构造器，禁止外部实例化
    }

    // ============================
    // 初始化与任务提交
    // ============================

    /**
     * 初始化交通灯线程池（固定大小）
     */
    public static void init() {
        if (lightExecutor == null || lightExecutor.isShutdown()) {
            System.out.println("初始化交通灯线程池 (最大线程数: " + MAX_LIGHT_THREADS + ")");
            lightExecutor = new ThreadPoolExecutor(
                    MAX_LIGHT_THREADS, MAX_LIGHT_THREADS,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(),
                    new LightThreadFactory()
            );
        }
    }

    /**
     * 提交交通灯任务执行
     * @param task Runnable 实例（通常是 TrafficLight）
     * @return Future 控制对象
     */
    public static Future<?> submit(Runnable task) {
        if (lightExecutor == null || lightExecutor.isShutdown()) {
            throw new IllegalStateException("LightThreadManager未初始化或线程池已关闭！");
        }
        return lightExecutor.submit(task);
    }

    // ============================
    // 关闭线程池
    // ============================

    /**
     * 停止并关闭交通灯线程池
     */
    public static void shutdown() {
        if (lightExecutor != null && !lightExecutor.isShutdown()) {
            System.out.println("关闭交通灯线程池...");
            lightExecutor.shutdown();
            try {
                if (!lightExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.out.println("交通灯线程池未及时关闭，强制关闭！");
                    lightExecutor.shutdownNow();
                } else {
                    System.out.println("交通灯线程池已正常关闭。");
                }
            } catch (InterruptedException e) {
                lightExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    // ============================
    // 自定义线程工厂
    // ============================

    /**
     * 自定义线程命名工厂，用于为每个交通灯线程命名
     */
    private static class LightThreadFactory implements ThreadFactory {
        private int count = 0;

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("Light-Thread-" + (++count));
            thread.setDaemon(true); // 守护线程，程序退出自动关闭
            return thread;
        }
    }
}
