package com.iflytek.skillhub;

import com.iflytek.skillhub.config.ProfileFieldPolicyProperties;
import com.iflytek.skillhub.config.ProfileModerationProperties;
import com.iflytek.skillhub.auth.inspur.InspurPassportProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Main Spring Boot entry point for the SkillHub backend application.
 */
@SpringBootApplication
@EnableConfigurationProperties({ProfileModerationProperties.class, ProfileFieldPolicyProperties.class, InspurPassportProperties.class})
public class SkillhubApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkillhubApplication.class, args);
    }
}
