package com.helpdesk.notificationserver.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class EmailData {

    private final String fromAddressEmail = "cindefoftwoworldsflames.gm@gmail.com";
    private final String subject = "Helpdesk - info email";
    private final String content = """
            
            Hello. We would like to inform you, that your issue has been reported in our system.
            
            """;
    @Setter
    private String toAddressEmail;
}
