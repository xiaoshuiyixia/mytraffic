package com.example.mytraffic.observer;

import com.example.mytraffic.model.Car;

/**
 * CarObserver：车辆观察者接口
 * 供Car模型在数据变化时通知观察者（比如CarView界面刷新）
 */
public interface CarObserver {

    /**
     * 当Car对象状态更新时调用
     *
     * @param car 被更新的Car实例
     */
    void onCarUpdated(Car car);
}
