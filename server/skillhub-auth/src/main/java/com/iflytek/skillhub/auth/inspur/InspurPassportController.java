package com.iflytek.skillhub.auth.inspur;

import com.iflytek.skillhub.auth.identity.IdentityBindingService;
import com.iflytek.skillhub.auth.inspur.InspurPassportProperties;
import com.iflytek.skillhub.auth.inspur.InspurUserInfoService;
import com.iflytek.skillhub.auth.oauth.ClaimsExtractor;
import com.iflytek.skillhub.auth.oauth.OAuthClaims;
import com.iflytek.skillhub.auth.policy.AccessDecision;
import com.iflytek.skillhub.auth.policy.AccessPolicy;
import com.iflytek.skillhub.auth.rbac.PlatformPrincipal;
import com.iflytek.skillhub.domain.user.UserStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller
public class InspurPassportController {

    private static final Logger log = LoggerFactory.getLogger(InspurPassportController.class);

    private final InspurPassportProperties properties;
    private final InspurUserInfoService userInfoService;
    private final ClaimsExtractor claimsExtractor;
    private final AccessPolicy accessPolicy;
    private final IdentityBindingService identityBindingService;

    public InspurPassportController(InspurPassportProperties properties,
                                     InspurUserInfoService userInfoService,
                                     ClaimsExtractor claimsExtractor,
                                     AccessPolicy accessPolicy,
                                     IdentityBindingService identityBindingService) {
        this.properties = properties;
        this.userInfoService = userInfoService;
        this.claimsExtractor = claimsExtractor;
        this.accessPolicy = accessPolicy;
        this.identityBindingService = identityBindingService;
    }

    @GetMapping("/auth/inspur-login")
    public void getLoginUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("############开始执行重定向");
        log.error("############request:"+request);
        log.error("############request.session:"+request.getSession());
        log.error("############request.getSession().getAttribute(\"platformPrincipal\"):"+request.getSession().getAttribute("platformPrincipal"));
        if (request.getSession().getAttribute("platformPrincipal") != null) {
            response.sendRedirect("/");
            return;
        }
        log.error("############getInspurLoginUrl"+getInspurLoginUrl());

        response.sendRedirect(getInspurLoginUrl());
    }

    @GetMapping("/ssologin")
    public void ssoLogin(@RequestParam String code,
                         @RequestParam String state,
                         HttpServletRequest request,
                         HttpServletResponse response) throws Exception {
        if (!properties.getState().equals(state)) {
            log.warn("Inspur Passport state mismatch: expected={}, got={}", properties.getState(), state);
            response.sendRedirect("/access-denied");
            return;
        }

        String accessToken = userInfoService.exchangeToken(code);
        Map<String, Object> userInfo = userInfoService.getUserInfo(accessToken);
        OAuthClaims claims = claimsExtractor.extract(userInfo);

        AccessDecision decision = accessPolicy.evaluate(claims);
        if (decision == AccessDecision.DENY) {
            response.sendRedirect("/access-denied");
            return;
        }

        PlatformPrincipal principal = identityBindingService.bindOrCreate(claims, UserStatus.ACTIVE);
        request.getSession().setAttribute("platformPrincipal", principal);

        response.sendRedirect("/");
    }

    private String getInspurLoginUrl() {
        return properties.getAuthServer()
            + "/oauth2.0/authorize"
            + "?response_type=code"
            + "&client_id=" + properties.getClientId()
            + "&redirect_uri=" + URLEncoder.encode(properties.getRedirectUri(), StandardCharsets.UTF_8)
            + "&state=" + properties.getState();
    }
}