package com.unimelb.studentclub.reactapi.port.postgres;

import com.unimelb.studentclub.reactapi.domain.Application;
import com.unimelb.studentclub.reactapi.domain.ApplicationRepository;
import com.unimelb.studentclub.reactapi.domain.Event;

import java.sql.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.ZoneId;


public class ApplicationRepositoryImpl implements ApplicationRepository {
    private final ConnectionProvider connectionProvider;
    public ApplicationRepositoryImpl(ConnectionProvider connectionProvider) {this.connectionProvider = connectionProvider;}

    @Override
    public void create(Application application){
        insert(application);
    }

    private void insert(Application application){
        var connection = connectionProvider.nextConnection();

        // Lock the table 'fund' to prevent conflicts.
        LockManagerWait.getInstance().acquireLock("fund", Thread.currentThread().getName());

        try {
            // First, check if there is an fund application in this semester.
            ZonedDateTime melbourneTime = ZonedDateTime.now(ZoneId.of("Australia/Sydney"));
            System.out.println(melbourneTime);
            // Simple Logic: month 7-11 is one semester, 2-6 is another semester.
            int month = melbourneTime.getMonthValue();
            int clubId = application.getClubId();
            if (month >= 7 && month <= 11) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT COUNT(*) FROM app.fund WHERE EXTRACT(MONTH FROM time) BETWEEN 7 AND 11 AND EXTRACT(YEAR FROM time) = ? AND club_id = ? AND status NOT IN ('cancelled', 'rejected', 'inDraft');"
                );
                statement.setInt(1, melbourneTime.getYear());
                statement.setInt(2, clubId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    throw new RuntimeException("An application already exists for this semester.");
                }
            } else if (month >= 2 && month <= 6) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT COUNT(*) FROM app.fund WHERE EXTRACT(MONTH FROM time) BETWEEN 2 AND 6 AND EXTRACT(YEAR FROM time) = ? AND club_id = ? AND status NOT IN ('cancelled', 'rejected', 'inDraft');"
                );
                statement.setInt(1, melbourneTime.getYear());
                statement.setInt(2, clubId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    throw new RuntimeException("An application already exists for this semester.");
                }
            } else {
                throw new RuntimeException("Not a semester time.");
            }

            // Now insert the fund application.
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO app.fund (id, description, amount, time, club_id, status) VALUES (?, ?, ?, ?, ?, ?)"
            );
            statement.setObject(1, UUID.fromString(application.getId()));
            statement.setString(2, application.getDescription());
            statement.setInt(3, application.getAmount());
            //statement.setObject(4, new Date(application.getDate().getTime()));
