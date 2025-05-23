package com.example.mytraffic.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoaderTest {

    @Test
    public void testLoadDefaultRedDuration() {
        ConfigLoader loader = ConfigLoader.getInstance();
        int red = loader.getIntProperty("LIGHT_RED_DURATION", 7000);
        assertTrue(red >= 1000 && red <= 60000, "红灯时间应在合理范围内");
    }

    @Test
    public void testMissingPropertyReturnsDefault() {
        ConfigLoader loader = ConfigLoader.getInstance();
        int result = loader.getIntProperty("NON_EXISTENT_KEY", 1234);
        assertEquals(1234, result);
    }
}
