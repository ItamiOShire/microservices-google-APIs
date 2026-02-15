package com.helpdesk.notificationserver.service;


import com.google.api.client.auth.oauth2.TokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class GmailCredentialsService {



    private TokenResponse refreshAccessToken() {

        RestTemplate restTemplate = new RestTemplate();

        return new TokenResponse();

    }

}
