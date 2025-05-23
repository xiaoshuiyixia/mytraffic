module com.example.mytraffic {
    // JavaFX 模块
    requires javafx.controls;    // JavaFX 控件模块
    requires javafx.fxml;         // JavaFX FXML模块
    requires javafx.base;         // JavaFX 基础模块（属性绑定等）

    // 其他依赖库
    requires org.slf4j;           // 日志库 SLF4J
    requires ch.qos.logback.classic;
    requires org.json; // Logback实现

    // 允许 FXML 和 JavaFX 反射访问
    opens com.example.mytraffic to javafx.fxml;
    opens com.example.mytraffic.controller to javafx.fxml;
    opens com.example.mytraffic.model to javafx.base, javafx.controls;
    opens com.example.mytraffic.ui to javafx.base, javafx.controls;
    opens com.example.mytraffic.view to javafx.base, javafx.controls;
    opens com.example.mytraffic.observer to javafx.base;

    // 导出各功能包
    exports com.example.mytraffic;
    exports com.example.mytraffic.controller;
    exports com.example.mytraffic.model;
    exports com.example.mytraffic.ui;
    exports com.example.mytraffic.view;
    exports com.example.mytraffic.factory;
    exports com.example.mytraffic.manager;
    exports com.example.mytraffic.util;
    exports com.example.mytraffic.observer;
}
