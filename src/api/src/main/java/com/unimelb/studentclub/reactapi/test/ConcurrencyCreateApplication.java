package com.unimelb.studentclub.reactapi.test;

import com.unimelb.studentclub.reactapi.domain.Application;
import com.unimelb.studentclub.reactapi.port.postgres.ConnectionProvider;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ConcurrencyCreateApplication {

    public static void main(String[] args) {

        // Config test parameters
        List<Thread> threads = new ArrayList<Thread>();

        // Config application parameters.
        String application1Description = "This is a test 1 application.";
        String application2Description = "This is a test 2 application.";
        int applicationAmount = 100;
        int applicationClubID = 3;

        // Create application object
        Application application1 = new Application();
        application1.setId(UUID.randomUUID().toString());
        application1.setDescription(application1Description);
        application1.setAmount(applicationAmount);
        application1.setDate(ZonedDateTime.now());
        application1.setStatus(Application.Status.submitted);
        application1.setClubId(applicationClubID);

        Application application2 = new Application();
        application2.setId(UUID.randomUUID().toString());
        application2.setDescription(application2Description);
        application2.setAmount(applicationAmount);
        application2.setDate(ZonedDateTime.now());
        application2.setStatus(Application.Status.submitted);
        application2.setClubId(applicationClubID);

        // Create connection provider
        String uri = "jdbc:postgresql://localhost:5432/student_club";
        String username = "student_club_database_manager";
        String password = "admin";

        var connectionProvider = new ConnectionProvider(uri, username, password);
        connectionProvider.init();

        //Start threads
        System.out.println("Starting threads");
        Thread t1 = new CreateApplicationThread(application1, connectionProvider);
        threads.add(t1);
        t1.start();

        Thread t2 = new CreateApplicationThread(application2, connectionProvider);
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
    }
}
