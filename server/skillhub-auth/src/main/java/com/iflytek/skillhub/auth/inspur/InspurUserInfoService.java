package com.iflytek.skillhub.auth.inspur;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iflytek.skillhub.auth.inspur.InspurPassportProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class InspurUserInfoService {

    private final InspurPassportProperties properties;
    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public InspurUserInfoService(InspurPassportProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.restClient = restClientBuilder.build();
    }

    public String exchangeToken(String code) {
        String clientIdAndSecret = properties.getClientId() + ":" + properties.getClientSecret();
        String encoded = Base64.getEncoder().encodeToString(clientIdAndSecret.getBytes(StandardCharsets.UTF_8));

        String url = properties.getAuthServer() + "/oauth2.0/token";
        String redirectUri = properties.getRedirectUri();

        String response = restClient.post()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Basic " + encoded)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .body("grant_type=authorization_code&code=" + code + "&redirect_uri=" + redirectUri)
            .retrieve()
            .body(String.class);

        try {
            Map<String, Object> tokenData = objectMapper.readValue(response, Map.class);
            return (String) tokenData.get("access_token");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse token response", e);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getUserInfo(String accessToken) {
        String url = properties.getAuthServer() + "/oauth2.0/user-info";

        String response = restClient.get()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .body(String.class);

        try {
            return objectMapper.readValue(response, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse user-info response", e);
        }
    }
}