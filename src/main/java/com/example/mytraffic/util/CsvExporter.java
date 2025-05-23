package com.example.mytraffic.util;

import com.example.mytraffic.model.Car;
import javafx.scene.control.Alert;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * CsvExporter：用于导出车辆统计信息到 CSV 文件
 */
public class CsvExporter {

    /**
     * 导出车辆数据到 CSV 文件
     * @param cars 所有车辆列表
     * @param filename 输出文件路径（支持如 output/car_data_时间戳.csv）
     */
    public static void export(List<Car> cars, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("CarID,LaneIndex,EnterTime,ExportTime,PositionX,Speed,WasBlocked,TotalWaitTime");
            for (Car car : cars) {
                writer.println(car.getCsvRow());
            }

            System.out.println("✅ CSV 导出成功：" + filename);

            // ✅ 弹出提示框
            javafx.application.Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("导出成功");
                alert.setHeaderText("车辆数据已导出！");
                alert.setContentText("文件路径：\n" + filename);
                alert.showAndWait();
            });

        } catch (IOException e) {
            System.err.println("❌ CSV 导出失败：" + e.getMessage());

            // ⛔ 出错也弹窗提示
            javafx.application.Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("导出失败");
                alert.setHeaderText("写入文件时发生错误");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            });
        }

    }
}
