package com.unimelb.studentclub.reactapi.domain;

import com.unimelb.studentclub.reactapi.port.postgres.RSVPRepositoryImpl;
import com.unimelb.studentclub.reactapi.port.postgres.TicketRepositoryImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import org.json.JSONArray;
import java.text.SimpleDateFormat;
import java.time.LocalTime;

public class RSVPService {
    private final RSVPRepositoryImpl rsvpMapper;
    private final TicketRepositoryImpl ticketMapper;

    public RSVPService(RSVPRepositoryImpl rsvpMapper, TicketRepositoryImpl ticketMapper) {
        this.rsvpMapper = rsvpMapper;
        this.ticketMapper = ticketMapper;
    }

    // Create a rsvp.
    public boolean create(HttpServletRequest req) throws IOException, ParseException {
        // Analyze request.
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // Get all the elements of rsvps in request.
        // Note the difference in naming between checking and front-end
        // No validation implemented right now.
        JSONObject json = new JSONObject(sb.toString());
        int studentId = json.getInt("student_id");
        if (studentId == 0){
            throw new BeanDefinitionValidationException("studentId is required!");
        }
        String eventId = getElement(json, "event_id");

        JSONArray attendeesArray = json.getJSONArray("attendees");
        List<Integer> attendees = new ArrayList<>();
        for (int i = 0; i < attendeesArray.length(); i++) {
            String attendeeStr = attendeesArray.getString(i);
            attendees.add(Integer.parseInt(attendeeStr));
        }

        System.out.println(attendees);

        UnitOfWork uof = new UnitOfWork(rsvpMapper, ticketMapper);

        // Create new rsvp.
        var rsvp = new RSVP();
        rsvp.setId(UUID.randomUUID().toString());
        rsvp.setStudentId(studentId);
        rsvp.setEventId(eventId);
        uof.registerNew(rsvp);

        // Create the corresponding ticket(s).
        for (Integer attendee : attendees) {
            var ticket = new Ticket();
            ticket.setRSVPId(rsvp.getId());
            ticket.setStatus(Ticket.Status.created);
            ticket.setStudentId(attendee);
            uof.registerNew(ticket);
        }

        // Insert into database. Use unit of work.
        return uof.commit();
        // rsvpMapper.create(rsvp);

        //return rsvp;
    }

    public Map<Ticket, String> getAllTicket(HttpServletRequest req) throws IOException, ParseException {
//        // Analyze request.
//        StringBuilder sb = new StringBuilder();
//        BufferedReader reader = req.getReader();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            sb.append(line);
//        }
//        JSONObject json = new JSONObject(sb.toString());
//        int studentId = Integer.parseInt(getElement(json, "student_id"));

        // get student_id from req
        String studentIdStr = req.getParameter("student_id");

        if (studentIdStr == null || studentIdStr.isEmpty()) {
            throw new IllegalArgumentException("student_id is required");
        }

        int studentId = Integer.parseInt(studentIdStr);

        return ticketMapper.getAll(studentId);
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