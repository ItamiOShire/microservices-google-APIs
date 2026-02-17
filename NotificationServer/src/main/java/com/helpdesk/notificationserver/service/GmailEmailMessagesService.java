package com.helpdesk.notificationserver.service;

import com.google.api.services.gmail.model.Message;

import com.helpdesk.notificationserver.dto.EmailData;
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

    private MimeMessage createEmailContent(
            EmailData emailData
    ) throws MessagingException {

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(emailData.getFromAddressEmail());
        email.addRecipients(jakarta.mail.Message.RecipientType.TO, emailData.getToAddressEmail());
        email.setSubject(emailData.getSubject());
        email.setText(emailData.getContent());

        return email;
    }

    private Message encodeEmailContent(MimeMessage email)
        throws MessagingException, IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);

        return message;
    }

    public Message createAndEncodeEmail(
            EmailData emailData
    ) throws MessagingException, IOException {
        return encodeEmailContent(
                createEmailContent(
                        emailData
                )
        );
    }

}
