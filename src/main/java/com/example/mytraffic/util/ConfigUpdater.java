package com.example.mytraffic.util;

import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * ConfigUpdater：配置文件写入器
 * 写入 target/classes 下的 config.properties（不推荐用于打包 JAR 后）
 */
public class ConfigUpdater {

    // 获取编译输出目录下的配置路径（target/classes）
    private static final String CONFIG_FILE = System.getProperty("user.dir") + File.separator + "config.properties";

    private final Properties properties;

    public ConfigUpdater() {
        properties = new Properties();
        loadConfig();
    }

    /**
     * 获取 target/classes 下 config.properties 的路径
     */
    private static String getTargetConfigPath() {
        try {
            URL url = ConfigUpdater.class.getClassLoader().getResource("config.properties");
            if (url == null) {
                throw new RuntimeException("❌ 未找到 config.properties 文件！");
            }
            return url.getPath().replace("%20", " ");
        } catch (Exception e) {
            throw new RuntimeException("❌ 获取 target config 路径失败！", e);
        }
    }

    /**
     * 加载配置
     */
    private void loadConfig() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.clear();
            properties.load(input);
        } catch (IOException e) {
            System.err.println("首次加载失败（可能文件尚未创建）：" + CONFIG_FILE);
        }
    }

    /**
     * 更新配置并写入
     */
    public void updateProperty(String key, String value) {
        // 每次写入前都强制加载最新配置，防止内存是旧值
        loadConfig();

        properties.setProperty(key, value);
        saveConfig();

        // 再次 reload，通知全局 ConfigLoader 更新
        ConfigLoader.getInstance().reload();
    }


    /**
     * 写入配置文件
     */
    private void saveConfig() {
        File file = new File(CONFIG_FILE);
        System.out.println("准备写入配置文件：" + file.getAbsolutePath());

        try (OutputStream output = new FileOutputStream(file)) {
            properties.store(output, "Updated config properties");
            output.flush(); // 强制刷新到磁盘
            System.out.println("✅ 配置文件保存成功！");
        } catch (IOException e) {
            System.err.println("❌ 保存配置文件失败：" + CONFIG_FILE);
            e.printStackTrace();
            return;
        }

        // ✅ 立即读取文件回显验证写入是否成功
        try (InputStream input = new FileInputStream(file)) {
            Properties check = new Properties();
            check.load(input);
            System.out.println("🧪 写入后确认 red.time = " + check.getProperty("red.time"));
            System.out.println("🧪 写入后确认 green.time = " + check.getProperty("green.time"));
            System.out.println("🧪 写入后确认 yellow.time = " + check.getProperty("yellow.time"));
            System.out.println("✅ 当前写入路径 = " + CONFIG_FILE);
            System.out.println("✅ 文件是否存在 = " + new File(CONFIG_FILE).exists());
            System.out.println("✅ 文件是否可写 = " + new File(CONFIG_FILE).canWrite());

        } catch (IOException e) {
            System.err.println("❌ 写入后验证失败！");
            e.printStackTrace();
        }
    }

}
