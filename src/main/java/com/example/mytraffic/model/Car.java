package com.example.mytraffic.model;

import com.example.mytraffic.observer.CarObserver;
import com.example.mytraffic.util.ConfigLoader;
import com.example.mytraffic.util.PausableTask;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Car：车辆模型类，包含运行状态、线程逻辑、交通灯响应与导出字段。
 */
public class Car implements Runnable, PausableTask {

    // ============================
    // 静态配置加载（最大/最小速度）
    // ============================
    private static final int MAX_SPEED;
    private static final int MIN_SPEED;

    static {
        ConfigLoader loader = ConfigLoader.getInstance();
        MAX_SPEED = loader.getIntProperty("CAR_MAX_SPEED", 200); // 像素/秒
        MIN_SPEED = loader.getIntProperty("CAR_MIN_SPEED", 20);
    }

    // ============================
    // 基础属性
    // ============================
    private final int id;
    private double positionX;
    private final int permanentSpeed;
    private int speed;
    private int laneIndex = 1;

    private volatile boolean running = true;
    private volatile boolean paused = false;

    // ============================
    // 状态引用
    // ============================
    private final List<CarObserver> observers = new ArrayList<>();
    private volatile List<TrafficLight> trafficLights;
    private List<Car> allCars;

    // ============================
    // 统计字段（用于CSV导出）
    // ============================
    private long enterTime;
    private long totalWaitTime = 0;
    private boolean wasBlocked = false;

    public Car(int id) {
        this.id = id;
        this.positionX = -30;
        Random rand = new Random();
        this.permanentSpeed = rand.nextInt(MAX_SPEED - MIN_SPEED + 1) + MIN_SPEED;
        this.speed = permanentSpeed;
    }

    // ============================
    // 线程运行主逻辑
    // ============================
    @Override
    public void run() {
        System.out.println("开始车辆 ID: " + id + " 的线程");
        enterTime = System.currentTimeMillis();
        while (running) {
            try {
                if (!paused) {
                    move();
                    notifyObservers();
                }
                Thread.sleep(50);
            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 移动逻辑，判断是否受阻或红灯停滞。
     */
    private void move() {
        Car frontCar = findNearestFrontCar();
        boolean blocked = false;

        if (frontCar != null) {
            double distanceToFrontCar = frontCar.positionX - this.positionX;
            if (distanceToFrontCar <= 30) {
                blocked = true;
                wasBlocked = true;
            }
        }

        Color nearestLightColor = findNearestTrafficLightColor();

        if (blocked || nearestLightColor.equals(Color.RED)) {
            if (nearestLightColor.equals(Color.RED)) {
                totalWaitTime += 50;
            }
            speed = 0;
        } else {
            if (speed == 0) {
                speed = permanentSpeed;
            }
            positionX += (speed / 20.0);
            if (positionX > 1000) {
                positionX = -30;
            }
        }
    }

    /**
     * 查找最近红绿灯颜色
     */
    public Color findNearestTrafficLightColor() {
        if (trafficLights == null || trafficLights.isEmpty()) return Color.GREEN;

        TrafficLight nearestFrontLight = null;
        double minDistance = Double.MAX_VALUE;

        for (TrafficLight light : trafficLights) {
            double dx = light.getPositionX() - this.positionX;
            if (dx >= 0 && dx < minDistance) {
                minDistance = dx;
                nearestFrontLight = light;
            }
        }

        if (nearestFrontLight == null) return Color.GREEN;
        if (nearestFrontLight.getCurrentColor().equals(Color.RED) && minDistance <= 30) {
            return Color.RED;
        }

        return Color.GREEN;
    }


    /** 设置所有车辆列表（用于前车检测） */
    public void setAllCars(List<Car> allCars) {
        this.allCars = allCars;
    }

    /**
     * 查找最近前方同车道车辆
     */
    private Car findNearestFrontCar() {
        if (allCars == null || allCars.isEmpty()) return null;

        Car nearestFrontCar = null;
        double minDistance = Double.MAX_VALUE;

        for (Car other : allCars) {
            if (other == this) continue;
            if (other.laneIndex != this.laneIndex) continue;

            double dx = other.positionX - this.positionX;
            if (dx > 0 && dx < minDistance) {
                minDistance = dx;
                nearestFrontCar = other;
            }
        }
        return nearestFrontCar;
    }

    /** 通知所有观察者进行界面刷新 */
    private void notifyObservers() {
        for (CarObserver observer : observers) {
            observer.onCarUpdated(this);
        }
    }

    public void registerObserver(CarObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(CarObserver observer) {
        observers.remove(observer);
    }

    // ============================
    // 生命周期控制接口
    // ============================
    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void stop() {
        running = false;
    }

    // ============================
    // Getter & Setter
    // ============================

    public int getId() {
        return id;
    }

    public double getPositionX() {
        return positionX;
    }

    public int getSpeed() {
        return speed;
    }

    public int getPermanentSpeed() {
        return permanentSpeed;
    }

    public void setTrafficLights(List<TrafficLight> trafficLights) {
        this.trafficLights = trafficLights;
    }

    public int getLaneIndex() {
        return laneIndex;
    }

    public void setLaneIndex(int laneIndex) {
        this.laneIndex = laneIndex;
    }

    public long getEnterTime() {
        return enterTime;
    }

    public long getTotalWaitTime() {
        return totalWaitTime;
    }

    public boolean isWasBlocked() {
        return wasBlocked;
    }

    // ============================
    // CSV 导出字段封装
    // ============================

    /** 返回车辆状态的CSV数据行 */
    public String getCsvRow() {
        long now = System.currentTimeMillis();
        return id + "," + laneIndex + "," + enterTime + "," + now + "," +
                String.format("%.2f", positionX) + "," + speed + "," +
                wasBlocked + "," + totalWaitTime;
    }
}
