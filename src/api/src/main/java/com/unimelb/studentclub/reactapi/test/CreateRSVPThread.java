package com.unimelb.studentclub.reactapi.test;

import com.unimelb.studentclub.reactapi.domain.*;
import com.unimelb.studentclub.reactapi.port.postgres.*;

import java.util.List;
import java.util.UUID;

public class CreateRSVPThread extends Thread {
    private final int mainStudentId;
    private final String eventId;
    private final List<Integer> attendees;
    private final RSVPRepositoryImpl rsvpMapper;
    private final TicketRepositoryImpl ticketMapper;

    public CreateRSVPThread(int mainStudentId, String eventId, List<Integer> attendees, RSVPRepositoryImpl rsvpMapper, TicketRepositoryImpl ticketMapper) {
        this.mainStudentId = mainStudentId;
        this.eventId = eventId;
        this.attendees = attendees;
        this.rsvpMapper = rsvpMapper;
        this.ticketMapper = ticketMapper;
    }

    @Override
    public void run() {
        UnitOfWork uow = new UnitOfWork(rsvpMapper, ticketMapper);

        // Create new RSVP
        RSVP rsvp = new RSVP();
        rsvp.setId(UUID.randomUUID().toString());
        rsvp.setStudentId(mainStudentId);
        rsvp.setEventId(eventId);
        uow.registerNew(rsvp);

        // Create tickets for all attendees
        for (Integer attendeeId : attendees) {
            Ticket ticket = new Ticket();
            ticket.setRSVPId(rsvp.getId());
            ticket.setStudentId(attendeeId);
            ticket.setStatus(Ticket.Status.created);
            uow.registerNew(ticket);
        }

        // Commit the unit of work
        boolean success = uow.commit();
        if (success) {
            System.out.println("RSVP and tickets created successfully for main student " + mainStudentId + " and attendees " + attendees);
        } else {
            System.out.println("Failed to create RSVP and tickets for main student " + mainStudentId + " and attendees " + attendees);
        }
    }
}