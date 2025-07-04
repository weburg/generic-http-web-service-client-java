package com.weburg.ghowst;

import example.Engine;
import example.ExampleService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenericHttpWebServiceClientTest {
    ExampleService testService = (ExampleService) GenericHttpWebServiceClient.newInstance("http://nohost/noservice", ExampleService.class);

    @Test
    void serviceException() {
        Engine engine = new Engine();
        engine.setName("JavaTestEngine");
        engine.setCylinders(12);
        engine.setThrottleSetting(50);

        assertThrowsExactly(HttpWebServiceException.class, () -> {
            testService.createEngines(engine);
        });
    }
}