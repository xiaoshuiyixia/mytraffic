package com.example.mytraffic.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigUpdaterTest {

    @Test
    public void testUpdateProperty_shouldAffectLoader() {
        ConfigUpdater updater = new ConfigUpdater();
        String testKey = "LIGHT_RED_DURATION";
        String testValue = "8888";

        updater.updateProperty(testKey, testValue);

        int loaded = ConfigLoader.getInstance().getIntProperty(testKey, -1);
        assertEquals(8888, loaded);
    }
}
