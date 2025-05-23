package com.example.mytraffic.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigLoader：配置文件读取器
 * 采用单例模式统一管理配置加载（从 target/classes 下加载 config.properties）
 */
public class ConfigLoader {

    private static final ConfigLoader INSTANCE = new ConfigLoader();
    private final Properties properties = new Properties();

    /**
     * 私有构造函数，禁止外部实例化
     */
    private ConfigLoader() {
        loadConfig();
    }

    /**
     * 获取唯一实例
     */
    public static ConfigLoader getInstance() {
        return INSTANCE;
    }

    /**
     * 加载配置文件（从 classpath: target/classes 下）
     */
    private void loadConfig() {
        String configPath = System.getProperty("user.dir") + File.separator + "config.properties";
        try (InputStream input = new FileInputStream(configPath)) {
            properties.clear();
            properties.load(input);
        } catch (IOException e) {
            System.err.println("❌ 无法加载运行目录下配置文件：" + configPath);
        }
    }


    /**
     * 重新加载配置文件（支持动态刷新）
     */
    public void reload() {
        loadConfig();
    }

    /**
     * 获取整数类型配置
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                System.err.println("配置格式错误 (int): " + key + "=" + value);
            }
        }
        return defaultValue;
    }

    /**
     * 获取字符串类型配置
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public String getStringProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
