package com.unimelb.studentclub.reactapi.port.postgres;

import com.unimelb.studentclub.reactapi.domain.Event;
import com.unimelb.studentclub.reactapi.domain.EventRepository;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.sql.Date;
import java.sql.Time;
import java.util.concurrent.locks.Lock;

public class EventRepositoryImpl implements EventRepository {
    private final ConnectionProvider connectionProvider;

    public EventRepositoryImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void create(Event event){
        insert(event);
    }

    // Insert an event into database.
    private void insert(Event event){
        var connection = connectionProvider.nextConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO app.event (id, club_id, title, description, venue_id, date, time, cost, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            statement.setObject(1, UUID.fromString(event.getId()));
            statement.setInt(2, event.getClubId());
            statement.setString(3, event.getTitle());
            statement.setString(4, event.getDescription());
            statement.setInt(5, event.getVenueId());
            statement.setObject(6, new Date(event.getDate().getTime()));
            statement.setObject(7, Time.valueOf(event.getTime()));
            statement.setInt(8, event.getCost());
            statement.setString(9, event.getStatus().name());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(String.format("failed to insert new event: %s", e.getMessage()), e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
    }

    @Override
    public void modify(Event event){
        update(event);
    }

    // update an event
    private void update(Event event) {

        // Connection to the database. Type: Connection
        var connection = connectionProvider.nextConnection();

        // Lock the event table to prevent conflicts.
        LockManagerWait.getInstance().acquireLock("event", Thread.currentThread().getName());

        try {
            PreparedStatement lockstatement = connection.prepareStatement(
                    "SELECT * FROM app.event WHERE id = ?"
            );
            lockstatement.setObject(1, UUID.fromString(event.getId()));
            ResultSet lockstatementResult = lockstatement.executeQuery();
            if (lockstatementResult.next()) {
                String stus = lockstatementResult.getString("status");
                if(stus.equals("cancelled")){
                    System.out.println("Event has been cancelled.");
                    throw new RuntimeException("The event has been cancelled.");
                }
            }
            // find event's ticket count (status = 'created')
            PreparedStatement ticketCountStmt = connection.prepareStatement(
                    "SELECT COUNT(t.rsvp_id) AS ticket_count " +
                            "FROM app.event e " +
                            "LEFT JOIN app.rsvp r ON r.event_id = e.id " +
                            "LEFT JOIN app.ticket t ON t.rsvp_id = r.id AND t.status = 'created' " +
                            "WHERE e.id = ?"
            );
            ticketCountStmt.setObject(1, UUID.fromString(event.getId()));
            ResultSet ticketCountResult = ticketCountStmt.executeQuery();

            int ticketCount = 0;
            if (ticketCountResult.next()) {
                ticketCount = ticketCountResult.getInt("ticket_count");
            }

            // find the new venue's capacity
            PreparedStatement venueCapacityStmt = connection.prepareStatement(
                    "SELECT capacity FROM app.venue WHERE id = ?"
            );
            venueCapacityStmt.setInt(1, event.getVenueId());
            ResultSet venueCapacityResult = venueCapacityStmt.executeQuery();

            int venueCapacity = 0;
            if (venueCapacityResult.next()) {
                venueCapacity = venueCapacityResult.getInt("capacity");
            }

            //  check if ticket count is more than venue's capacity
            if (ticketCount > venueCapacity) {
                // if the ticket's number is more than venue's capacity，set event's state cancelled
                PreparedStatement cancelEventStmt = connection.prepareStatement(
                        "UPDATE app.event SET status = ? WHERE id = ?"
                );
                cancelEventStmt.setString(1, "cancelled");
                cancelEventStmt.setObject(2, UUID.fromString(event.getId()));
                cancelEventStmt.executeUpdate();

                throw new RuntimeException("Cannot update event. Ticket count exceeds the new venue capacity. Event has been cancelled.");
            }


            // update
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE app.event SET title = ?, description = ?, venue_id = ?, date = ?, time = ?, cost =?, club_id =? WHERE id = ?"
            );
            statement.setString(1, event.getTitle());
            statement.setString(2, event.getDescription());
            statement.setInt(3, event.getVenueId());
            statement.setObject(4, new Date(event.getDate().getTime()));
            statement.setObject(5, Time.valueOf(event.getTime()));
            statement.setInt(6, event.getCost());
            statement.setInt(7, event.getClubId());
            statement.setObject(8, UUID.fromString(event.getId()));

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No rows were updated, event not found");
            }

        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to update event: %s", e.getMessage()), e);
        } finally {
            // Release the lock
            LockManagerWait.getInstance().releaseLock("event", Thread.currentThread().getName());
            connectionProvider.releaseConnection(connection);
        }
    }



    @Override
    public void cancel(String eventId){
        setCancel(eventId);
    }

    // cancel an event
    private void setCancel(String eventId) {
        var connection = connectionProvider.nextConnection();
        LockManagerWait.getInstance().acquireLock("event", Thread.currentThread().getName());
        try {
            PreparedStatement lockstatement = connection.prepareStatement(
                    "SELECT * FROM app.event WHERE id = ?"
            );
            lockstatement.setObject(1, UUID.fromString(eventId));
            ResultSet lockstatementResult = lockstatement.executeQuery();
            if (lockstatementResult.next()) {
                String stus = lockstatementResult.getString("status");
                if(stus.equals("cancelled")){
                    System.out.println("Event has been cancelled.");
                    throw new RuntimeException("The event has already been cancelled.");
                }
            }
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE app.event SET status = ? WHERE id = ?");
            statement.setString(1, "cancelled");
            statement.setObject(2, UUID.fromString(eventId));

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No rows were updated, event not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to cancel event: %s", e.getMessage()), e);
        } finally {
            LockManagerWait.getInstance().releaseLock("event", Thread.currentThread().getName());
            connectionProvider.releaseConnection(connection);
        }
    }

