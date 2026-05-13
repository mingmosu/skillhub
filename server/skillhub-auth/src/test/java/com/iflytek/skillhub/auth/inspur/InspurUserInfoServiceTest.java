package com.iflytek.skillhub.auth.inspur;

import com.iflytek.skillhub.auth.inspur.InspurPassportProperties;
import com.iflytek.skillhub.auth.inspur.InspurUserInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class InspurUserInfoServiceTest {

    private InspurPassportProperties properties;
    private InspurUserInfoService service;

    @BeforeEach
    void setUp() {
        properties = new InspurPassportProperties();
        properties.setAuthServer("https://id-dev.inspuronline.com");
        properties.setClientId("test-client-id");
        properties.setClientSecret("test-client-secret");
        properties.setRedirectUri("http://localhost:8080/ssologin");
        service = new InspurUserInfoService(properties, RestClient.builder());
    }

    @Test
    void testServiceConstruction() {
        // Just verify service can be constructed and methods are callable
        assertNotNull(service);
    }
}