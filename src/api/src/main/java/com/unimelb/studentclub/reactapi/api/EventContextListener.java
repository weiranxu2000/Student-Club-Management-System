package com.unimelb.studentclub.reactapi.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unimelb.studentclub.reactapi.domain.EventService;
import com.unimelb.studentclub.reactapi.port.postgres.*;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@WebListener
public class EventContextListener implements ServletContextListener {
    static final String EVENT_SERVICE = "eventService";
    static final String MAPPER = "mapper";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        var connectionProvider = new ConnectionProvider(System.getProperty("jdbc.uri"),
                System.getProperty("jdbc.username"),
                System.getProperty("jdbc.password"));
        connectionProvider.init();
        sce.getServletContext().setAttribute(EVENT_SERVICE, new EventService(new EventRepositoryImpl(connectionProvider), new ClubRepositoryImpl(connectionProvider), new VenueRepositoryImpl(connectionProvider), new StudentClubRepositoryImpl(connectionProvider)));

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
