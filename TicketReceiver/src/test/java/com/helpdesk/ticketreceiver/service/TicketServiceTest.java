package com.helpdesk.ticketreceiver.service;

import com.helpdesk.ticketreceiver.datamodel.Ticket;
import com.helpdesk.ticketreceiver.dto.TicketDto;
import com.helpdesk.ticketreceiver.repository.TicketRepository;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private PubSubTemplate pubSubTemplate;

    @InjectMocks
    private TicketService ticketService;

    private final String TOPIC_NAME = "test-ticket-topic";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSaveTicketAndPublishMessage() {
        // given
        TicketDto dto = new TicketDto();
        dto.setEmail("user@example.com");
        dto.setSubject("Test subject");
        dto.setMessage("Message content");

        Ticket saved = new Ticket(dto);
        saved.setId("123");

        // mock: repository zwraca zapisany ticket
        when(ticketRepository.save(any(Ticket.class))).thenReturn(Mono.just(saved));

        // mock: pubsub.publish zwraca "udany" future
        when(pubSubTemplate.publish(eq(TOPIC_NAME), eq(saved.getEmail())))
                .thenReturn(CompletableFuture.completedFuture("msg-id-1"));

        // when
        Mono<Ticket> result = ticketService.processTicketRequest(dto);

        // then
        StepVerifier.create(result)
                .expectNextMatches(ticket ->
                        ticket.getId().equals("123") &&
                                ticket.getEmail().equals("user@example.com"))
                .verifyComplete();

        // verify side-effects
        verify(ticketRepository, times(1)).save(any(Ticket.class));
        verify(pubSubTemplate, times(1)).publish(TOPIC_NAME, saved.getEmail());
    }

}
