package com.helpdesk.notificationserver.service;


import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GoogleApiConnectionService {

    private final String clientId;
    private final String clientSecret;
    private final String refreshToken;

    public GoogleApiConnectionService(
            @Value("${google.api.client-id}") String clientId,
            @Value("${google.api.client-secret}") String clientSecret,
            @Value("${google.api.refresh-token}") String refreshToken
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.refreshToken = refreshToken;
    }

    public Gmail getConnectionToGmail() throws Exception{

        GoogleCredentials credentials = UserCredentials.newBuilder()
                .setClientId(this.clientId)
                .setClientSecret(this.clientSecret)
                .setRefreshToken(this.refreshToken)
                .build();
        try {
            Gmail gmail = new Gmail.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            ).setApplicationName("notification-service-helpdesk").build();
            return gmail;
        } catch (Exception e) {
            log.error("Cannot connect to gmail service or google client api: {}", e.getMessage());
            throw new Exception("Error during creation of gmail in GoogleApiConnectionService");
        }

    }



}
