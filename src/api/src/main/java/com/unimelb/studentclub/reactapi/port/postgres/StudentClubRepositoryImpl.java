package com.unimelb.studentclub.reactapi.port.postgres;

import com.unimelb.studentclub.reactapi.domain.StudentClubRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentClubRepositoryImpl implements StudentClubRepository {
    private final ConnectionProvider connectionProvider;
    public StudentClubRepositoryImpl(ConnectionProvider connectionProvider) {this.connectionProvider = connectionProvider;}

    public boolean isAdmin(int studentID, String clubName) {
        int clubID;
        var connection = connectionProvider.nextConnection();
        try {
            PreparedStatement findClubIDstatement = connection.prepareStatement(
                    "SELECT id FROM app.club WHERE name = ?"
            );
            findClubIDstatement.setString(1, clubName);
            ResultSet resultSet = findClubIDstatement.executeQuery();

            // Judge if the club exists.
            if (resultSet.next()) {
                clubID = resultSet.getInt("id");
            } else {
                throw new RuntimeException("Club with name '" + clubName + "' does not exist.");
            }

            PreparedStatement statement = connection.prepareStatement(
                    "SELECT student_id FROM app.student_club WHERE student_id = ? AND club_id = ? and is_admin = true"
            );
            statement.setInt(1, studentID);
            statement.setInt(2,clubID);
            ResultSet result = statement.executeQuery();
            return result.next();

        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
    }
    public boolean isStudentAdmin(int studentID) {
        var connection = connectionProvider.nextConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT student_id FROM app.student_club WHERE student_id = ? AND is_admin = true"
            );
            statement.setInt(1, studentID);
            ResultSet result = statement.executeQuery();
            return result.next();
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
    }

    public List<Integer> clubIDOfStudent(int studentID) {
        var connection = connectionProvider.nextConnection();
        List<Integer> clubID = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT club_id FROM app.student_club WHERE student_id = ? AND is_admin = true"
            );
            statement.setInt(1, studentID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                clubID.add(resultSet.getInt("club_id"));
                // 获取其余的 club_id
                while (resultSet.next()) {
                    clubID.add(resultSet.getInt("club_id"));
                }
            } else {
                throw new RuntimeException("The student with id '" + studentID + "' does not belong to any club.");
            }
            return clubID;
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
    }
    public List<String> clubOfStudent(int studentID) {
        List<String> club = new ArrayList<>();
        List<Integer> clubID = new ArrayList<>();
        var connection = connectionProvider.nextConnection();
        try {
            PreparedStatement findClubNamestatement = connection.prepareStatement(
                    "SELECT club_id FROM app.student_club WHERE student_id = ? and is_admin = true"
            );
            findClubNamestatement.setInt(1, studentID);
            ResultSet resultSet = findClubNamestatement.executeQuery();

            // get first club_id
            if (resultSet.next()) {
                clubID.add(resultSet.getInt("club_id"));
                // get other club_ids
                while (resultSet.next()) {
                    clubID.add(resultSet.getInt("club_id"));
                }
            } else {
                throw new RuntimeException("The student with id '" + studentID + "' does not belong to any club.");
            }

            // query every club_id's club name
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT name FROM app.club WHERE id = ?"
            );
            for (Integer clubId : clubID) {
                statement.setInt(1, clubId);
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    club.add(result.getString("name"));
                } else {
                    throw new RuntimeException("The club with id '" + clubId + "' does not exist.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
        return club;
    }

    public List<Integer> studentIDOfClub(int studentID,List<Integer> ClubIDs){



        List<Integer> studentIDs = new ArrayList<>();
        var connection = connectionProvider.nextConnection();
        try {
            PreparedStatement findStudentIDstatement = connection.prepareStatement(
                    "SELECT student_id FROM app.student_club WHERE club_id = ? and is_admin = true and student_id != ?"
            );
            for (Integer clubID : ClubIDs){
            findStudentIDstatement.setInt(1, clubID);
            findStudentIDstatement.setInt(2, studentID);
            ResultSet resultSet = findStudentIDstatement.executeQuery();
            if (resultSet.next()) {studentIDs.add(resultSet.getInt("student_id"));
            }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
        return studentIDs;
    }

    public List<String> studentNameOfClub(List<Integer> studentIDs){
    // query student's name
        List<String> studentName = new ArrayList<>();
        var connection = connectionProvider.nextConnection();
    try {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT name FROM app.user WHERE student_id = ?"
        );
        for (Integer studentId : studentIDs) {
            statement.setInt(1, studentId);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                studentName.add(result.getString("name"));
            } else {
                throw new RuntimeException("The student with id '" + studentId + "' does not exist.");
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException("Database error: " + e.getMessage(), e);
    } finally {
        connectionProvider.releaseConnection(connection);
    }
    return studentName;
    }
    @Override
    public List<Map<String, Object>> getAdminClubs(int studentId){
        return findClubsById(studentId);
    };

    private List<Map<String, Object>> findClubsById(int studentId) {
        // Stores a list of returned results
        List<Map<String, Object>> resultList = new ArrayList<>();

        // SQL query
        String query = "SELECT sc2.student_id, u.name AS student_name, c.name AS club_name " +
                "FROM app.student_club sc1 " +
                "JOIN app.student_club sc2 ON sc1.club_id = sc2.club_id " +
                "JOIN app.club c ON sc1.club_id = c.id " +
                "JOIN app.user u ON sc2.student_id = u.student_id " +
                "WHERE sc1.student_id = ? " +
                "AND sc2.student_id != sc1.student_id " +
                "AND sc2.is_admin = true";

        var connection = connectionProvider.nextConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, studentId);  // filter by student_id
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                //find student_id, student_name and club_name
                int studentIdFromResult = resultSet.getInt("student_id");
                String studentName = resultSet.getString("student_name");
                String clubName = resultSet.getString("club_name");

                //create a Map to store a record
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("student_id", studentIdFromResult);
                resultMap.put("student_name", studentName);
                resultMap.put("club_name", clubName);

                // put Map into result
                resultList.add(resultMap);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve admin club information", e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }

        // return result
        return resultList;
    }

    @Override
    public void deleteAdmin(int student_id, String clubName){
        delete(student_id, clubName);
    };

    private void delete(int student_id, String clubName) {
        String findClubIdQuery = "SELECT id FROM app.club WHERE name = ?";
        String updateAdminStatusQuery = "UPDATE app.student_club SET is_admin = false WHERE student_id = ? AND club_id = ?";

        var connection = connectionProvider.nextConnection();

        try {
            // Step 1: Get club_id using the club name
            int clubId = 0;
            try (PreparedStatement findClubIdStatement = connection.prepareStatement(findClubIdQuery)) {
                findClubIdStatement.setString(1, clubName);
                ResultSet resultSet = findClubIdStatement.executeQuery();
                if (resultSet.next()) {
                    clubId = resultSet.getInt("id");
                } else {
                    throw new RuntimeException("Club not found with name: " + clubName);
                }
            }

            // Step 2: Update student_club table to set is_admin = false
            try (PreparedStatement updateAdminStatusStatement = connection.prepareStatement(updateAdminStatusQuery)) {
                updateAdminStatusStatement.setInt(1, student_id);
                updateAdminStatusStatement.setInt(2, clubId);
                int rowsUpdated = updateAdminStatusStatement.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new RuntimeException("No record found to update admin status");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete admin status", e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
    }

    @Override
    public void addAdmin(String originalid, int student_id, String clubName){
        insert(originalid,student_id, clubName);
    }

    public void insert(String originalid,int studentId, String clubName) {
        System.out.println(originalid);
        //String selectStudentIDClubQuery = "SELECT id FROM app.user WHERE name = ?";
        String selectClubQuery = "SELECT id FROM app.club WHERE name = ?";
        String selectOriginStudentClubQuery = "SELECT * FROM app.student_club WHERE student_id = ? AND club_id = ? and is_admin = true";
        String selectTargetStudentClubQuery = "SELECT * FROM app.student_club WHERE student_id = ? AND club_id = ?";
        String updateStudentClubQuery = "UPDATE app.student_club SET is_admin = true WHERE student_id = ? AND club_id = ?";
        String insertStudentClubQuery = "INSERT INTO app.student_club (student_id, club_id, is_admin) VALUES (?, ?, true)";

        var connection = connectionProvider.nextConnection();

        try {
            // get club_id by clubName
            int clubId = -1;
            try (PreparedStatement selectClubStmt = connection.prepareStatement(selectClubQuery)) {
                selectClubStmt.setString(1, clubName);
                ResultSet clubResult = selectClubStmt.executeQuery();
                if (clubResult.next()) {
                    clubId = clubResult.getInt("id");
                } else {
                    throw new RuntimeException("Club not found with the name: " + clubName);
                }
            }
            // Look for student_id and club_id records in the student_club table.
            try (PreparedStatement selectOriginStudentClubStmt = connection.prepareStatement(selectOriginStudentClubQuery)) {
                selectOriginStudentClubStmt.setInt(1, Integer.parseInt(originalid));
                selectOriginStudentClubStmt.setInt(2, clubId);
                ResultSet originStudentResult = selectOriginStudentClubStmt.executeQuery();

                if (originStudentResult.next()) {
                        try (PreparedStatement selectStudentClubStmt = connection.prepareStatement(selectTargetStudentClubQuery)) {
                            selectStudentClubStmt.setInt(1, studentId);
                            selectStudentClubStmt.setInt(2, clubId);
                            ResultSet studentClubResult = selectStudentClubStmt.executeQuery();

                            if (studentClubResult.next()) {
                                //  Update is_admin to true if it exists and is_admin is false.
                                if (!studentClubResult.getBoolean("is_admin")) {
                                    try (PreparedStatement updateStudentClubStmt = connection.prepareStatement(updateStudentClubQuery)) {
                                        updateStudentClubStmt.setInt(1, studentId);
                                        updateStudentClubStmt.setInt(2, clubId);
                                        updateStudentClubStmt.executeUpdate();
                                    }
                                } else {
                                    System.out.println("This student is already an admin for the club: " + clubName);
                                }
                            } else {
                                // if not exist insert new admin.
                                try (PreparedStatement insertStudentClubStmt = connection.prepareStatement(insertStudentClubQuery)) {
                                    insertStudentClubStmt.setInt(1, studentId);
                                    insertStudentClubStmt.setInt(2, clubId);
                                    insertStudentClubStmt.executeUpdate();
                                }
                            }
                        }
                } else {
                    throw new RuntimeException("This command is not coming from an admin of the club 1");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add admin: " + e.getMessage(), e);
        } finally {
            connectionProvider.releaseConnection(connection);
        }
    }





}
