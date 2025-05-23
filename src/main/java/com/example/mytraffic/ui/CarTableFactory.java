package com.example.mytraffic.ui;

import com.example.mytraffic.model.Car;
import com.example.mytraffic.util.ConfigLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * CarTableFactory：负责统一创建车辆信息表格
 * 供界面模块使用，标准化车辆表格样式
 */
public class CarTableFactory {

    // 从配置加载表格属性
    private static final int ROW_HEIGHT;
    private static final int HEADER_HEIGHT;
    private static final int ROW_COUNT;

    static {
        ConfigLoader loader = ConfigLoader.getInstance();
        ROW_HEIGHT = loader.getIntProperty("ROW_HEIGHT", 25);         // 默认行高
        HEADER_HEIGHT = loader.getIntProperty("HEADER_HEIGHT", 28);   // 表头高
        ROW_COUNT = loader.getIntProperty("ROW_COUNT", 10);           // 显示行数
    }

    private CarTableFactory() {
        // 私有构造器，禁止实例化
    }

    /**
     * 创建一个车辆信息表格
     *
     * @return 配置好的TableView<Car>
     */
    public static TableView<Car> createCarTable() {
        TableView<Car> tableView = new TableView<>();

        // 设置固定行高
        tableView.setFixedCellSize(ROW_HEIGHT);

        // 设置表格总高度
        double totalHeight = HEADER_HEIGHT + ROW_HEIGHT * ROW_COUNT;
        tableView.setPrefHeight(totalHeight);

        // 创建列
        TableColumn<Car, Integer> carNumberCol = new TableColumn<>("车牌号");
        carNumberCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        carNumberCol.setPrefWidth(100);

        TableColumn<Car, Integer> speedCol = new TableColumn<>("车速 (像素/秒)");
        speedCol.setCellValueFactory(new PropertyValueFactory<>("speed"));
        speedCol.setPrefWidth(130);

        TableColumn<Car, Double> positionCol = new TableColumn<>("位置 (px)");
        positionCol.setCellValueFactory(new PropertyValueFactory<>("positionX"));
        positionCol.setPrefWidth(130);

        // 添加列到表格
        tableView.getColumns().addAll(carNumberCol, speedCol, positionCol);

        return tableView;
    }
}
