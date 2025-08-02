package com.unimelb.studentclub.reactapi.domain;

import com.unimelb.studentclub.reactapi.port.postgres.ApplicationRepositoryImpl;
import com.unimelb.studentclub.reactapi.port.postgres.ClubRepositoryImpl;
import com.unimelb.studentclub.reactapi.port.postgres.StudentClubRepositoryImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ApplicationService {
    private final ApplicationRepositoryImpl applicationMapper;
    private final ClubRepositoryImpl clubMapper;
    private final StudentClubRepositoryImpl studentclubMapper;

    public ApplicationService(ApplicationRepositoryImpl applicationMapper, ClubRepositoryImpl clubMapper,StudentClubRepositoryImpl studentclubMapper) {
        this.applicationMapper = applicationMapper;
        this.clubMapper = clubMapper;
        this.studentclubMapper = studentclubMapper;
    }
    public Application create(HttpServletRequest req) throws IOException, ParseException{
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
        //int applicationUserID = json.getInt("user_id");
        String applicationClubName = json.getString("club_name");
        String applicationDescription = getElement(json, "description");
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Date applicationDate = dateFormat.parse(getElement(json, "date"));
        int applicationAmount = Integer.parseInt(getElement(json, "amount"));
        //Application.Status status = Application.Status.valueOf(getElement(json, "status"));
        int admin_id = json.getInt("admin_id");

        boolean canBeCreatedByThisStudent = studentclubMapper.isAdmin(admin_id, applicationClubName);

        // Use data mapper to get data from or insert data into database.
        // Get club id
//        boolean isAdmin = studentclubMapper.isAdmin(applicationUserID,applicationClubName);
//        if(!isAdmin){
//            return null;
//        }
        int applicationClubID = clubMapper.getId(applicationClubName);
        if (!canBeCreatedByThisStudent){
            return null;
        }

        var application = new Application();
        application.setId(UUID.randomUUID().toString());
        application.setDescription(applicationDescription);
        application.setAmount(applicationAmount);
        application.setDate(ZonedDateTime.now());
        application.setStatus(Application.Status.submitted);
        application.setClubId(applicationClubID);

        applicationMapper.create(application);
        return application;
    }

    public Application modify(HttpServletRequest req) throws IOException, ParseException{
        Application application = new Application();
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // Parse the JSON request.
        JSONObject json = new JSONObject(sb.toString());

        String applicationId = getElement(json, "id");
        String applicationDescription = getElement(json, "description");
        String applicationClubName = json.getString("clubName");
        int applicationAmount = Integer.parseInt(getElement(json, "amount"));
        int applicationClubID = clubMapper.getId(applicationClubName);
        application.setId(applicationId);
        application.setDescription(applicationDescription);
        application.setAmount(applicationAmount);
        application.setDate(ZonedDateTime.now());
        application.setStatus(Application.Status.submitted);
        application.setClubId(applicationClubID);

        applicationMapper.modify(application);
        return application;
    }

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

        // Get the application ID from the request
        String applicationId = getElement(json, "id"); // Ensure the front-end provides the event ID

        // Call the cancel method to cancel the application in the database
        applicationMapper.cancel(applicationId);
    }

    public void approve(HttpServletRequest req) throws IOException {
        // Analyze request.
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // Parse the JSON data
        JSONObject json = new JSONObject(sb.toString());

        // Get the application ID from the request
        String applicationId = getElement(json, "id"); // Ensure the front-end provides the event ID

        // Call the cancel method to cancel the application in the database
        applicationMapper.approve(applicationId);
    }

    public void reject(HttpServletRequest req) throws IOException {
        // Analyze request.
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // Parse the JSON data
        JSONObject json = new JSONObject(sb.toString());

        // Get the application ID from the request
        String applicationId = getElement(json, "id"); // Ensure the front-end provides the event ID

        // Call the cancel method to cancel the application in the database
        applicationMapper.reject(applicationId);
    }
    public void review(HttpServletRequest req) throws IOException {
        // Analyze request.
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // Parse the JSON data
        JSONObject json = new JSONObject(sb.toString());

        // Get the application ID from the request
        String applicationId = getElement(json, "id"); // Ensure the front-end provides the event ID

        // Call the cancel method to cancel the application in the database
        applicationMapper.review(applicationId);
    }

    public List<Application> getAllAdminApplication(HttpServletRequest req) {
        int studentID = Integer.parseInt(req.getParameter("id"));
        List<Integer> ClubIDs = studentclubMapper.clubIDOfStudent(studentID);  // Fetch all events from the repository
        List<Application> adminApplications = applicationMapper.getAllAdminApplication(ClubIDs);
        return adminApplications;
    }

    public List<Application> getAllApplication(HttpServletRequest req){
        List<Application> applications = applicationMapper.getAllApplication();
        return applications;
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