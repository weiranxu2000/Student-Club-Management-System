package com.unimelb.studentclub.reactapi.test;

import com.unimelb.studentclub.reactapi.domain.Event;
import com.unimelb.studentclub.reactapi.domain.EventRepository;
import com.unimelb.studentclub.reactapi.port.postgres.ConnectionProvider;
import com.unimelb.studentclub.reactapi.port.postgres.EventRepositoryImpl;

import java.sql.Connection;
import java.sql.SQLException;


public class EventModifyThread extends Thread {

    private final Event event;
    private final EventRepository eventRepository;
    private final ConnectionProvider connectionProvider;

    public EventModifyThread(Event event, ConnectionProvider connectionProvider) {
        this.event = event;
        this.connectionProvider = connectionProvider;
        this.eventRepository = new EventRepositoryImpl(connectionProvider);
    }

    @Override
    public void run() {
        eventRepository.modify(event);
    }
}

