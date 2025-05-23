package com.example.mytraffic.controller;

import com.example.mytraffic.manager.CarThreadManager;
import com.example.mytraffic.manager.LightThreadManager;
import com.example.mytraffic.model.Car;
import com.example.mytraffic.ui.PointPane;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.List;

/**
 * TrafficController：顶层控制器，统一调度所有子控制器，协调 UI、模拟状态、车辆与交通灯的联动。
 */
public class TrafficController {

    private CarController carController;
    private TrafficLightController trafficLightController;
    private SimulationController simulationController;
    private UIController uiController;
    private PointPane pointPane;
    private Stage primaryStage;

    public TrafficController() {
        CarThreadManager.init();
        LightThreadManager.init();
    }

    // =======================
    // 初始化方法
    // =======================

    /**
     * 初始化主界面及控制模块
     */
    public void init(Stage primaryStage) {
        this.primaryStage = primaryStage;

        pointPane = new PointPane();
        trafficLightController = new TrafficLightController(pointPane);
        carController = new CarController(pointPane, trafficLightController);

        uiController = new UIController(this);
        simulationController = new SimulationController(carController, trafficLightController);

        setupScene();
    }

    /**
     * 构建并展示主窗口
     */
    private void setupScene() {
        AnchorPane root = uiController.buildMainLayout(pointPane);
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("交通模拟器");

        primaryStage.setOnCloseRequest(event -> {
            shutdown();
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();
    }

    // =======================
    // 模拟控制方法（按钮绑定）
    // =======================

    /** 启动模拟 */
    public void startSimulation() {
        simulationController.start();
        uiController.enablePauseButton();
        carController.startTableAutoUpdater();
    }

    /** 暂停或继续模拟 */
    public void togglePauseSimulation() {
        if (!simulationController.isRunning()) return;

        simulationController.togglePause();
        if (simulationController.isPaused()) {
            uiController.setPauseButtonText("继续");
        } else {
            uiController.setPauseButtonText("暂停");
        }
    }

    /** 判断是否正在模拟中 */
    public boolean isSimulationRunning() {
        return simulationController.isRunning();
    }

    /** 重置模拟状态 */
    public void resetSimulation() {
        simulationController.stop();
        carController.stopTableAutoUpdater();
        uiController.updateCarCount();
        uiController.updateTrafficLightCount();
    }

    /** 停止模拟并恢复UI初始状态 */
    public void stopSimulation() {
        simulationController.stop();
        carController.stopTableAutoUpdater();
        uiController.enableStartButton();
        uiController.setPauseButtonText("暂停");
        uiController.disablePauseButton();
    }

    // =======================
    // 控制操作方法（UI按钮）
    // =======================

    public void addCar() {
        carController.addCar();
        uiController.updateCarCount();
    }

    public void removeCar() {
        carController.removeCar();
        uiController.updateCarCount();
    }

    public void addTrafficLight() {
        trafficLightController.addTrafficLight();
        uiController.updateTrafficLightCount();
    }

    public void removeTrafficLight() {
        trafficLightController.removeTrafficLight();
        uiController.updateTrafficLightCount();
    }

    // =======================
    // 表格与统计信息接口
    // =======================

    public void initCarTables(TableView<Car> leftTable, TableView<Car> rightTable) {
        carController.initTables(leftTable, rightTable);
    }

    public int getCarCount() {
        return carController.getCarCount();
    }

    public int getTrafficLightCount() {
        return trafficLightController.getTrafficLightCount();
    }

    public List<Car> getAllCars() {
        return carController.getAllCars();
    }

    // =======================
    // 应用关闭
    // =======================

    /** 关闭模拟并关闭线程池 */
    private void shutdown() {
        simulationController.stop();
        CarThreadManager.shutdown();
        LightThreadManager.shutdown();
    }
}
