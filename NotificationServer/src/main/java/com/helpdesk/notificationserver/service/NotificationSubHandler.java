package com.helpdesk.notificationserver.service;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.helpdesk.notificationserver.dto.EmailData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class NotificationSubHandler {

    private final PubSubTemplate pubSubTemplate;
    private final GmailApiService gmailApiService;
    private final String NOTIFICATION_SUB_NAME;
    private final EmailData emailData;

    @Autowired
    NotificationSubHandler(
            PubSubTemplate pubSubTemplate,
            EmailData emailData,
            GmailApiService gmailApiService,
            @Value("${pubsub.sub.name}") String subName) {
        this.pubSubTemplate = pubSubTemplate;
        this.emailData = emailData;
        this.gmailApiService = gmailApiService;
        this.NOTIFICATION_SUB_NAME = subName;
    }


    @PostConstruct
    public void subscribe() {

        pubSubTemplate.subscribe(NOTIFICATION_SUB_NAME, message -> {
            String toEmail = message.getPubsubMessage().getData().toStringUtf8();
            emailData.setToAddressEmail(toEmail);
            gmailApiService.sendEmail(emailData);
            message.ack();
        });

    }
}
