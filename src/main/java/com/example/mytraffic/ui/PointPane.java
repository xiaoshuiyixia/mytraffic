package com.example.mytraffic.ui;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * PointPane：道路绘制面板
 * 负责绘制静态背景（道路、位置标记）
 * 并作为动态添加车辆和交通灯View的容器
 */
public class PointPane extends Pane {

    private final Rectangle[] roads = new Rectangle[3]; // 三个车道
    private final Line[] dividers = new Line[2];        // 两条分隔线

    /**
     * 构造方法：初始化三车道和位置标记
     */
    public PointPane() {
        // 设置Pane大小（高度根据三车道+空白设为200）
        setPrefSize(1500, 200);

        // 初始化三条道路
        initializeRoads();

        // 初始化位置标记
        initializePositionMarkers();
    }

    /**
     * 初始化三条车道和分隔线
     */
    private void initializeRoads() {
        int baseY = 70;
        int laneHeight = 40;

        for (int i = 0; i < 3; i++) {
            Rectangle lane = new Rectangle(0, baseY + i * laneHeight, 1500, laneHeight);
            lane.setFill(Color.LIGHTGRAY);
            roads[i] = lane;
            getChildren().add(lane);

            // 添加分隔线（第1和第2条之间）
            if (i > 0) {
                Line divider = new Line(0, baseY + i * laneHeight, 1500, baseY + i * laneHeight);
                divider.setStroke(Color.BLACK);
                divider.setStrokeWidth(2);
                dividers[i - 1] = divider;
                getChildren().add(divider);
            }
        }
    }


    /**
     * 初始化道路上的里程标记（每1000m一个）
     */
    private void initializePositionMarkers() {
        double markerSpacing = 100;  // 每100像素一个标记（代表1000米）
        double totalWidth = getPrefWidth();  // 当前面板宽度
        int markerCount = (int) (totalWidth / markerSpacing); // 适配面板宽度，自动算出标记数

        for (int i = 1; i <= markerCount; i++) {
            Text positionMarker = new Text(i * 1000 + "m");
            positionMarker.setFont(Font.font("Arial", 13));
            positionMarker.setX(i * markerSpacing - 18);  // 微调对齐
            positionMarker.setY(200);  // 更低的位置，防止重叠车道
            getChildren().add(positionMarker);
        }
    }


    /**
     * 清除所有动态对象（车辆、交通灯等），保留三车道与位置标记
     */
    public void clear() {
        getChildren().clear();

        // 恢复三车道和分隔线
        for (Rectangle road : roads) getChildren().add(road);
        for (Line divider : dividers) getChildren().add(divider);

        // 恢复标记
        initializePositionMarkers();
    }
}
