package com.iflytek.skillhub.auth.oauth;

import java.util.Map;

/**
 * Claims extractor for provider-specific user info maps.
 */
public interface ClaimsExtractor {
    String getProvider();
    OAuthClaims extract(Map<String, Object> userInfo);
}