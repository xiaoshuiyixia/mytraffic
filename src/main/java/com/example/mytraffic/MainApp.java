package com.example.mytraffic;

import com.example.mytraffic.controller.TrafficController;
import com.example.mytraffic.util.ConfigLoader;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * 程序启动入口
 */
public class MainApp extends Application {

    private TrafficController trafficController;  // 顶层控制器

    @Override
    public void start(Stage primaryStage) {
        configureLogging();  // 配置日志系统
        initControllers(primaryStage);  // 初始化控制器并启动界面
    }

    /**
     * 配置日志等级
     */
    private void configureLogging() {
        String logLevel = ConfigLoader.getInstance().getStringProperty("LOG_LEVEL", "INFO");
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.toLevel(logLevel));
    }

    /**
     * 初始化顶层控制器，并传递主舞台
     */
    private void initControllers(Stage primaryStage) {
        trafficController = new TrafficController();
        trafficController.init(primaryStage);
    }

    /**
     * JavaFX应用入口
     */
    public static void main(String[] args) {
        launch(args);  // 启动JavaFX应用
    }
}
