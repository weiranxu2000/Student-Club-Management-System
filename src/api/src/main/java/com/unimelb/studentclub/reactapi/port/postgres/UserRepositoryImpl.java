package com.unimelb.studentclub.reactapi.port.postgres;

import com.unimelb.studentclub.reactapi.domain.AppUser;
import com.unimelb.studentclub.reactapi.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserRepositoryImpl implements UserRepository {
    private final ConnectionProvider connectionProvider;


    public UserRepositoryImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    //@PostConstruct
    public void init() {
        System.out.println("UserRepositoryImpl bean has been initialized!");
    }

    @Override
    public String getPasswordById(int id, String role) {
        return findPassword(id, role);
    }

    private String findPassword (int id, String role) {
        var connection = connectionProvider.nextConnection();
        String query = "";
        if (role.equals("Student")) {
            query = "SELECT password FROM app.user WHERE student_id = ?";
        } else if (role.equals("Faculty Administrator")) {
            query = "SELECT password FROM app.user WHERE admin_id = ?";
        }

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            // Judge if the club exists.
            if (resultSet.next()) {
                return resultSet.getString("password");
            } else {
                //throw new RuntimeException("User with username '" + id + "' does not exist.");
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
    }


    @Override
    public AppUser findById(int id) {
        return findUserById(id);
    }

    private AppUser findUserById(int id) {
        var connection = connectionProvider.nextConnection();
        String query = "SELECT e_mail, student_id, admin_id, password, name FROM app.user WHERE student_id = ? OR admin_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.setInt(2, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Dynamically set roles based on whether or not you have a student_id or admin_id.
                String role = (resultSet.getInt("student_id") != 0) ? "STUDENT" : "ADMIN";

                System.out.println("你好"+role);

                // Returns the complete AppUser object with dynamically generated roles
                return new AppUser(
                        resultSet.getString("e_mail"),
                        resultSet.getInt("student_id"),
                        resultSet.getInt("admin_id"),
                        resultSet.getString("password"),
                        resultSet.getString("name"),
                        role  // Dynamically generated roles
                );
            } else {
                throw new RuntimeException("User with id '" + id + "' does not exist.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
    }
}
