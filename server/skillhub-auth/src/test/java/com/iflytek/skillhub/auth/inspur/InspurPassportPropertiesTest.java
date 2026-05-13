package com.iflytek.skillhub.auth.inspur;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InspurPassportPropertiesTest {

    @Test
    void testDefaultValues() {
        var props = new InspurPassportProperties();
        assertFalse(props.isEnabled());
        assertEquals("https://id-dev.inspuronline.com", props.getAuthServer());
        assertEquals("ssotest", props.getState());
    }

    @Test
    void testSetters() {
        var props = new InspurPassportProperties();
        props.setEnabled(true);
        props.setAuthServer("https://id-prod.inspuronline.com");
        props.setClientId("test-client-id");
        props.setClientSecret("test-secret");
        props.setRedirectUri("http://localhost:8080/ssologin");
        props.setState("custom-state");

        assertTrue(props.isEnabled());
        assertEquals("https://id-prod.inspuronline.com", props.getAuthServer());
        assertEquals("test-client-id", props.getClientId());
        assertEquals("test-secret", props.getClientSecret());
        assertEquals("http://localhost:8080/ssologin", props.getRedirectUri());
        assertEquals("custom-state", props.getState());
    }
}