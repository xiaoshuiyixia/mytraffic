package com.example.mytraffic.view;

import com.example.mytraffic.model.Car;
import com.example.mytraffic.observer.CarObserver;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * CarView：车辆图形视图，负责将 Car 模型以图形形式展示在画布上。
 * 通过观察者模式监听 Car 的位置和车道变化，动态更新界面。
 */
public class CarView extends Group implements CarObserver {

    private final Car car;          // 绑定的车辆模型对象
    private final Rectangle body;   // 车辆矩形形状
    private final Text carNumber;   // 显示车辆编号的文字

    public CarView(Car car) {
        this.car = car;

        // 初始化车身矩形
        body = new Rectangle(30, 20, Color.DARKBLUE);
        body.setY(calculateY(car.getLaneIndex()));

        // 初始化车辆编号文字
        carNumber = new Text(String.valueOf(car.getId()));
        carNumber.setFill(Color.ORANGE);
        carNumber.setY(calculateY(car.getLaneIndex()) + 20);

        // 添加图形到 Group
        getChildren().addAll(body, carNumber);

        // 注册为 Car 的观察者，实现位置更新回调
        car.registerObserver(this);

        // 初始化图形位置
        updatePosition();
    }

    /**
     * 当 Car 状态更新时调用，刷新图形位置
     */
    @Override
    public void onCarUpdated(Car car) {
        updatePosition();
    }

    /**
     * 根据 Car 的位置和车道更新图形位置
     */
    private void updatePosition() {
        double x = car.getPositionX();
        int laneY = calculateY(car.getLaneIndex());
        Platform.runLater(() -> {
            body.setX(x);
            body.setY(laneY);
            carNumber.setX(x + 8);
            carNumber.setY(laneY + 20);
        });
    }

    /**
     * 根据车道编号计算对应Y轴位置
     */
    private int calculateY(int laneIndex) {
        int baseY = 70;
        int laneHeight = 40;
        return baseY + laneIndex * laneHeight;
    }
}
