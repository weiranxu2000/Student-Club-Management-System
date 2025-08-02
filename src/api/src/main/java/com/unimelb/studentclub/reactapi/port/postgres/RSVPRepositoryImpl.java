package com.unimelb.studentclub.reactapi.port.postgres;

import com.unimelb.studentclub.reactapi.domain.Event;
import com.unimelb.studentclub.reactapi.domain.RSVP;
import com.unimelb.studentclub.reactapi.domain.RSVPRepository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.UUID;

public class RSVPRepositoryImpl implements RSVPRepository {
    private final ConnectionProvider connectionProvider;

    public RSVPRepositoryImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void create(RSVP rsvp) {
        insert(rsvp);
    }

    // Insert an rsvp into database.
    private void insert(RSVP rsvp){
        var connection = connectionProvider.nextConnection();

        // Lock the RSVP table to prevent conflicts.
        LockManagerWait.getInstance().acquireLock("rsvp", Thread.currentThread().getName());

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO app.rsvp (id, student_id, event_id) VALUES (?, ?, ?)"
            );
            statement.setObject(1, UUID.fromString(rsvp.getId()));
            statement.setInt(2, rsvp.getStudentId());
            statement.setObject(3, UUID.fromString(rsvp.getEventId()));
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(String.format("failed to insert new rsvp: %s", e.getMessage()), e);
        } finally {
            LockManagerWait.getInstance().releaseLock("rsvp", Thread.currentThread().getName());
            connectionProvider.releaseConnection(connection);
        }
    }
}
