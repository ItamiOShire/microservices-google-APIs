package com.helpdesk.ticketreceiver.service;


import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.helpdesk.ticketreceiver.datamodel.Ticket;
import com.helpdesk.ticketreceiver.dto.TicketDto;
import com.helpdesk.ticketreceiver.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final PubSubTemplate pubSubTemplate;
    private final String TOPIC_NAME;
    private static final Logger LOGGER = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    TicketService(
            TicketRepository ticketRepository,
            PubSubTemplate pubSubTemplate,
            @Value("${cloud.gcp.pubsub.topic.name}") String topicName)  {
        this.ticketRepository = ticketRepository;
        this.pubSubTemplate = pubSubTemplate;
        this.TOPIC_NAME = topicName;
    }

    public Mono<Ticket> processTicketRequest(TicketDto userTicket) {

        Ticket ticket = new Ticket(userTicket);

        Mono<Ticket> savedTicket = ticketRepository.save(ticket)
                .doOnError(err -> LOGGER.error("Error przy zapisywaniu ticketu: {}", err.getMessage()))
                .doOnSuccess( t -> LOGGER.info("Zapisano ticket o id {}", t.getId()))
                .flatMap( t -> Mono.fromFuture(pubSubTemplate.publish(TOPIC_NAME, t.getEmail()))
                        .doOnError(err -> LOGGER.error("Error przy publikowaniu wiadomości: {}" , err.getMessage()))
                        .doOnSuccess( mess -> LOGGER.info("Opublikowano temat z wiadomością - {}", mess))
                        .thenReturn(t))
                .doOnError(err -> LOGGER.error("Błąd przy przetwarzaniu tematu: {}", err.getMessage()))
                .doOnSuccess(t -> LOGGER.info("Operacja przetwarzania ticketa zakończona pomyślnie"));

        return savedTicket;
    }

}
