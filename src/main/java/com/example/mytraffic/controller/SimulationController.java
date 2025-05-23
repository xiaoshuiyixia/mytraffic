package com.example.mytraffic.controller;

/**
 * SimulationController：统一管理模拟的开始、暂停、继续、停止状态
 * 调用 CarController 和 TrafficLightController 对车辆与交通灯统一调度控制。
 */
public class SimulationController {

    private final CarController carController;
    private final TrafficLightController trafficLightController;

    private volatile boolean running = false;  // 当前是否正在运行
    private volatile boolean paused = false;   // 当前是否暂停

    public SimulationController(CarController carController, TrafficLightController trafficLightController) {
        this.carController = carController;
        this.trafficLightController = trafficLightController;
    }

    // =======================
    // 模拟控制方法
    // =======================

    /**
     * 启动模拟（初始化线程并设置状态）
     */
    public synchronized void start() {
        if (!running) {
            running = true;
            paused = false;
            resumeAll();
            System.out.println("模拟已开始");
        }
    }

    /**
     * 暂停或恢复模拟运行状态
     */
    public synchronized void togglePause() {
        if (!running) return; // 若未开始，不能暂停

        if (!paused) {
            paused = true;
            pauseAll();
            System.out.println("模拟已暂停");
        } else {
            paused = false;
            resumeAll();
            System.out.println("模拟已继续");
        }
    }

    /**
     * 停止模拟（清除所有资源，重置状态）
     */
    public synchronized void stop() {
        if (running) {
            running = false;
            paused = false;
            stopAll();
            System.out.println("模拟已停止");
        }
    }

    // =======================
    // 子模块控制方法
    // =======================

    /**
     * 暂停车辆与交通灯线程
     */
    private void pauseAll() {
        carController.pauseAllCars();
        trafficLightController.pauseAllLights();
    }

    /**
     * 恢复车辆与交通灯线程
     */
    private void resumeAll() {
        carController.resumeAllCars();
        trafficLightController.resumeAllLights();
    }

    /**
     * 停止车辆与交通灯线程并清空状态
     */
    private void stopAll() {
        carController.clearAllCars();
        trafficLightController.clearAllTrafficLights();
    }

    // =======================
    // 状态查询接口
    // =======================

    /**
     * 判断模拟是否处于运行状态
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * 判断模拟是否处于暂停状态
     */
    public boolean isPaused() {
        return paused;
    }
}
