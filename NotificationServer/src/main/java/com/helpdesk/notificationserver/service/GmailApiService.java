package com.helpdesk.notificationserver.service;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.helpdesk.notificationserver.dto.EmailData;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class GmailApiService {

    private final GoogleCredentials credentials;
    private final Gmail gmail;
    private final GmailEmailMessagesService messagesService;


    @Autowired
    public GmailApiService(
            GoogleCredentials credentials,
            GmailEmailMessagesService messagesService
    ) throws Exception {

        this.messagesService = messagesService;
        this.credentials = credentials;
        this.gmail = new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(this.credentials)
        ).setApplicationName("notification-service-helpdesk").build();

    }

    public void sendEmail(
            EmailData emailData
    ) {

        try {
            Message message = messagesService.createAndEncodeEmail(emailData);
            Message sentMessage = gmail.users().messages().send(emailData.getFromAddressEmail(), message).execute();

            log.info("Sent message id: {}", sentMessage.getId());
            log.info("Message: {}", sentMessage.toPrettyString());

        } catch (Exception e) {
            log.error("Cannot create message or email: {}", e.getMessage());
        }

    }




}
