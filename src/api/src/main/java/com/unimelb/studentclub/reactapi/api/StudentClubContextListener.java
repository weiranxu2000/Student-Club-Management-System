package com.unimelb.studentclub.reactapi.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unimelb.studentclub.reactapi.domain.StudentClubService;
import com.unimelb.studentclub.reactapi.port.postgres.ClubRepositoryImpl;
import com.unimelb.studentclub.reactapi.port.postgres.ConnectionProvider;
import com.unimelb.studentclub.reactapi.port.postgres.StudentClubRepositoryImpl;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@WebListener
public class StudentClubContextListener implements ServletContextListener{
    public static final String STUDENTCLUB_SERVICE = "studentClubService";
    static final String MAPPER = "mapper";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("JDBC URI: " + System.getProperty("jdbc.uri"));
        System.out.println("JDBC Username: " + System.getProperty("jdbc.username"));
        System.out.println("JDBC Password: " + System.getProperty("jdbc.password"));

        var connectionProvider = new ConnectionProvider(System.getProperty("jdbc.uri"),
                System.getProperty("jdbc.username"),
                System.getProperty("jdbc.password"));
        connectionProvider.init();

        // Check that the connectionProvider is properly initialised
        if (connectionProvider == null) {
            System.out.println("ConnectionProvider failed to initialize");
        } else {
            System.out.println("ConnectionProvider initialized successfully");
        }

        // Initialise the StudentClubService and put it into the ServletContext.
        var studentClubService = new StudentClubService(new StudentClubRepositoryImpl(connectionProvider));
        sce.getServletContext().setAttribute(STUDENTCLUB_SERVICE, studentClubService);

        // Check if the studentClubService was created successfully.
        if (studentClubService == null) {
            System.out.println("StudentClubService failed to initialize");
        } else {
            System.out.println("StudentClubService initialized successfully");
        }

        // Handling ObjectMapper
        var mapper = Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .failOnUnknownProperties(false)
                .serializationInclusion(JsonInclude.Include.NON_EMPTY)
                .build();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        sce.getServletContext().setAttribute(MAPPER, mapper);

        ServletContextListener.super.contextInitialized(sce);
    }


    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }
}
