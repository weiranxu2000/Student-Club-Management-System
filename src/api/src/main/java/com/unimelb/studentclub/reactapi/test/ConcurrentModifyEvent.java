package com.unimelb.studentclub.reactapi.test;

import com.unimelb.studentclub.reactapi.domain.Event;
import com.unimelb.studentclub.reactapi.port.postgres.ConnectionProvider;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ConcurrentModifyEvent {

    public static void main(String[] args) {

        // Config test parameters
        int numThreads = 8;  // Range: 0 - 10
        List<Thread> threads = new ArrayList<Thread>();

        // Config event parameters. All the parameters should be found in database.
        String id = "8474e225-60dc-4c78-9a53-20f1dc2a1865";
        int clubId = 1;
        String title = "Watch LCK final game.";
        int cost = 0;
        String description = "Support T1!";
        int venueId = 0;
        Date date = new java.util.Date(2024, 9, 8);
        LocalTime time = LocalTime.of(16, 0, 0, 0);
        Event.Status status = Event.Status.created;

        // Create event object
        Event event = new Event();
        event.setId(id);
        event.setClubId(clubId);
        event.setTitle(title);
        event.setCost(cost);
        event.setDescription(description);
        event.setVenueId(venueId);
        event.setDate(date);
        event.setTime(time);
        event.setStatus(status);

        // Create connection provider
        String uri = "jdbc:postgresql://localhost:5432/student_club";
        String username = "student_club_database_manager";
        String password = "admin";

        var connectionProvider = new ConnectionProvider(uri, username, password);
        connectionProvider.init();

        //Start threads
        System.out.println("Starting threads");
        for (int i = 0; i < numThreads; i++) {
            cost ++;
            event.setCost(cost);
            Thread t = new EventModifyThread(event, connectionProvider);
            threads.add(t);
            t.start();
        }
        try {
            for (Thread t: threads) {
                t.join();
                System.out.println(t.getName() + " finished");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Threads finished");
    }

}
