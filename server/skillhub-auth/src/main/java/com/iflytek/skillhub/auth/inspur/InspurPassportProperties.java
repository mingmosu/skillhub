package com.iflytek.skillhub.auth.inspur;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "astron.auth.inspur-passport")
public class InspurPassportProperties {

    private boolean enabled = false;
    private String authServer = "https://id-dev.inspuronline.com";
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String state = "ssotest";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAuthServer() {
        return authServer;
    }

    public void setAuthServer(String authServer) {
        this.authServer = authServer;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}