package com.helpdesk.notificationserver.service;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.helpdesk.notificationserver.dto.EmailData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class NotificationSubHandler {

    private final PubSubTemplate pubSubTemplate;
    private static final String TOPIC_NAME = System.getenv("topic_name");
    private final EmailData emailData;

    @Autowired
    NotificationSubHandler(PubSubTemplate pubSubTemplate, EmailData emailData) {
        this.pubSubTemplate = pubSubTemplate;
        this.emailData = emailData;
    }


    @PostConstruct
    public void subscribe() {

        pubSubTemplate.subscribe(TOPIC_NAME, message -> {
            String toEmail = message.getPubsubMessage().getData().toStringUtf8();
            emailData.setToAddressEmail(toEmail);
        });

    }
}
