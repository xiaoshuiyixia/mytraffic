package com.example.mytraffic.model;

import com.example.mytraffic.observer.TrafficLightObserver;
import com.example.mytraffic.util.ConfigLoader;
import com.example.mytraffic.util.PausableTask;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TrafficLight：交通灯模型类，仅包含运行逻辑与状态，不处理图形。
 */
public class TrafficLight implements Runnable, PausableTask {

    // ============================
    // 基础属性
    // ============================
    private final int positionX;              // 灯的X坐标位置
    private volatile Color currentColor;      // 当前颜色（红/绿/黄）
    private volatile int countdownMs;         // 当前颜色剩余时间（毫秒）

    private volatile boolean running = true;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object(); // 控制线程暂停/恢复

    private final List<TrafficLightObserver> observers = new ArrayList<>();

    public TrafficLight(int positionX) {
        this.positionX = positionX;
        this.currentColor = randomStartColor();
        this.countdownMs = getColorDuration(currentColor);
    }

    // ============================
    // 线程执行逻辑
    // ============================
    @Override
    public void run() {
        try {
            while (running) {
                long startTime = System.currentTimeMillis();
                long endTime = startTime + countdownMs;

                while (System.currentTimeMillis() < endTime) {
                    synchronized (pauseLock) {
                        if (paused) pauseLock.wait();
                    }
                    countdownMs = (int) (endTime - System.currentTimeMillis());
                    notifyObservers();
                    Thread.sleep(100);
                }

                if (running && !paused) {
                    switchToNextColor();
                    notifyObservers();
                }
            }
        } catch (InterruptedException e) {
            running = false;
            Thread.currentThread().interrupt();
        }
    }

    // ============================
    // 状态变更逻辑
    // ============================

    /** 切换下一个灯色，并重置计时 */
    private void switchToNextColor() {
        if (currentColor == Color.RED) {
            currentColor = Color.GREEN;
        } else if (currentColor == Color.GREEN) {
            currentColor = Color.YELLOW;
        } else {
            currentColor = Color.RED;
        }
        countdownMs = getColorDuration(currentColor);
    }

    /** 根据颜色从配置中获取对应持续时间 */
    private int getColorDuration(Color color) {
        ConfigLoader loader = ConfigLoader.getInstance();
        if (color == Color.RED) return loader.getIntProperty("LIGHT_RED_DURATION", 7000);
        if (color == Color.GREEN) return loader.getIntProperty("LIGHT_GREEN_DURATION", 5000);
        return loader.getIntProperty("LIGHT_YELLOW_DURATION", 2000);
    }

    /** 随机生成初始灯色（红/绿/黄） */
    private Color randomStartColor() {
        return switch (new Random().nextInt(3)) {
            case 0 -> Color.RED;
            case 1 -> Color.GREEN;
            case 2 -> Color.YELLOW;
            default -> Color.RED;
        };
    }

    // ============================
    // 观察者通知
    // ============================

    /** 通知所有注册的观察者刷新界面 */
    private void notifyObservers() {
        for (TrafficLightObserver observer : observers) {
            observer.onTrafficLightUpdated(this);
        }
    }

    public void registerObserver(TrafficLightObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TrafficLightObserver observer) {
        observers.remove(observer);
    }

    // ============================
    // 生命周期控制
    // ============================

    @Override
    public void pause() {
        synchronized (pauseLock) {
            paused = true;
        }
    }

    @Override
    public void resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    @Override
    public void stop() {
        running = false;
    }

    // ============================
    // Getter
    // ============================

    public Color getCurrentColor() {
        return currentColor;
    }

    public int getCountdownMs() {
        return countdownMs;
    }

    public int getPositionX() {
        return positionX;
    }
}
