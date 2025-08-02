package com.unimelb.studentclub.reactapi.domain;

import java.util.List;
import java.util.Map;

public interface TicketRepository {
    public boolean create(Ticket ticket);
    public void cancel(Ticket ticket);
    public Map<Ticket, String> getAll(int studentId);
}