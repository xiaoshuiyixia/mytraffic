package com.example.mytraffic.observer;

import com.example.mytraffic.model.TrafficLight;

/**
 * TrafficLightObserver：交通灯观察者接口
 * 供TrafficLight模型在状态变化时通知观察者（比如TrafficLightView界面刷新）
 */
public interface TrafficLightObserver {

    /**
     * 当TrafficLight对象状态更新时调用
     *
     * @param trafficLight 被更新的TrafficLight实例
     */
    void onTrafficLightUpdated(TrafficLight trafficLight);
}
