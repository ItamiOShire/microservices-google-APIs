package com.helpdesk.notificationserver.config;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleAuthConfig {

    private final String clientId = System.getenv("CLIENT-ID");
    private final String clientSecret = System.getenv("CLIENT-SECRET");
    private final String refreshToken = System.getenv("REFRESH-TOKEN");

    @Bean
    public GoogleCredentials googleCredentials() {
        return UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(refreshToken)
                .build();
    }
}
