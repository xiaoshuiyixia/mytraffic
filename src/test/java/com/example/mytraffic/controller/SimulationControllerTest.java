package com.example.mytraffic.controller;

import com.example.mytraffic.controller.SimulationController;
import com.example.mytraffic.controller.CarController;
import com.example.mytraffic.controller.TrafficLightController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SimulationControllerTest {

    private SimulationController simulationController;
    private CarController mockCarController;
    private TrafficLightController mockTrafficLightController;

    @BeforeEach
    public void setUp() {
        mockCarController = mock(CarController.class);
        mockTrafficLightController = mock(TrafficLightController.class);
        simulationController = new SimulationController(mockCarController, mockTrafficLightController);
    }

    @Test
    public void testStart_shouldSetRunningAndCallResumeAll() {
        // 模拟启动前状态
        assertFalse(simulationController.isRunning());

        // 调用 start()
        simulationController.start();

        // 断言状态已更新
        assertTrue(simulationController.isRunning());
        assertFalse(simulationController.isPaused());

        // 验证依赖方法是否被调用
        verify(mockCarController, times(1)).resumeAllCars();
        verify(mockTrafficLightController, times(1)).resumeAllLights();
    }
}
