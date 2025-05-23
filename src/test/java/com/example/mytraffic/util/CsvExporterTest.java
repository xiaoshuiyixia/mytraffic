package com.example.mytraffic.util;

import com.example.mytraffic.model.Car;
import com.example.mytraffic.util.CsvExporter;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CsvExporterTest {

    @Test
    public void testExportRowFormat() {
        Car car = new Car(1);
        car.setLaneIndex(2);
        String row = car.getCsvRow();
        assertTrue(row.startsWith("1,2,")); // ID 和车道号
        assertTrue(row.contains(","));     // 确保字段分隔符存在
    }

    @Test
    public void testExport_shouldNotThrowException() {
        List<Car> cars = Collections.singletonList(new Car(1));
        assertDoesNotThrow(() -> {
            CsvExporter.export(cars, "test_output.csv"); // 注意：此处为演示，可模拟写入虚拟路径
        });
    }
}
