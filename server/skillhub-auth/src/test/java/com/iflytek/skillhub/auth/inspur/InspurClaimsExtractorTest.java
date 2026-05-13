package com.iflytek.skillhub.auth.inspur;

import com.iflytek.skillhub.auth.oauth.ClaimsExtractor;
import com.iflytek.skillhub.auth.oauth.InspurClaimsExtractor;
import com.iflytek.skillhub.auth.oauth.OAuthClaims;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class InspurClaimsExtractorTest {

    private final ClaimsExtractor extractor = new InspurClaimsExtractor();

    @Test
    void testProviderName() {
        assertEquals("inspur", extractor.getProvider());
    }

    @Test
    void testExtractWithAllFields() {
        Map<String, Object> userInfo = Map.of(
            "sub", "user-uuid-123",
            "email", "zhangsan@inspur.com",
            "first_name", "三",
            "last_name", "张"
        );

        OAuthClaims claims = extractor.extract(userInfo);

        assertEquals("inspur", claims.provider());
        assertEquals("user-uuid-123", claims.subject());
        assertEquals("zhangsan@inspur.com", claims.email());
        assertTrue(claims.emailVerified());
        assertEquals("张三", claims.providerLogin());
        assertEquals("三", claims.extra().get("first_name"));
        assertEquals("张", claims.extra().get("last_name"));
    }

    @Test
    void testExtractWithMissingFields() {
        Map<String, Object> userInfo = Map.of(
            "sub", "user-uuid-456",
            "email", "lisi@inspur.com"
        );

        OAuthClaims claims = extractor.extract(userInfo);

        assertEquals("inspur", claims.provider());
        assertEquals("user-uuid-456", claims.subject());
        assertEquals("lisi@inspur.com", claims.email());
        assertTrue(claims.emailVerified());
        assertNull(claims.providerLogin());
    }

    @Test
    void testExtractWithNoEmail() {
        Map<String, Object> userInfo = Map.of(
            "sub", "user-uuid-789"
        );

        OAuthClaims claims = extractor.extract(userInfo);

        assertEquals("inspur", claims.provider());
        assertEquals("user-uuid-789", claims.subject());
        assertNull(claims.email());
        assertFalse(claims.emailVerified());
    }
}