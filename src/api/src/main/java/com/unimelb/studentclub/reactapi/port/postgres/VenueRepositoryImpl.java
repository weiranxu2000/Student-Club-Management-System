package com.unimelb.studentclub.reactapi.port.postgres;

import com.unimelb.studentclub.reactapi.domain.VenueRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VenueRepositoryImpl implements VenueRepository {
    private final ConnectionProvider connectionProvider;

    public VenueRepositoryImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }


    @Override
    public int getId(String venueName) {
        return findId(venueName);
    }

    private int findId (String venueName) {
        var connection = connectionProvider.nextConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT id FROM app.venue WHERE name = ?"
            );
            statement.setString(1, venueName);
            ResultSet resultSet = statement.executeQuery();

            // Judge if the club exists.
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                throw new RuntimeException("Venue with name '" + venueName + "' does not exist.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
    }

    @Override
    public int getCapacity(String venueName) {
        return findCapacity(venueName);
    }

    private int findCapacity (String venueName) {
        var connection = connectionProvider.nextConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT capacity FROM app.venue WHERE name = ?"
            );
            statement.setString(1, venueName);
            ResultSet resultSet = statement.executeQuery();

            // Judge if the club exists.
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                throw new RuntimeException("Venue with name '" + venueName + "' does not exist.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
    }
}
