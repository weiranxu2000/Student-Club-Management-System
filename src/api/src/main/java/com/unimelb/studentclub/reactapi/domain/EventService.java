package com.unimelb.studentclub.reactapi.domain;

import com.unimelb.studentclub.reactapi.port.postgres.ClubRepositoryImpl;
import com.unimelb.studentclub.reactapi.port.postgres.EventRepositoryImpl;
import com.unimelb.studentclub.reactapi.port.postgres.StudentClubRepositoryImpl;
import com.unimelb.studentclub.reactapi.port.postgres.VenueRepositoryImpl;
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
import java.util.Map;

public class EventService {
    private final EventRepositoryImpl eventMapper;
    private final ClubRepositoryImpl clubMapper;
    private final VenueRepositoryImpl venueMapper;
    private final StudentClubRepositoryImpl studentClubMapper;

    public EventService(EventRepositoryImpl eventMapper, ClubRepositoryImpl clubMapper, VenueRepositoryImpl venueMapper,StudentClubRepositoryImpl studentClubMapper) {
        this.eventMapper = eventMapper;
        this.clubMapper = clubMapper;
        this.venueMapper = venueMapper;
        this.studentClubMapper = studentClubMapper;
    }

    // Create an event.
    public Event create(HttpServletRequest req) throws IOException, ParseException {
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
        String eventClubName = json.getString("club_name");
        String eventTitle = getElement(json, "title");
        String eventDescription = getElement(json, "description");
        String eventVenueName = getElement(json, "venue_name");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date eventDate = dateFormat.parse(getElement(json, "date"));
        LocalTime eventTime = LocalTime.parse(getElement(json, "time"));
        int eventCost = Integer.parseInt(getElement(json, "cost"));
        //Event.Status status = Event.Status.valueOf(getElement(json, "status"));
        //Event.Status status = Event.Status.created;
        int admin_id = json.getInt("admin_id");

        // Use data mapper to get data from or insert data into database.
        // Get club id.
        int eventClubID = clubMapper.getId(eventClubName);

        // Get venue id.
        int eventVenueID = venueMapper.getId(eventVenueName);

        boolean canBeCreatedByThisStudent = studentClubMapper.isAdmin(admin_id,eventClubName);

        if (!canBeCreatedByThisStudent){
            return null;
        }
        // Create new event.
        var event = new Event();
        event.setId(UUID.randomUUID().toString());
        event.setClubId(eventClubID);
        event.setTitle(eventTitle);
        event.setDescription(eventDescription);
        event.setVenueId(eventVenueID);
        event.setDate(eventDate);
        event.setTime(eventTime);
        event.setCost(eventCost);
        event.setStatus(Event.Status.created);
        event.setClubName(eventClubName);
        event.setVenueName(eventVenueName);

        // Insert into database.
        eventMapper.create(event);

        return event;
    }

    // Modify an existing event.
    public Event modify(HttpServletRequest req) throws IOException, ParseException {
        // Analyze request.
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // Parse the JSON request.
        JSONObject json = new JSONObject(sb.toString());

        // Get event ID to modify.
        // Fields that can modify -> title, description, venue, data and time.
        String eventId = getElement(json, "id");
        String eventTitle = getElement(json, "title");
        String clubName = getElement(json, "club_name");
        String eventDescription = getElement(json, "description");
        String eventVenueName = getElement(json, "venue_name");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date eventDate = dateFormat.parse(getElement(json, "date"));
        LocalTime eventTime = LocalTime.parse(getElement(json, "time"));
        //int eventCost = Integer.parseInt(getElement(json, "cost"));
        int eventCost = json.getInt("cost");

                System.out.println(eventId+eventTitle+eventDescription+eventVenueName+eventDate+eventCost);

        // Get venue id
        int eventVenueID = venueMapper.getId(eventVenueName);
        // Get club id.
        int eventClubID = clubMapper.getId(clubName);

        // Get the number of current rsvps for this event.

        // Outnumber -> Cancelled.

        // Create an event object to update.
        Event event = new Event();
        event.setId(eventId);
        event.setTitle(eventTitle);
        event.setDescription(eventDescription);
        event.setVenueId(eventVenueID);
        event.setDate(eventDate);
        event.setTime(eventTime);
        event.setVenueName(eventVenueName);
        event.setCost(eventCost);
        event.setClubId(eventClubID);
        event.setClubName(clubName);

        // Update the event in the database.
        eventMapper.modify(event); // Directly update without prior findById check.

        return event;
    }

    // Cancel an existing event.
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

        // Get the event ID from the request
        String eventId = getElement(json, "id"); // Ensure the front-end provides the event ID

        // Call the cancel method to cancel the event in the database
        eventMapper.cancel(eventId);
    }

    // Get all events (basic info)
    public Map<Event, Integer> getAll() {
        return eventMapper.getAll();  // Fetch all events from the repository
    }

    // Get detailed info for a specific event
    public Event getDetails(HttpServletRequest req) throws IOException {
        // Get the event ID from the request
        String eventId = req.getParameter("id");

        // Create an event object with the ID
        Event event = new Event();
        event.setId(eventId);

        // Fetch and return the detailed information of the event
        return eventMapper.getDetails(event);
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
