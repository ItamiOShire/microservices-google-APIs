package com.helpdesk.notificationserver.service;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class NotificationSubHandler {

    private final PubSubTemplate pubSubTemplate;
    private static final String TOPIC_NAME = System.getenv("topic_name");

    @Autowired
    NotificationSubHandler(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }


    @PostConstruct
    public void subscribe() {

        pubSubTemplate.subscribe(TOPIC_NAME, message -> {
            String ticketId = message.getPubsubMessage().getData().toStringUtf8();

        });

    }
}
