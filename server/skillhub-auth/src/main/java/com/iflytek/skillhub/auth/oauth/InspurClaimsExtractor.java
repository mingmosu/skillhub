package com.iflytek.skillhub.auth.oauth;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Provider-specific claims extractor for Inspur Passport.
 */
@Component
public class InspurClaimsExtractor implements OAuthClaimsExtractor {
    @Override
    public String getProvider() {
        return "inspur-passport";
    }

    @Override
    public OAuthClaims extract(OAuth2UserRequest request, OAuth2User oAuth2User) {
        Map<String, Object> attrs = oAuth2User.getAttributes();

        Object phone = attrs.get("phone");
        String subject = String.valueOf(phone);
        String email = (String) attrs.get("mail");
        boolean emailVerified = email != null;
        String firstName = (String) attrs.get("first_name");
        String lastName = (String) attrs.get("last_name");
        String providerLogin = (firstName != null && !firstName.isBlank())
                ? firstName
                : (lastName != null && !lastName.isBlank() ? lastName : null);

        return new OAuthClaims(
                "inspur-passport",
                subject,
                email,
                emailVerified,
                providerLogin,
                attrs
        );
    }
}