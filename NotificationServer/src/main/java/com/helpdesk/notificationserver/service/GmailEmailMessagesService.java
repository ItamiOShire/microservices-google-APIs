package com.helpdesk.notificationserver.service;

import com.google.api.services.gmail.model.Message;

import jakarta.mail.*;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

@Service
public class GmailEmailMessagesService {

    public static MimeMessage createEmailContent(
            String fromEmailAddress,
            String toEmailAddress,
            String subject,
            String content
    ) throws MessagingException {

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(fromEmailAddress);
        email.addRecipients(jakarta.mail.Message.RecipientType.TO, toEmailAddress);
        email.setSubject(subject);
        email.setText(content);

        return email;
    }

    public static Message encodeEmailContent(MimeMessage email)
        throws MessagingException, IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);

        return message;
    }

}
