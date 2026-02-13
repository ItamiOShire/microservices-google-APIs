package com.helpdesk.ticketreceiver.controller;


import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.helpdesk.ticketreceiver.datamodel.Ticket;
import com.helpdesk.ticketreceiver.dto.TicketDto;
import com.helpdesk.ticketreceiver.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/ticket/")
public class TicketController {

    private final TicketService ticketService;

    @Autowired
    TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }


    @PostMapping("/")
    public ResponseEntity<String> receiveTicketRequest(@RequestBody TicketDto userTicket) {

        Mono<Ticket> ticket = ticketService.processTicketRequest(userTicket);
        ticket.subscribe();

        return ResponseEntity.ok("Zgłoszenie zostało zarejestrowane, sprawdź maila aby dowiedzieć się więcej.");

    }


}
