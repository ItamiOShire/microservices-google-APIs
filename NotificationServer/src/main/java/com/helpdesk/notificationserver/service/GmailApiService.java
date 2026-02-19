package com.helpdesk.notificationserver.service;


import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.helpdesk.notificationserver.dto.EmailData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GmailApiService {

    private final GmailEmailMessagesService messagesService;
    private final GoogleApiConnectionService googleApiConnectionService;

    @Autowired
    public GmailApiService(
            GmailEmailMessagesService messagesService,
            GoogleApiConnectionService googleApiConnectionService
    ) {
        this.messagesService = messagesService;
        this.googleApiConnectionService = googleApiConnectionService;
    }

    public void sendEmail(
            EmailData emailData
    ) {

        try {
            Message message = messagesService.createAndEncodeEmail(emailData);
            Gmail gmail = googleApiConnectionService.getConnectionToGmail();
            Message sentMessage = gmail.users().messages().send(emailData.getFromAddressEmail(), message).execute();

            log.info("Sent message id: {}", sentMessage.getId());
            log.info("Message: {}", sentMessage.toPrettyString());

        } catch (Exception e) {
            log.error("Cannot create message or email: {}", e.getMessage());
        }

    }




}
