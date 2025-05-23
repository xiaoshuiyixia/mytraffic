package com.example.mytraffic.manager;

import com.example.mytraffic.model.Car;

import java.util.concurrent.*;

/**
 * CarThreadManager：车辆线程池管理器。
 * 管理所有车辆线程的调度、提交与关闭，防止线程资源泄漏。
 */
public class CarThreadManager {

    private static final int MAX_THREADS = 60; // 最大车辆线程数，可根据项目规模调整
    private static ExecutorService carExecutor;

    private CarThreadManager() {
        // 私有构造器，防止外部实例化
    }

    // ============================
    // 初始化与任务提交
    // ============================

    /**
     * 初始化车辆线程池（固定线程数）
     */
    public static void init() {
        if (carExecutor == null || carExecutor.isShutdown()) {
            synchronized (CarThreadManager.class) {
                if (carExecutor == null || carExecutor.isShutdown()) {
                    System.out.println("初始化车辆线程池 (最大线程数: " + MAX_THREADS + ")");
                    carExecutor = new ThreadPoolExecutor(
                            MAX_THREADS, MAX_THREADS,
                            0L, TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<>(),
                            new CarThreadFactory()
                    );
                }
            }
        }
    }


    /**
     * 提交车辆任务执行（Car 实现 Runnable）
     *
     * @param task 车辆线程任务
     * @return Future 对象用于任务控制
     */
    public static Future<?> submit(Runnable task) {
        if (carExecutor == null || carExecutor.isShutdown()) {
            throw new IllegalStateException("CarThreadManager未初始化或线程池已关闭！");
        }
        System.out.println("提交车辆任务 ID: " + ((Car) task).getId());
        return carExecutor.submit(task);
    }

    // ============================
    // 关闭线程池
    // ============================

    /**
     * 安全关闭线程池，尝试优雅退出任务
     */
    public static void shutdown() {
        if (carExecutor != null && !carExecutor.isShutdown()) {
            System.out.println("关闭车辆线程池...");
            carExecutor.shutdown();
            try {
                if (!carExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.out.println("车辆线程池未及时关闭，强制关闭！");
                    carExecutor.shutdownNow();
                } else {
                    System.out.println("车辆线程池已正常关闭。");
                }
            } catch (InterruptedException e) {
                carExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    // ============================
    // 自定义线程工厂
    // ============================

    /**
     * 自定义线程命名工厂，方便调试线程来源
     */
    private static class CarThreadFactory implements ThreadFactory {
        private int count = 0;

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("Car-Thread-" + (++count));
            thread.setDaemon(true); // 设置为守护线程，主程序退出时自动回收
            return thread;
        }
    }
}
