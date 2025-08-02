package com.unimelb.studentclub.reactapi.domain;

import com.unimelb.studentclub.reactapi.port.postgres.LockManagerWait;
import com.unimelb.studentclub.reactapi.port.postgres.RSVPRepositoryImpl;
import com.unimelb.studentclub.reactapi.port.postgres.TicketRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

public class UnitOfWork {
    private static final ThreadLocal<UnitOfWork> current = new ThreadLocal<>();
    private final RSVPRepositoryImpl RSVPMapper;
    private final TicketRepositoryImpl ticketMapper;
    private List<Object> newObjects = new ArrayList<>();

    public UnitOfWork(RSVPRepositoryImpl rsvpMapper, TicketRepositoryImpl ticketMapper) {
        this.RSVPMapper = rsvpMapper;
        this.ticketMapper = ticketMapper;
    }

    // Starting a new UnitOfWork
    public static void newCurrent(RSVPRepositoryImpl rsvpMapper, TicketRepositoryImpl ticketMapper) {
        setCurrent(new UnitOfWork(rsvpMapper, ticketMapper));
    }

    // Setting the current UnitOfWork
    public static void setCurrent(UnitOfWork uow) {
        current.set(uow);
    }

    // Get the current UnitOfWork
    public static UnitOfWork getCurrent() {
        return current.get();
    }

    // Registering a new object (e.g. RSVP or Ticket)
    public void registerNew(Object obj) {
        if (!newObjects.contains(obj)) {
            newObjects.add(obj);
        }
    }

    // commit
    public boolean commit() {
        // Lock the uow to prevent conflicts.
        LockManagerWait.getInstance().acquireLock("uow", Thread.currentThread().getName());

        boolean isSuccess =false;
        try {

            // Inserting a new object (processing a new RSVP or Ticket)
            for (Object obj : newObjects) {
                if (obj instanceof RSVP) {
                    RSVPMapper.create((RSVP) obj);
                } else if (obj instanceof Ticket) {
                    isSuccess = ticketMapper.create((Ticket) obj);
                }
            }

            // Empty all tracked objects after commit
            newObjects.clear();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LockManagerWait.getInstance().releaseLock("uow", Thread.currentThread().getName());
        }
        return isSuccess;
    }
}
