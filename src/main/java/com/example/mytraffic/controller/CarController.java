package com.example.mytraffic.controller;

import com.example.mytraffic.factory.CarFactory;
import com.example.mytraffic.manager.CarThreadManager;
import com.example.mytraffic.model.Car;
import com.example.mytraffic.model.TrafficLight;
import com.example.mytraffic.view.CarView;
import com.example.mytraffic.ui.PointPane;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * CarController：车辆控制器，负责车辆的创建、删除、暂停、恢复等逻辑，
 * 并管理车辆在表格和视图中的同步展示。
 */
public class CarController {

    private static final int MAX_CARS = 50; // 最大车辆数限制

    private final List<Car> cars;                   // 所有车辆逻辑对象
    private final List<CarView> carViews;           // 所有车辆视图对象
    private final ObservableList<Car> leftCars;     // 左表格绑定数据
    private final ObservableList<Car> rightCars;    // 右表格绑定数据
    private final PointPane pointPane;              // 显示车辆的画布
    private final TrafficLightController trafficLightController; // 获取交通灯列表

    private TableView<Car> leftCarTable;            // 左车表格视图
    private TableView<Car> rightCarTable;           // 右车表格视图

    private ScheduledExecutorService tableUpdateExecutor; // 表格自动刷新任务

    public CarController(PointPane pointPane, TrafficLightController trafficLightController) {
        this.pointPane = pointPane;
        this.trafficLightController = trafficLightController;
        this.cars = new ArrayList<>();
        this.carViews = new ArrayList<>();
        this.leftCars = FXCollections.observableArrayList();
        this.rightCars = FXCollections.observableArrayList();
    }

    /**
     * 初始化左右车辆表格
     */
    public void initTables(TableView<Car> leftTable, TableView<Car> rightTable) {
        this.leftCarTable = leftTable;
        this.rightCarTable = rightTable;
    }

    /**
     * 添加一辆新车并在界面显示
     */
    public void addCar() {
        if (cars.size() >= MAX_CARS) {
            System.out.println("车辆数量已达上限！");
            return;
        }

        Car car = CarFactory.createCar(cars.size() + 1);
        car.setTrafficLights(trafficLightController.getTrafficLights());
        car.setAllCars(this.cars);

        CarView carView = new CarView(car);

        cars.add(car);
        carViews.add(carView);

        CarThreadManager.submit(car);

        System.out.println("添加车辆 ID: " + car.getId());
        Platform.runLater(() -> pointPane.getChildren().add(carView));

        updateCarTables();
    }

    /**
     * 移除最后一辆车
     */
    public void removeCar() {
        if (cars.isEmpty()) {
            System.out.println("当前没有车辆可移除！");
            return;
        }

        Car car = cars.remove(cars.size() - 1);
        CarView carView = carViews.remove(carViews.size() - 1);

        car.stop();
        Platform.runLater(() -> pointPane.getChildren().remove(carView));

        updateCarTables();
    }

    /**
     * 清除所有车辆及其视图
     */
    public void clearAllCars() {
        for (Car car : cars) {
            car.stop();
        }
        cars.clear();
        carViews.clear();

        Platform.runLater(pointPane::clear);
        updateCarTables();
    }

    /**
     * 将车辆按序分配到左右表格显示
     */
    public void updateCarTables() {
        if (leftCarTable == null || rightCarTable == null) return;

        leftCars.clear();
        rightCars.clear();

        int index = 0;
        for (Car car : cars) {
            if (index < 10) {
                leftCars.add(car);
            } else {
                rightCars.add(car);
            }
            index++;
        }

        Platform.runLater(() -> {
            leftCarTable.setItems(leftCars);
            rightCarTable.setItems(rightCars);
        });
    }

    /**
     * 暂停所有车辆线程
     */
    public void pauseAllCars() {
        for (Car car : cars) {
            car.pause();
        }
    }

    /**
     * 恢复所有车辆线程
     */
    public void resumeAllCars() {
        for (Car car : cars) {
            car.resume();
        }
    }

    /**
     * 获取当前车辆数量
     */
    public int getCarCount() {
        return cars.size();
    }

    /**
     * 启动车辆表格自动刷新器
     */
    public void startTableAutoUpdater() {
        if (tableUpdateExecutor == null || tableUpdateExecutor.isShutdown()) {
            tableUpdateExecutor = Executors.newSingleThreadScheduledExecutor();
            tableUpdateExecutor.scheduleAtFixedRate(this::updateCarTables, 0, 200, TimeUnit.MILLISECONDS);
            System.out.println("表格定时刷新器已启动");
        }
    }

    /**
     * 停止车辆表格自动刷新器
     */
    public void stopTableAutoUpdater() {
        if (tableUpdateExecutor != null && !tableUpdateExecutor.isShutdown()) {
            tableUpdateExecutor.shutdown();
            System.out.println("表格定时刷新器已停止");
        }
    }

    /**
     * 提供车辆列表用于外部访问（如导出）
     */
    public List<Car> getAllCars() {
        return cars;
    }
}
