package com.iflytek.skillhub.auth.inspur;

import com.iflytek.skillhub.auth.inspur.InspurPassportController;
import com.iflytek.skillhub.auth.inspur.InspurPassportProperties;
import com.iflytek.skillhub.auth.inspur.InspurUserInfoService;
import com.iflytek.skillhub.auth.oauth.ClaimsExtractor;
import com.iflytek.skillhub.auth.oauth.OAuthClaims;
import com.iflytek.skillhub.auth.policy.AccessPolicy;
import com.iflytek.skillhub.auth.policy.AccessDecision;
import com.iflytek.skillhub.auth.identity.IdentityBindingService;
import com.iflytek.skillhub.auth.rbac.PlatformPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import java.util.Map;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class InspurPassportControllerTest {

    private InspurPassportProperties properties;
    private InspurUserInfoService userInfoService;
    private ClaimsExtractor claimsExtractor;
    private AccessPolicy accessPolicy;
    private IdentityBindingService identityBindingService;
    private InspurPassportController controller;

    @BeforeEach
    void setUp() {
        properties = new InspurPassportProperties();
        properties.setEnabled(true);
        properties.setAuthServer("https://id-dev.inspuronline.com");
        properties.setClientId("test-client-id");
        properties.setClientSecret("test-client-secret");
        properties.setRedirectUri("http://localhost:8080/ssologin");
        properties.setState("ssotest");

        userInfoService = mock(InspurUserInfoService.class);
        claimsExtractor = mock(ClaimsExtractor.class);
        accessPolicy = mock(AccessPolicy.class);
        identityBindingService = mock(IdentityBindingService.class);

        controller = new InspurPassportController(
            properties, userInfoService, claimsExtractor, accessPolicy, identityBindingService
        );
    }

    @Test
    void testGetLoginUrlGeneratesCorrectRedirect() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.getLoginUrl(request, response);

        String redirectUrl = response.getRedirectedUrl();
        assertTrue(redirectUrl.contains("https://id-dev.inspuronline.com/oauth2.0/authorize"));
        assertTrue(redirectUrl.contains("response_type=code"));
        assertTrue(redirectUrl.contains("client_id=test-client-id"));
        assertTrue(redirectUrl.contains("redirect_uri="));
        assertTrue(redirectUrl.contains("state=ssotest"));
    }

    @Test
    void testSsoLoginSuccess() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(userInfoService.exchangeToken("auth-code-123")).thenReturn("test-access-token");
        when(userInfoService.getUserInfo("test-access-token")).thenReturn(
            Map.of("sub", "user-123", "email", "test@inspur.com", "first_name", "San", "last_name", "Zhang")
        );
        when(claimsExtractor.extract(any())).thenReturn(
            new OAuthClaims("inspur", "user-123", "test@inspur.com", true, "San Zhang", Map.of())
        );
        when(accessPolicy.evaluate(any())).thenReturn(AccessDecision.ALLOW);
        when(identityBindingService.bindOrCreate(any(), any())).thenReturn(
            new PlatformPrincipal("user-123", "San Zhang", "test@inspur.com", null, "inspur", Set.of())
        );

        controller.ssoLogin("auth-code-123", "ssotest", request, response);

        assertEquals(302, response.getStatus());
        assertEquals("/", response.getRedirectedUrl());
    }

    @Test
    void testSsoLoginStateMismatch() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.ssoLogin("auth-code-123", "wrong-state", request, response);

        assertEquals(302, response.getStatus());
        assertEquals("/access-denied", response.getRedirectedUrl());
    }
}