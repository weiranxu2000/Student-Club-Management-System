package com.unimelb.studentclub.reactapi.test;

import com.unimelb.studentclub.reactapi.domain.Application;
import com.unimelb.studentclub.reactapi.port.postgres.ConnectionProvider;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ConcurrentReviewApplication {
    public static void main(String[] args) {

        // Config test parameters
        int numThreads = 2;  // Range: 0 - 10
        List<Thread> threads = new ArrayList<Thread>();

        // Config application parameters. All the parameters should be found in database.
        String id = "b959ffd1-fd7b-4f82-8f41-5e3946774b60";
        int clubId = 2;
        String description = "test";
        int amount = 2000;
        //Date date = new java.util.Date(2024, 9, 8);
        //LocalTime time = LocalTime.of(16, 0, 0, 0);
        Application.Status status = Application.Status.submitted;

        // Create event Application
        var application = new Application();
        application.setId(id);
        application.setDescription(description);
        application.setAmount(amount);
        application.setDate(ZonedDateTime.now());
        application.setStatus(Application.Status.submitted);
        application.setClubId(clubId);

        // Create connection provider
        String uri = "jdbc:postgresql://localhost:5432/student_club";
        String username = "student_club_database_manager";
        String password = "admin";

        var connectionProvider = new ConnectionProvider(uri, username, password);
        connectionProvider.init();

        //Start threads
        System.out.println("Starting threads");
        Thread t1 = new ApplicationApproveThread(application, connectionProvider);
        threads.add(t1);
        t1.start();
        Thread t2 = new ApplicationRejectThread(application, connectionProvider);
        threads.add(t2);
        t2.start();
//        for (int i = 0; i < numThreads; i++) {
//            Thread t = new ApplicationReviewThread(application, connectionProvider);
//            threads.add(t);
//            t.start();
//        }

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
