package com.weburg.ghowst;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenericHttpWebServiceClientTest {
    TestService testService = (TestService) GenericHttpWebServiceClient.newInstance("http://nohost/noservice", TestService.class);

    @Test
    void createTestResource() {
        assertThrowsExactly(HttpWebServiceException.class, () -> {
            testService.createResource(new TestResource());
        });
    }
}