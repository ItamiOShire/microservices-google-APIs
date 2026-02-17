package com.helpdesk.ticketreceiver.service;


import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.helpdesk.ticketreceiver.datamodel.Ticket;
import com.helpdesk.ticketreceiver.dto.TicketDto;
import com.helpdesk.ticketreceiver.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final PubSubTemplate pubSubTemplate;
    private static final String TOPIC_NAME = System.getenv("topicName");
    private static final Logger LOGGER = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    TicketService(TicketRepository ticketRepository, PubSubTemplate pubSubTemplate) {
        this.ticketRepository = ticketRepository;
        this.pubSubTemplate = pubSubTemplate;
    }

    public Mono<Ticket> processTicketRequest(TicketDto userTicket) {

        Ticket ticket = new Ticket(userTicket);

        Mono<Ticket> savedTicket = ticketRepository.save(ticket)
                .doOnSuccess( t -> LOGGER.info("Zapisano ticket o id {}", t.getId()))
                .flatMap( t -> Mono.fromFuture(pubSubTemplate.publish(TOPIC_NAME, t.getEmail()))
                        .thenReturn(t))
                .doOnSuccess( t -> LOGGER.info("Opublikowano temat z wiadomością - {}", t.getEmail()));

        return savedTicket;
    }

}