    @Override
    public Map<Event, Integer> getAll(){
        return selectAll();
    }

    private Map<Event, Integer> selectAll() {
        Map<Event, Integer> eventMap = new HashMap<>();

//        String query = "SELECT app.event.id, app.club.name, title, date, cost, capacity,app.event.club_id, app.event.venue_id, app.event.description, app.event.time " +
//                "FROM app.event INNER JOIN app.venue ON app.venue.id = app.event.venue_id INNER JOIN app.club on app.club.id = app.event.club_id " +
//                "WHERE app.event.status = 'created'";

//        String query = "SELECT e.id, c.name, e.title, e.date, e.cost, v.capacity, e.club_id, e.venue_id, e.description, e.time, COUNT(r.id) AS rsvp_count " +
//                "FROM app.event e " +
//                "INNER JOIN app.venue v ON v.id = e.venue_id " +
//                "INNER JOIN app.club c ON c.id = e.club_id " +
//                "LEFT JOIN app.rsvp r ON r.event_id = e.id " +
//                "WHERE e.status = 'created' " +
//                "GROUP BY e.id, c.name, e.title, e.date, e.cost, v.capacity, e.club_id, e.venue_id, e.description, e.time";

        String query = "SELECT e.id, c.name, e.title, e.date, e.cost, v.capacity, e.club_id, COUNT(t.rsvp_id) AS ticket_count " +
                "FROM app.event e " +
                "INNER JOIN app.venue v ON v.id = e.venue_id " +
                "INNER JOIN app.club c ON c.id = e.club_id " +
                "LEFT JOIN app.rsvp r ON r.event_id = e.id " +
                "LEFT JOIN app.ticket t ON t.rsvp_id = r.id AND t.status = 'created' " +  // Join ticket table and filter only created tickets
                "WHERE e.status = 'created' " +
                "GROUP BY e.id, c.name, e.title, e.date, e.cost, v.capacity, e.club_id";

        var connection = connectionProvider.nextConnection();

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Event event = new Event();
                event.setId(resultSet.getString("id"));
                event.setClubId(resultSet.getInt("club_id"));
                event.setClubName(resultSet.getString("name"));
                event.setTitle(resultSet.getString("title"));
                event.setDate(resultSet.getDate("date"));
                event.setCost(resultSet.getInt("cost"));
                event.setCapacity(resultSet.getInt("capacity"));

                // Get RSVP count
                int rsvpCount = resultSet.getInt("ticket_count");

                // Put the event and its RSVP count in the map
                eventMap.put(event, rsvpCount);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve all events", e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }

        return eventMap;
    }


    @Override
    public Event getDetails(Event event){ return
            getEventDetails(event);
    }

    private Event getEventDetails(Event event) {
        String query = "SELECT description, name, status, time " +
                "FROM app.event INNER JOIN app.venue ON app.event.venue_id = app.venue.id " +
                "WHERE app.event.id = ?";
        var connection = connectionProvider.nextConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Use the event's ID to fetch the details
            statement.setObject(1, UUID.fromString(event.getId()));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Populate the event object with the detailed data
                event.setDescription(resultSet.getString("description"));
                event.setVenueName(resultSet.getString("name"));
                event.setStatus(Event.Status.valueOf(resultSet.getString("status")));
                event.setTime(resultSet.getTime("time").toLocalTime());
            } else {
                throw new RuntimeException("Event not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to retrieve event details: %s", e.getMessage()), e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }

        return event;  // Return the same event object, now populated with additional details
    }
    @Override
    public Map<Event, Integer> getAllAdminEvent(List<Integer> ClubIDs){
        return selectAllAdminEvent(ClubIDs);
    }

    private Map<Event, Integer> selectAllAdminEvent(List<Integer> ClubIDs) {
        Map<Event, Integer> eventMap = new HashMap<>();
        var connection = connectionProvider.nextConnection();
        try{
        PreparedStatement statement = connection.prepareStatement(
                "SELECT e.id, c.name, e.title, e.date, e.cost, v.capacity, e.club_id, COUNT(t.rsvp_id) AS ticket_count " +
                        "FROM app.event e " +
                        "INNER JOIN app.venue v ON v.id = e.venue_id " +
                        "INNER JOIN app.club c ON c.id = e.club_id " +
                        "LEFT JOIN app.rsvp r ON r.event_id = e.id " +
                        "LEFT JOIN app.ticket t ON t.rsvp_id = r.id AND t.status = 'created' " +  // Join ticket table and filter only created tickets
                        "WHERE e.status = 'created' and e.club_id = ? " +
                        "GROUP BY e.id, c.name, e.title, e.date, e.cost, v.capacity, e.club_id"
        );
        for (Integer clubId : ClubIDs) {
            statement.setInt(1, clubId);
            ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Event event = new Event();
                    event.setId(resultSet.getString("id"));
                    event.setClubId(resultSet.getInt("club_id"));
                    event.setClubName(resultSet.getString("name"));
                    event.setTitle(resultSet.getString("title"));
                    event.setDate(resultSet.getDate("date"));
                    event.setCost(resultSet.getInt("cost"));
                    event.setCapacity(resultSet.getInt("capacity"));

                    // Get RSVP count
                    int rsvpCount = resultSet.getInt("ticket_count");

                    // Put the event and its RSVP count in the map
                    eventMap.put(event, rsvpCount);
                }

        }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
        return eventMap;
    }


}
