package com.example.mytraffic.controller;

import com.example.mytraffic.LLMHelper;
import com.example.mytraffic.ui.ConfigModifyTable;
import com.example.mytraffic.ui.PointPane;
import com.example.mytraffic.ui.CarTableFactory;
import com.example.mytraffic.model.Car;
import com.example.mytraffic.util.CsvExporter;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * UIController：界面控制器，负责构建主界面布局，初始化按钮和表格，并绑定业务逻辑。
 */
public class UIController {

    private final TrafficController trafficController;

    private Button startButton;
    private Button pauseContinueButton;
    private Button addCarButton;
    private Button removeCarButton;
    private Button addLightButton;
    private Button removeLightButton;
    private Button exportButton;
    private Button exitButton;

    private Label carCountLabel;
    private Label trafficLightCountLabel;

    private TableView<Car> leftCarTable;
    private TableView<Car> rightCarTable;

    private ConfigModifyTable configModifyTable;

    private TextField llmInputField;
    private Button applyLLMConfigButton;

    public UIController(TrafficController trafficController) {
        this.trafficController = trafficController;
    }

    /**
     * 构建主界面布局并组装各组件（表格、按钮、配置区）
     */
    public AnchorPane buildMainLayout(PointPane pointPane) {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(20);
        gridPane.setHgap(20);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(60);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(40);
        gridPane.getColumnConstraints().addAll(col1, col2);

        initTables();
        initButtons();

        VBox titleBox = new VBox(10, new Label("交通模拟系统"), new Label("Powered by JavaFX MVC"));
        titleBox.setAlignment(Pos.CENTER);

        HBox lightControlBox = new HBox(10, trafficLightCountLabel, addLightButton, removeLightButton);
        lightControlBox.setAlignment(Pos.CENTER);

        HBox carControlBox = new HBox(10, carCountLabel, addCarButton, removeCarButton);
        carControlBox.setAlignment(Pos.CENTER);

        HBox simulationControlBox = new HBox(20, startButton, pauseContinueButton, exportButton, exitButton);
        simulationControlBox.setAlignment(Pos.CENTER);

        HBox carTablesBox = new HBox(10, leftCarTable, rightCarTable);
        carTablesBox.setAlignment(Pos.CENTER);

        configModifyTable = new ConfigModifyTable();

        VBox llmBox = new VBox(10, new Label("自然语言配置："), llmInputField, applyLLMConfigButton);
        llmBox.setAlignment(Pos.CENTER_LEFT);


        gridPane.add(titleBox, 0, 0);
        gridPane.add(lightControlBox, 0, 1);
        gridPane.add(carControlBox, 0, 2);
        gridPane.add(simulationControlBox, 0, 3);
        gridPane.add(pointPane, 0, 4);
        gridPane.add(carTablesBox, 0, 5);
        gridPane.add(configModifyTable.getGridPane(), 1, 1);
        gridPane.add(llmBox, 1, 2);

        AnchorPane root = new AnchorPane(gridPane);
        AnchorPane.setTopAnchor(gridPane, 10.0);
        AnchorPane.setLeftAnchor(gridPane, 10.0);
        AnchorPane.setRightAnchor(gridPane, 10.0);
        AnchorPane.setBottomAnchor(gridPane, 10.0);

        return root;
    }

    /** 初始化车辆表格并交由 TrafficController 接管 */
    private void initTables() {
        leftCarTable = CarTableFactory.createCarTable();
        rightCarTable = CarTableFactory.createCarTable();
        trafficController.initCarTables(leftCarTable, rightCarTable);
    }

    /** 初始化所有界面按钮并绑定事件逻辑 */
    private void initButtons() {
        startButton = createButton("开始", e -> {
            if (!trafficController.isSimulationRunning()) {
                trafficController.startSimulation();
                startButton.setText("重新开始");
                updateCarCount();
                updateTrafficLightCount();
                enablePauseButton();
            } else {
                trafficController.resetSimulation();
                startButton.setText("开始");
                updateCarCount();
                updateTrafficLightCount();
                disablePauseButton();
                setPauseButtonText("暂停");
            }
        });

        pauseContinueButton = createButton("暂停", e -> trafficController.togglePauseSimulation());
        pauseContinueButton.setDisable(true);

        addCarButton = createButton("添加车辆", e -> {
            trafficController.addCar();
        });

        removeCarButton = createButton("移除车辆", e -> {
            trafficController.removeCar();
        });

        addLightButton = createButton("添加交通灯", e -> {
            trafficController.addTrafficLight();
        });

        removeLightButton = createButton("移除交通灯", e -> {
            trafficController.removeTrafficLight();
        });

        exportButton = createButton("导出CSV", e -> {
            String filename = "output/car_data_" + System.currentTimeMillis() + ".csv";
            CsvExporter.export(trafficController.getAllCars(), filename);
        });

        exitButton = createButton("退出", e -> {
            trafficController.stopSimulation();
            System.exit(0);
        });

        carCountLabel = new Label("车辆数量: 0");
        trafficLightCountLabel = new Label("交通灯数量: 0");

        llmInputField = new TextField();
        llmInputField.setPromptText("例如：我希望绿灯长一点，红灯短一些");
        applyLLMConfigButton = createButton("应用智能配置", e -> {
            String input = llmInputField.getText();
            if (input != null && !input.isEmpty()) {
                LLMHelper.applyConfigFromLLM(input);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "配置已更新！");
                alert.show();
            }
        });
    }

    /** 工具方法：构建统一样式按钮 */
    private Button createButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button(text);
        button.setPrefWidth(100);
        button.setOnAction(action);
        return button;
    }

    /** 更新车辆数量标签 */
    public void updateCarCount() {
        carCountLabel.setText("车辆数量: " + trafficController.getCarCount());
    }

    /** 更新交通灯数量标签 */
    public void updateTrafficLightCount() {
        trafficLightCountLabel.setText("交通灯数量: " + trafficController.getTrafficLightCount());
    }

    /** 启用暂停按钮 */
    public void enablePauseButton() {
        pauseContinueButton.setDisable(false);
    }

    /** 禁用暂停按钮 */
    public void disablePauseButton() {
        pauseContinueButton.setDisable(true);
    }

    /** 启用开始按钮 */
    public void enableStartButton() {
        startButton.setDisable(false);
    }

    /** 设置暂停按钮的显示文本 */
    public void setPauseButtonText(String text) {
        pauseContinueButton.setText(text);
    }
}
