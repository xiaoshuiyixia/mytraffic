package com.example.mytraffic.view;

import com.example.mytraffic.model.TrafficLight;
import com.example.mytraffic.observer.TrafficLightObserver;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * TrafficLightView：交通灯图形视图，负责显示交通灯圆圈、倒计时和三车道斑马线。
 * 通过观察者模式监听 TrafficLight 模型状态变化，并更新界面显示。
 */
public class TrafficLightView extends Group implements TrafficLightObserver {

    private final TrafficLight trafficLight;            // 绑定的交通灯模型
    private final Circle lightCircle;                   // 灯体圆圈
    private final Line centerLine;                      // 灯杆中心线
    private final Label countdownLabel;                 // 倒计时显示文本
    private final List<Rectangle> zebraLines;           // 三车道斑马线图形集合

    public TrafficLightView(TrafficLight trafficLight) {
        this.trafficLight = trafficLight;
        this.zebraLines = new ArrayList<>();

        // 初始化灯体
        lightCircle = new Circle(trafficLight.getPositionX(), 50, 13, Color.RED);

        // 初始化灯杆
        centerLine = new Line(trafficLight.getPositionX(), 50, trafficLight.getPositionX(), 100);
        centerLine.setStrokeWidth(2);
        centerLine.setStroke(Color.RED);

        // 初始化倒计时文本
        countdownLabel = new Label();
        countdownLabel.setLayoutX(trafficLight.getPositionX() - 10);
        countdownLabel.setLayoutY(20);

        // 创建斑马线
        createZebraLines(trafficLight.getPositionX());

        // 添加所有图形元素到 Group
        getChildren().addAll(zebraLines);
        getChildren().addAll(lightCircle, centerLine, countdownLabel);

        // 注册观察者监听交通灯变化
        trafficLight.registerObserver(this);

        // 初始刷新一次视图
        updateView();
    }

    /**
     * 创建覆盖三车道区域的白色斑马线条
     * @param centerX 交通灯中心 X 坐标
     */
    private void createZebraLines(double centerX) {
        double stripeHeight = 5;         // 每条斑马线的高度
        double gap = 5;                  // 每条之间的间距
        double totalHeight = 120;        // 三车道垂直总高度
        double stripeWidth = 15;         // 每条斑马线宽度

        int stripeCount = (int) ((totalHeight + gap) / (stripeHeight + gap));
        double startY = 70; // 起始 Y 坐标

        for (int i = 0; i < stripeCount; i++) {
            Rectangle stripe = new Rectangle();
            stripe.setX(centerX - stripeWidth / 2);
            stripe.setY(startY + i * (stripeHeight + gap));
            stripe.setWidth(stripeWidth);
            stripe.setHeight(stripeHeight);
            stripe.setFill(Color.WHITE);
            zebraLines.add(stripe);
        }
    }

    /**
     * 交通灯模型发生变化时的回调方法
     */
    @Override
    public void onTrafficLightUpdated(TrafficLight trafficLight) {
        updateView();
    }

    /**
     * 根据交通灯当前颜色和倒计时刷新界面状态
     */
    private void updateView() {
        Color color = trafficLight.getCurrentColor();
        int countdownMs = trafficLight.getCountdownMs();

        Platform.runLater(() -> {
            lightCircle.setFill(color);
            centerLine.setStroke(color);
            double seconds = Math.max(countdownMs, 0) / 1000.0;
            countdownLabel.setText(String.format("%.1f", seconds));
        });
    }

    // ========= 外部访问接口 =========

    public Circle getLightCircle() {
        return lightCircle;
    }

    public Line getCenterLine() {
        return centerLine;
    }

    public Label getCountdownLabel() {
        return countdownLabel;
    }
}
