package com.example.mytraffic.factory;

import com.example.mytraffic.model.TrafficLight;

/**
 * TrafficLightFactory：负责创建交通灯实例
 * 统一管理交通灯的生成逻辑
 */
public class TrafficLightFactory {

    private TrafficLightFactory() {
        // 私有构造器，防止外部实例化
    }

    /**
     * 创建一个新的TrafficLight对象
     *
     * @param positionX 交通灯横向位置（像素坐标）
     * @return 新创建的TrafficLight实例
     */
    public static TrafficLight createTrafficLight(int positionX) {
        return new TrafficLight(positionX);
    }


}
