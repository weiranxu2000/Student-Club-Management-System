package com.unimelb.studentclub.reactapi.domain;

import com.unimelb.studentclub.reactapi.port.postgres.TicketRepositoryImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TicketService {
    private final TicketRepositoryImpl ticketMapper;


    public TicketService(TicketRepositoryImpl ticketMapper) {
        this.ticketMapper = ticketMapper;
    }

    // Create an event.
    public Ticket create(HttpServletRequest req) throws IOException, ParseException {
        // Analyze request.
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // Get all the elements of events in request.
        // Note the difference in naming between checking and front-end
        // No validation implemented right now.
        JSONObject json = new JSONObject(sb.toString());
        String rsvpId = getElement(json, "rsvp_id");
        int studentId = Integer.parseInt(getElement(json, "student_id"));
        //Ticket.Status status = Ticket.Status.valueOf(getElement(json, "status"));
        Ticket.Status status = Ticket.Status.created;
                // Create new ticket.
        var ticket = new Ticket();
        ticket.setStudentId(studentId);
        ticket.setRSVPId(rsvpId);
        ticket.setStatus(status);

        // Insert into database.
        ticketMapper.create(ticket);

        return ticket;
    }

    // Cancel an existing ticket.
    public void cancel(HttpServletRequest req) throws IOException {
        // Analyze request.
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // Parse the JSON data
        JSONObject json = new JSONObject(sb.toString());
        String rsvpId = getElement(json, "rsvp_id");
        int studentId = json.getInt("student_id");

        var ticket = new Ticket();
        ticket.setStudentId(studentId);
        ticket.setRSVPId(rsvpId);

        System.out.println(studentId);
        System.out.println(rsvpId);

        // Call the cancel method to cancel the ticket in the database
        ticketMapper.cancel(ticket);
    }

    private String getElement(JSONObject json, String str){
        String element = json.getString(str);
        if (element == null){
            throw new BeanDefinitionValidationException(
                    str + " is required!"
            );
        }
        return element;
    }

}
