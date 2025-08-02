package com.unimelb.studentclub.reactapi.port.postgres;

import com.unimelb.studentclub.reactapi.domain.ClubRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClubRepositoryImpl implements ClubRepository {
    private final ConnectionProvider connectionProvider;

    public ClubRepositoryImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public int getId(String clubName) {
        return findId(clubName);
    }

    private int findId(String clubName) {
        var connection = connectionProvider.nextConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT id FROM app.club WHERE name = ?"
            );
            statement.setString(1, clubName);
            ResultSet resultSet = statement.executeQuery();

            // Judge if the club exists.
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                throw new RuntimeException("Club with name '" + clubName + "' does not exist.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
    }
}
