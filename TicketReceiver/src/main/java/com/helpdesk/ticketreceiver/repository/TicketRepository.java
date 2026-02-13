package com.helpdesk.ticketreceiver.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.helpdesk.ticketreceiver.datamodel.Ticket;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends FirestoreReactiveRepository<Ticket> {
}