//            ZonedDateTime zonedDateTime = application.getDate(); // Assuming application.getDate() returns ZonedDateTime
//            statement.setTimestamp(4, Timestamp.from(zonedDateTime.toInstant())); // Convert ZonedDateTime to Timestamp

            // set time
            //statement.setObject(4, melbourneTime);
            statement.setTimestamp(4, Timestamp.from(melbourneTime.toInstant()));
            statement.setInt(5, application.getClubId());
            statement.setString(6, application.getStatus().name());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(String.format("failed to insert new application: %s", e.getMessage()), e);
        } finally {
            LockManagerWait.getInstance().releaseLock("fund", Thread.currentThread().getName());
            connectionProvider.releaseConnection(connection);
        }
    }

    @Override
    public void modify(Application application){
        update(application);
    }

    private void update(Application application) {
        var connection = connectionProvider.nextConnection();
        // Lock the table 'fund' to prevent conflicts.
        LockManagerWait.getInstance().acquireLock("fund", Thread.currentThread().getName());
        try {
            PreparedStatement lockstatement = connection.prepareStatement("SELECT * FROM app.fund WHERE id = ?");
            lockstatement.setObject(1, UUID.fromString(application.getId()));
            ResultSet resultSet = lockstatement.executeQuery();
            if (resultSet.next()) {
                String status = resultSet.getString("status");
                if (status.equals("rejected")) {
                    throw new RuntimeException("This Application has already been rejected.");
                }
                else if (status.equals("approved")) {
                    throw new RuntimeException("This Application has already been approved.");
                }
                else if (status.equals("cancelled")) {
                    throw new RuntimeException("This Application has already been cancelled.");
                }
            }
            // check if the student is the admin of the club
            PreparedStatement checkStatement = connection.prepareStatement(
                    "SELECT club_id FROM app.student_club WHERE student_id = ?"
            );

            // update
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE app.fund SET description = ?, amount = ?, time = ?, status =?, club_id =? WHERE id = ?"
            );
            statement.setString(1, application.getDescription());
            statement.setInt(2, application.getAmount());
            ZonedDateTime melbourneTime = ZonedDateTime.now(ZoneId.of("Australia/Sydney"));
            //statement.setObject(4, melbourneTime);
            statement.setTimestamp(3, Timestamp.from(melbourneTime.toInstant()));
            statement.setString(4, application.getStatus().name());
            statement.setInt(5, application.getClubId());
            statement.setObject(6, UUID.fromString(application.getId()));
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No rows were updated, application not found");
            }

        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to update application: %s", e.getMessage()), e);
        } finally {
            LockManagerWait.getInstance().releaseLock("fund", Thread.currentThread().getName());
            connectionProvider.releaseConnection(connection);
        }
    }

    @Override
    public void cancel(String applicationId){delete(applicationId);}

    private void delete(String applicationId){
        var connection = connectionProvider.nextConnection();
        LockManagerWait.getInstance().acquireLock("fund", Thread.currentThread().getName());
        try {
            PreparedStatement lockstatement = connection.prepareStatement("SELECT * FROM app.fund WHERE id = ?");
            lockstatement.setObject(1, UUID.fromString(applicationId));
            ResultSet resultSet = lockstatement.executeQuery();
            if (resultSet.next()) {
                String status = resultSet.getString("status");
                if (status.equals("rejected")) {
                    throw new RuntimeException("This Application has already been rejected.");
                }
                else if (status.equals("approved")) {
                    throw new RuntimeException("This Application has already been approved.");
                }
                else if (status.equals("cancelled")) {
                    throw new RuntimeException("This Application has already been cancelled.");
                }
            }
            PreparedStatement statement = connection.prepareStatement("UPDATE app.fund SET status = ? WHERE id = ?");
            statement.setString(1, "cancelled");
            statement.setObject(2, UUID.fromString(applicationId));

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No rows were updated, application not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to cancel application: %s", e.getMessage()), e);
        } finally {
            LockManagerWait.getInstance().releaseLock("fund", Thread.currentThread().getName());
            connectionProvider.releaseConnection(connection);
        }
    }

    @Override
    public void approve(String applicationId){agree(applicationId);}

    private void agree(String applicationId){
        var connection = connectionProvider.nextConnection();
        LockManagerWait.getInstance().acquireLock("fund", Thread.currentThread().getName());
        try {
            PreparedStatement lockstatement = connection.prepareStatement("SELECT * FROM app.fund WHERE id = ?");
            lockstatement.setObject(1, UUID.fromString(applicationId));
            ResultSet resultSet = lockstatement.executeQuery();
            if (resultSet.next()) {
                String status = resultSet.getString("status");
                if (status.equals("rejected")) {
                    throw new RuntimeException("This Application has already been rejected.");
                }
                else if (status.equals("approved")) {
                    throw new RuntimeException("This Application has already been approved.");
                }
                else if (status.equals("cancelled")) {
                    throw new RuntimeException("This Application has already been cancelled.");
                }
            }
            PreparedStatement statement = connection.prepareStatement("UPDATE app.fund SET status = ? WHERE id = ?");
            statement.setString(1, "approved");
            statement.setObject(2, UUID.fromString(applicationId));

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No rows were updated, application not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to cancel application: %s", e.getMessage()), e);
        } finally {
            LockManagerWait.getInstance().releaseLock("fund", Thread.currentThread().getName());
            connectionProvider.releaseConnection(connection);
        }
    }

    @Override
    public void reject(String applicationId){disagree(applicationId);}

    private void disagree(String applicationId){
        var connection = connectionProvider.nextConnection();
        LockManagerWait.getInstance().acquireLock("fund", Thread.currentThread().getName());
        try {
            PreparedStatement lockstatement = connection.prepareStatement("SELECT * FROM app.fund WHERE id = ?");
            lockstatement.setObject(1, UUID.fromString(applicationId));
            ResultSet resultSet = lockstatement.executeQuery();
            if (resultSet.next()) {
                String status = resultSet.getString("status");
                if (status.equals("rejected")) {
                    throw new RuntimeException("This Application has already been rejected.");
                }
                else if (status.equals("approved")) {
                    throw new RuntimeException("This Application has already been approved.");
                }
                else if (status.equals("cancelled")) {
                    throw new RuntimeException("This Application has already been cancelled.");
                }
            }
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE app.fund SET status = ? WHERE id = ?");
            statement.setString(1, "rejected");
            statement.setObject(2, UUID.fromString(applicationId));

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No rows were updated, application not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to cancel application: %s", e.getMessage()), e);
        } finally {
            LockManagerWait.getInstance().releaseLock("fund", Thread.currentThread().getName());
            connectionProvider.releaseConnection(connection);
        }
    }
    @Override
    public void review(String applicationId){look(applicationId);}

    private void look(String applicationId){
        var connection = connectionProvider.nextConnection();
        LockManagerWait.getInstance().acquireLock("fund", Thread.currentThread().getName());
        try {
            PreparedStatement lockstatement = connection.prepareStatement("SELECT * FROM app.fund WHERE id = ?");
            lockstatement.setObject(1, UUID.fromString(applicationId));
            ResultSet resultSet = lockstatement.executeQuery();
            if (resultSet.next()) {
                String status = resultSet.getString("status");
                if (status.equals("rejected")) {
                    throw new RuntimeException("This Application has already been rejected.");
                }
                else if (status.equals("approved")) {
                    throw new RuntimeException("This Application has already been approved.");
                }
                else if (status.equals("cancelled")) {
                    throw new RuntimeException("This Application has already been cancelled.");
                }
//                else if (status.equals("inReview")) {
//                    throw new RuntimeException("This Application has already been reviewed.");
//                }
            }
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE app.fund SET status = ? WHERE id = ?");
            statement.setString(1, "inReview");
            statement.setObject(2, UUID.fromString(applicationId));

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No rows were updated, application not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Failed to review application: %s", e.getMessage()), e);
        } finally {
            LockManagerWait.getInstance().releaseLock("fund", Thread.currentThread().getName());
            connectionProvider.releaseConnection(connection);
        }
    }

    @Override
    public List<Application> getAllAdminApplication(List<Integer> ClubIDs){return selectAllAdminApplication(ClubIDs);}

    private List<Application> selectAllAdminApplication(List<Integer> ClubIDs){
        List<Application> applications = new ArrayList<>();
        var connection = connectionProvider.nextConnection();
        try{
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM app.fund e INNER JOIN app.club c on c.id = e.club_id WHERE e.club_id = ? AND e.status != 'cancelled'"
            );
            for (Integer clubId : ClubIDs) {
                statement.setInt(1, clubId);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Application application = new Application();
                    application.setId(resultSet.getString("id"));
                    application.setClubId(resultSet.getInt("club_id"));
                    application.setDescription(resultSet.getString("description"));
                    ZonedDateTime melbourneTime = resultSet.getTimestamp("time").toInstant().atZone(ZoneId.of("Australia/Sydney"));
                    application.setDate(melbourneTime);
                    application.setAmount(resultSet.getInt("amount"));
                    application.setStatus(Application.Status.valueOf(resultSet.getString("status")));
                    application.setClubName(resultSet.getString("name"));

                    // Put the event and its RSVP count in the map
                    applications.add(application);
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
        return applications;
    }

    @Override
    public List<Application> getAllApplication(){
        return selectAllApplication();
    }

    private List<Application> selectAllApplication(){
        var connection = connectionProvider.nextConnection();
        List<Application> applications = new ArrayList<>();
        try{
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM app.fund e INNER JOIN app.club c on c.id = e.club_id WHERE e.status IN ('submitted', 'inReview')"
            );

            ResultSet resultSet = statement.executeQuery();


            while (resultSet.next()) {
                    Application application = new Application();
                    application.setId(resultSet.getString("id"));
                    application.setClubId(resultSet.getInt("club_id"));
                    application.setDescription(resultSet.getString("description"));
                    ZonedDateTime melbourneTime = resultSet.getTimestamp("time").toInstant().atZone(ZoneId.of("Australia/Sydney"));
                    application.setDate(melbourneTime);
                    application.setAmount(resultSet.getInt("amount"));
                    application.setStatus(Application.Status.valueOf(resultSet.getString("status")));
                    application.setClubName(resultSet.getString("name"));

                    // Put the event and its RSVP count in the map
                    applications.add(application);
                }

        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
        return applications;
    }
}
