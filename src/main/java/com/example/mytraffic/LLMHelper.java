package com.example.mytraffic;

import com.example.mytraffic.controller.TrafficController;
import com.example.mytraffic.util.ConfigLoader;
import com.example.mytraffic.util.ConfigUpdater;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LLMHelper {
    public static void applyConfigFromLLM(String userText) {
        try {
            ProcessBuilder builder = new ProcessBuilder("python", "parse_config.py", userText);
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder jsonOutput = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonOutput.append(line);
            }

            process.waitFor();

            String result = jsonOutput.toString().trim();
            result = result.trim().replace("\uFEFF", "");

            System.out.println("🐛 模型原始返回（清理后）：" + "[" + result + "]");
            System.out.println("🐛 字符长度 = " + result.length());


            if (!result.startsWith("{")) {
                System.err.println("⚠️ 错误：返回内容不是合法 JSON，请检查 Python 输出！");
                return;
            }

            JSONObject json = new JSONObject(result);

            // 🔁 将秒转换为毫秒
            int red = json.getInt("red") * 1000;
            int green = json.getInt("green") * 1000;
            int yellow = json.getInt("yellow") * 1000;

            System.out.println("🔁 转换后时间（毫秒）：red=" + red + ", green=" + green + ", yellow=" + yellow);

            ConfigUpdater updater = new ConfigUpdater();
            updater.updateProperty("LIGHT_RED_DURATION", String.valueOf(red));
            updater.updateProperty("LIGHT_GREEN_DURATION", String.valueOf(green));
            updater.updateProperty("LIGHT_YELLOW_DURATION", String.valueOf(yellow));


            System.out.println("✅ 模型返回配置已应用（毫秒）：" + json.toString());

            System.out.println("🧪 reload 后 red = " +
                    ConfigLoader.getInstance().getIntProperty("red.time", -1));



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
