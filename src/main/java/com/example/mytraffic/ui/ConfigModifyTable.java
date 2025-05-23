package com.example.mytraffic.ui;

import com.example.mytraffic.util.ConfigLoader;
import com.example.mytraffic.util.ConfigUpdater;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * ConfigModifyTable：配置修改界面
 * 允许用户修改红绿灯时长配置（红灯、绿灯、黄灯）
 */
public class ConfigModifyTable {

    private final GridPane gridPane;             // 总布局
    private final TextField redDurationField;     // 红灯时长输入框
    private final TextField greenDurationField;   // 绿灯时长输入框
    private final TextField yellowDurationField;  // 黄灯时长输入框
    private final ConfigUpdater configUpdater;    // 配置文件更新工具

    public ConfigModifyTable() {
        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        configUpdater = new ConfigUpdater();

        // 创建标签和输入框
        Label redLabel = new Label("红灯时长 (ms):");
        Label greenLabel = new Label("绿灯时长 (ms):");
        Label yellowLabel = new Label("黄灯时长 (ms):");

        redDurationField = new TextField();
        greenDurationField = new TextField();
        yellowDurationField = new TextField();

        // 加载初始配置
        loadCurrentConfig();

        // 创建确认按钮传入输入参数
        Button redConfirm = createConfirmButton("LIGHT_RED_DURATION", redDurationField);
        Button greenConfirm = createConfirmButton("LIGHT_GREEN_DURATION", greenDurationField);
        Button yellowConfirm = createConfirmButton("LIGHT_YELLOW_DURATION", yellowDurationField);

        // 布局控件到GridPane
        gridPane.add(redLabel, 0, 0);
        gridPane.add(redDurationField, 1, 0);
        gridPane.add(redConfirm, 2, 0);

        gridPane.add(greenLabel, 0, 1);
        gridPane.add(greenDurationField, 1, 1);
        gridPane.add(greenConfirm, 2, 1);

        gridPane.add(yellowLabel, 0, 2);
        gridPane.add(yellowDurationField, 1, 2);
        gridPane.add(yellowConfirm, 2, 2);

        HBox wrapper = new HBox(gridPane);
        wrapper.setStyle("-fx-padding: 10;");
    }

    /**
     * 加载当前配置文件中的红绿灯时长
     */
    private void loadCurrentConfig() {
        ConfigLoader loader = ConfigLoader.getInstance();
        redDurationField.setText(String.valueOf(loader.getIntProperty("LIGHT_RED_DURATION", 7000)));
        greenDurationField.setText(String.valueOf(loader.getIntProperty("LIGHT_GREEN_DURATION", 5000)));
        yellowDurationField.setText(String.valueOf(loader.getIntProperty("LIGHT_YELLOW_DURATION", 2000)));
    }

    /**
     * 创建通用确认按钮
     */
    private Button createConfirmButton(String configKey, TextField field) {
        Button button = new Button("确认");
        button.setOnAction(e -> {
            try {
                // 第一步：获得传入参数
                String rawInput = field.getText().trim();

                // 第二步：尝试转换为整数
                int value = Integer.parseInt(rawInput);

                // 第三步：数值范围检查
                if (value <= 0) {
                    showAlert(Alert.AlertType.ERROR, "无效输入", "请输入大于0的正整数！");
                    return;
                }
                if (value > 60000) {
                    showAlert(Alert.AlertType.ERROR, "无效输入", "请输入不超过60000的数字！");
                    return;
                }

                // 第四步：合法则更新配置
                configUpdater.updateProperty(configKey, String.valueOf(value));
                showAlert(Alert.AlertType.INFORMATION, "保存成功", "配置已更新！");
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "无效输入", "请输入有效的整数！");
            }
        });
        return button;
    }


    /**
     * 弹出提示框
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * 提供给外部获取整个布局Pane
     */
    public GridPane getGridPane() {
        return gridPane;
    }
}
