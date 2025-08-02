package com.unimelb.studentclub.reactapi.port.postgres;

import com.unimelb.studentclub.reactapi.domain.RSVP;
import com.unimelb.studentclub.reactapi.domain.Ticket;
import com.unimelb.studentclub.reactapi.domain.TicketRepository;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.sql.Date;
import java.sql.Time;

public class TicketRepositoryImpl implements TicketRepository {
    private final ConnectionProvider connectionProvider;

    public TicketRepositoryImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public boolean create(Ticket ticket){
        return insert(ticket);
    }

    // Insert a ticket into database.
    private boolean insert(Ticket ticket) {
        var connection = connectionProvider.nextConnection();

        // Lock the table 'ticket' to prevent conflicts.
        LockManagerWait.getInstance().acquireLock("ticket", Thread.currentThread().getName());

        try {
            // query the ticket table for all the rsvp_id's of the student and their statuses.
            String checkQuery = "SELECT r.event_id, t.status FROM app.rsvp r " +
                    "JOIN app.ticket t ON r.id = t.rsvp_id " +
                    "WHERE t.student_id = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setInt(1, ticket.getStudentId());
            ResultSet resultSet = checkStatement.executeQuery();

            // Get the event_id of the currently inserted rsvp.
            String currentEventQuery = "SELECT event_id FROM app.rsvp WHERE id = ?";
            PreparedStatement eventStatement = connection.prepareStatement(currentEventQuery);
            eventStatement.setObject(1, UUID.fromString(ticket.getRsvpId()));
            ResultSet currentEventResult = eventStatement.executeQuery();

            boolean eventExists = false;
            String currentEventId = "";
            if (currentEventResult.next()) {
                currentEventId = currentEventResult.getString("event_id");

                // Check if there is a ticket with the same event_id and created status.
                while (resultSet.next()) {
                    String existingEventId = resultSet.getString("event_id");
                    String status = resultSet.getString("status");

                    // If the event_id is the same and the status is created, the insertion is blocked.
                    if (existingEventId.equals(currentEventId) && status.equals("created")) {
                        eventExists = true;
                        break;
                    }
                }
            }

            if (eventExists) {
                return false;
                //throw new RuntimeException("Ticket for this event already exists for the student.");
            }

            // Ticket exceeds the limit
            // String currentEventId = currentEventResult.getString("event_id");
            // find event's ticket count (status = 'created')
            PreparedStatement ticketCountStmt = connection.prepareStatement(
                    "SELECT COUNT(t.rsvp_id) AS ticket_count " +
                            "FROM app.event e " +
                            "LEFT JOIN app.rsvp r ON r.event_id = e.id " +
                            "LEFT JOIN app.ticket t ON t.rsvp_id = r.id AND t.status = 'created' " +
                            "WHERE e.id = ?"
            );
            ticketCountStmt.setObject(1, UUID.fromString(currentEventId));
            ResultSet ticketCountResult = ticketCountStmt.executeQuery();

            int ticketCount = 0;
            if (ticketCountResult.next()) {
                ticketCount = ticketCountResult.getInt("ticket_count");
            }

            // find event's venue_id
            PreparedStatement venueIdStmt = connection.prepareStatement(
                    "SELECT venue_id FROM app.event WHERE id = ?"
            );
            venueIdStmt.setObject(1, UUID.fromString(currentEventId));
            ResultSet venueIdResult = venueIdStmt.executeQuery();


            int currentVenueId = 0;
            if (venueIdResult.next()) {
                currentVenueId = venueIdResult.getInt("venue_id");
            }

            // find the venue's capacity
            PreparedStatement venueCapacityStmt = connection.prepareStatement(
                    "SELECT capacity FROM app.venue WHERE id = ?"
            );
            venueCapacityStmt.setInt(1, currentVenueId);
            ResultSet venueCapacityResult = venueCapacityStmt.executeQuery();


            int venueCapacity = 0;
            if (venueCapacityResult.next()) {
                venueCapacity = venueCapacityResult.getInt("capacity");
            }

            if (ticketCount >= venueCapacity) {
                return false;
                //throw new RuntimeException("Ticket limit exceeded for this event.");
            }

            // If there are no conflicts, insert a new ticket
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO app.ticket (rsvp_id, student_id, status) VALUES (?, ?, ?)"
            );
            statement.setObject(1, UUID.fromString(ticket.getRsvpId()));
            statement.setInt(2, ticket.getStudentId());
            statement.setString(3, ticket.getStatus().name());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to insert new ticket: %s", e.getMessage()), e);
        } finally {
            LockManagerWait.getInstance().releaseLock("ticket", Thread.currentThread().getName());
            connectionProvider.releaseConnection(connection);
        }

        return true;
    }





    @Override
    public void cancel(Ticket ticket){
        setCancel(ticket);
    }

    // cancel a ticket
    private void setCancel(Ticket ticket) {
        var connection = connectionProvider.nextConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE app.ticket SET status = ? WHERE rsvp_id = ? and student_id = ?"
        )) {
            statement.setString(1, "cancelled");
            statement.setObject(2, UUID.fromString(ticket.getRsvpId()));
            statement.setInt(3, ticket.getStudentId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No rows were updated, ticket not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to cancel ticket: %s", e.getMessage()), e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
    }

    @Override
    public Map<Ticket, String> getAll(int studentId){
        return selectAllByStudentId(studentId);
    }

    private Map<Ticket, String> selectAllByStudentId(int studentId) {
        Map<Ticket, String> ticketsWithEventIds = new HashMap<>();
        String query = "SELECT t.rsvp_id, t.student_id, r.event_id " +
                "FROM app.ticket t " +
                "JOIN app.rsvp r ON t.rsvp_id = r.id " +
                "WHERE r.student_id = ? AND t.status = 'created'";
        var connection = connectionProvider.nextConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, studentId);  // 通过 student_id 过滤
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                // create Ticket
                Ticket ticket = new Ticket();
                ticket.setRSVPId(resultSet.getString("rsvp_id"));  // get rsvp_id
                ticket.setStudentId(resultSet.getInt("student_id"));  // get student_id

                // get event_id
                String eventId = resultSet.getString("event_id");

                // Combine ticket and event_id into Map
                ticketsWithEventIds.put(ticket, eventId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve tickets by student ID", e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }

        return ticketsWithEventIds;
    }

}
