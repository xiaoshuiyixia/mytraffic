package com.example.mytraffic.factory;

import com.example.mytraffic.model.Car;

import java.util.Random;

/**
 * CarFactory：负责创建车辆实例。
 * 通过统一工厂方法管理车辆编号和初始车道分配逻辑。
 */
public class CarFactory {

    // 私有构造器，防止实例化
    private CarFactory() {}

    /**
     * 创建一个新的 Car 实例，并随机分配车道。
     *
     * @param id 车辆编号（唯一）
     * @return 新建的 Car 实例
     */
    public static Car createCar(int id) {
        Car car = new Car(id);

        // 为车辆随机分配一个车道（0=上车道，1=中车道，2=下车道）
        int laneIndex = new Random().nextInt(3);
        car.setLaneIndex(laneIndex);

        return car;
    }
}
