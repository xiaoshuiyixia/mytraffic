package com.example.mytraffic.controller;

import com.example.mytraffic.factory.TrafficLightFactory;
import com.example.mytraffic.manager.LightThreadManager;
import com.example.mytraffic.model.TrafficLight;
import com.example.mytraffic.view.TrafficLightView;
import com.example.mytraffic.ui.PointPane;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TrafficLightController：负责管理所有交通灯的创建、删除、线程控制与图形同步。
 */
public class TrafficLightController {

    private final List<TrafficLight> trafficLights;         // 逻辑交通灯列表
    private final List<TrafficLightView> trafficLightViews; // UI视图列表
    private final PointPane pointPane;                      // 承载图形的道路面板

    private static final int MAX_TRAFFIC_LIGHTS = 9;
    private static final int INITIAL_LIGHT_POSITION = 100;
    private static final int LIGHT_POSITION_INCREMENT = 200;

    public TrafficLightController(PointPane pointPane) {
        this.pointPane = pointPane;
        this.trafficLights = new ArrayList<>();
        this.trafficLightViews = new ArrayList<>();
    }

    // =======================
    // 交通灯增删操作
    // =======================

    /**
     * 添加一个新的交通灯（最多9个）
     */
    public void addTrafficLight() {
        if (trafficLights.size() >= MAX_TRAFFIC_LIGHTS) {
            System.out.println("交通灯数量已达上限，无法添加更多！");
            return;
        }

        int positionX = INITIAL_LIGHT_POSITION + trafficLights.size() * LIGHT_POSITION_INCREMENT;
        TrafficLight trafficLight = TrafficLightFactory.createTrafficLight(positionX);
        TrafficLightView trafficLightView = new TrafficLightView(trafficLight);

        trafficLights.add(trafficLight);
        trafficLightViews.add(trafficLightView);

        LightThreadManager.submit(trafficLight); // 交通灯线程启动
        Platform.runLater(() -> pointPane.getChildren().add(trafficLightView));

        System.out.println("添加交通灯 位置X: " + positionX);
    }

    /**
     * 移除最新添加的交通灯
     */
    public void removeTrafficLight() {
        if (trafficLights.isEmpty()) {
            System.out.println("当前没有交通灯可移除！");
            return;
        }

        TrafficLight trafficLight = trafficLights.remove(trafficLights.size() - 1);
        TrafficLightView trafficLightView = trafficLightViews.remove(trafficLightViews.size() - 1);

        trafficLight.stop();
        Platform.runLater(() -> pointPane.getChildren().remove(trafficLightView));
    }

    /**
     * 清除所有交通灯与图形
     */
    public void clearAllTrafficLights() {
        for (TrafficLight light : trafficLights) {
            light.stop();
        }
        trafficLights.clear();

        Platform.runLater(() -> {
            for (TrafficLightView view : trafficLightViews) {
                pointPane.getChildren().remove(view);
            }
            trafficLightViews.clear();
        });
    }

    // =======================
    // 交通灯线程控制
    // =======================

    /** 暂停所有交通灯线程 */
    public void pauseAllLights() {
        for (TrafficLight light : trafficLights) {
            light.pause();
        }
    }

    /** 恢复所有交通灯线程 */
    public void resumeAllLights() {
        for (TrafficLight light : trafficLights) {
            light.resume();
        }
    }

    // =======================
    // 数据查询接口
    // =======================

    /** 获取当前交通灯数量 */
    public int getTrafficLightCount() {
        return trafficLights.size();
    }

    /** 获取所有交通灯对象（不可修改） */
    public List<TrafficLight> getTrafficLights() {
        return Collections.unmodifiableList(trafficLights);
    }
}
