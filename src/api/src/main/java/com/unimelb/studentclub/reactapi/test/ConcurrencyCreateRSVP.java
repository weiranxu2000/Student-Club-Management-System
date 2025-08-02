package com.unimelb.studentclub.reactapi.test;

import com.unimelb.studentclub.reactapi.domain.*;
import com.unimelb.studentclub.reactapi.port.postgres.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConcurrencyCreateRSVP {

    public static void main(String[] args) {

        // Config test parameters
        List<Thread> threads = new ArrayList<>();

        // Config RSVP parameters
        String eventId = "8474e225-60dc-4c78-9a53-20f1dc2a1865";
        int mainStudentId1 = 1111111;
        int mainStudentId2 = 2222222;

        // Each main student booked for themselves and two other students
        List<Integer> attendees1 = new ArrayList<>(List.of(mainStudentId1, 3333333, 4444444));
        //List<Integer> attendees2 = new ArrayList<>(List.of(mainStudentId2, 5555555, 6666666));
        List<Integer> attendees2 = new ArrayList<>(List.of(mainStudentId2, 3333333, 5555555));

        // Create connection provider
        String uri = "jdbc:postgresql://localhost:5432/student_club";
        String username = "student_club_database_manager";
        String password = "admin";

        var connectionProvider = new ConnectionProvider(uri, username, password);
        connectionProvider.init();

        RSVPRepositoryImpl rsvpMapper = new RSVPRepositoryImpl(connectionProvider);
        TicketRepositoryImpl ticketMapper = new TicketRepositoryImpl(connectionProvider);

        // Start threads
        System.out.println("Starting threads");
        Thread t1 = new CreateRSVPThread(mainStudentId1, eventId, attendees1, rsvpMapper, ticketMapper);
        threads.add(t1);
        t1.start();

        Thread t2 = new CreateRSVPThread(mainStudentId2, eventId, attendees2, rsvpMapper, ticketMapper);
        threads.add(t2);
        t2.start();

        try {
            for (Thread t: threads) {
                t.join();
                System.out.println(t.getName() + " finished");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("All threads completed.");
    }
}