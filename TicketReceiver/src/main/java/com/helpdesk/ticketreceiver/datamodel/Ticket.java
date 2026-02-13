package com.helpdesk.ticketreceiver.datamodel;
import com.google.cloud.firestore.annotation.*;
import com.google.cloud.spring.data.firestore.Document;
import com.helpdesk.ticketreceiver.dto.TicketDto;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Document(collectionName = "ticket")
@Getter
@Setter
public class Ticket {

    @DocumentId
    private String id;

    private String subject;

    @Email
    private String email;

    private String message;

    private String status;

    private String priority;

    private LocalDate createdAt;

    private String assignedTo;

    public Ticket(){}

    public Ticket(TicketDto userTicket) {

        this.subject = userTicket.getSubject();
        this.message = userTicket.getMessage();
        this.status = Status.NEW.toString();
        this.createdAt = LocalDate.now();

    }

}
