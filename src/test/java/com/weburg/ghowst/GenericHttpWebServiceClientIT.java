package com.weburg.ghowst;

import example.Engine;
import example.ExampleService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GenericHttpWebServiceClientIT {
    ExampleService testService = (ExampleService) GenericHttpWebServiceClient.newInstance("http://localhost:8081/generichttpws", ExampleService.class);

    @Test
    void createEngine() {
        Engine engine = new Engine();
        engine.setName("JavaTestEngine");
        engine.setCylinders(12);
        engine.setThrottleSetting(50);

        int engineId = testService.createEngines(engine);

        assertTrue(engineId > 0);
    }
}