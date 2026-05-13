package com.iflytek.skillhub.auth.oauth;

import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * Provider-specific claims extractor for Inspur Passport.
 */
@Component
public class InspurClaimsExtractor implements ClaimsExtractor {

    @Override
    public String getProvider() {
        return "inspur";
    }

    @Override
    public OAuthClaims extract(Map<String, Object> userInfo) {
        String subject = String.valueOf(userInfo.getOrDefault("sub", ""));
        String email = (String) userInfo.get("email");
        boolean emailVerified = email != null;
        String firstName = (String) userInfo.get("first_name");
        String lastName = (String) userInfo.get("last_name");
        String providerLogin = (firstName != null && lastName != null)
            ? lastName + firstName
            : (firstName != null ? firstName : (lastName != null ? lastName : null));

        return new OAuthClaims(
            "inspur",
            subject,
            email,
            emailVerified,
            providerLogin,
            userInfo
        );
    }
}