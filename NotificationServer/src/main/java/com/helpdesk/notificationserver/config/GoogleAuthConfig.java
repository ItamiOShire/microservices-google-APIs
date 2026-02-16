package com.helpdesk.notificationserver.config;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleAuthConfig {

    private final String clientId = System.getenv("client+id");
    private final String clientSecret = System.getenv("client_secret");
    private final String refreshToken = System.getenv("refresh_token");

    @Bean
    public GoogleCredentials googleCredentials() {
        return UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(refreshToken)
                .build();
    }
}
