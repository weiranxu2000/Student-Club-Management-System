package com.unimelb.studentclub.reactapi.domain;

import com.unimelb.studentclub.reactapi.port.postgres.UserRepositoryImpl;
import com.unimelb.studentclub.reactapi.port.postgres.StudentClubRepositoryImpl;
import com.unimelb.studentclub.reactapi.port.postgres.EventRepositoryImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    private final UserRepositoryImpl userMapper;
    private final StudentClubRepositoryImpl studentclubMapper;
    private final EventRepositoryImpl eventMapper;
    public UserService(UserRepositoryImpl userMapper, StudentClubRepositoryImpl studentclubMapper, EventRepositoryImpl eventMapper) {
        this.userMapper = userMapper;
        this.studentclubMapper = studentclubMapper;
        this.eventMapper = eventMapper;
    }

    // Get the password
    public boolean isStudentAdmin(int id) throws IOException, ParseException {
        // Analyze request.
        if(studentclubMapper.isStudentAdmin(id)){
            return true;
        }
        else{
            return false;
        }
        //return passwords;
    }

    public List<String> getPassword(HttpServletRequest req) throws IOException, ParseException {
        // Analyze request.
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        JSONObject json = new JSONObject(sb.toString());
        int id = json.getInt("id");
        String role = json.getString("role");
        String password = json.getString("password");
        System.out.println(id+" "+role+" "+password);

        List<String> passwords = new ArrayList<>();
        passwords.add(password);
        passwords.add(userMapper.getPasswordById(id, role));
        System.out.println(passwords);

        if(studentclubMapper.isStudentAdmin(id)){
            passwords.add("true");
        }
        else{
            passwords.add("false");
        }

        passwords.add(String.valueOf(id));

        return passwords;
    }

   // @Override
//    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
//        int userId = Integer.parseInt(id);
//
//        AppUser appUser = userMapper.findById(userId);
//        if (appUser == null) {
//            throw new UsernameNotFoundException("user not found");
//        }
//
//        // Converting the queried AppUser to a Spring Security UserDetails object
//        return new org.springframework.security.core.userdetails.User(
//                appUser.getEmail(),
//                appUser.getPassword(),
//                List.of(new SimpleGrantedAuthority(appUser.getRole()))
//        );
//    }

    public Map<Event, Integer> getAllAdminEvents(HttpServletRequest req) {
        int studentID = Integer.parseInt(req.getParameter("id"));
        List<Integer> ClubIDs = studentclubMapper.clubIDOfStudent(studentID);  // Fetch all events from the repository
        Map<Event, Integer> adminEvents = eventMapper.getAllAdminEvent(ClubIDs);
        return adminEvents;
    }
    public Map<AppUser, Integer> getAllRelevantAdmin(HttpServletRequest req) {
        int studentID = Integer.parseInt(req.getParameter("id"));
        List<Integer> ClubIDs = studentclubMapper.clubIDOfStudent(studentID);  // Fetch all events from the repository
        List<Integer> adminIDs = studentclubMapper.studentIDOfClub(studentID,ClubIDs);
        List<String> adminNames = studentclubMapper.studentNameOfClub(adminIDs);
        Map<AppUser, Integer> relevantAdmins = new HashMap<>();
        return relevantAdmins;
    }

    public List<Map<String, Object>> getAllAdmin(HttpServletRequest req) {
        // Get the student_id parameter from the request
        int studentID = Integer.parseInt(req.getParameter("id"));

        // 调Call the findClubsById method to get information about the clubs managed by the student
        List<Map<String, Object>> adminClubInfo = studentclubMapper.getAdminClubs(studentID);

        // return clubs
        return adminClubInfo;
    }

    public void deleteAdmin(HttpServletRequest req)  throws IOException, ParseException {

        // Analyze request.
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // Get all the elements of rsvps in request.
        // Note the difference in naming between checking and front-end
        // No validation implemented right now.
        JSONObject json = new JSONObject(sb.toString());
        //int studentId = Integer.parseInt(json.getString("student_id"));
        int studentId = json.getInt("student_id");
        //String eventId = getElement(json, "event_id");
        String clubName = json.getString("club_name");
        System.out.println(studentId);
        System.out.println(clubName);

        //int studentID = Integer.parseInt(req.getParameter("student_id"));
        //String clubName = req.getParameter("club_name");
        studentclubMapper.deleteAdmin(studentId, clubName);
    }

    public void addAdmin(HttpServletRequest req) throws IOException {
        // Analyze request.
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // Get all the elements of rsvps in request.
        // Note the difference in naming between checking and front-end
        // No validation implemented right now.
        JSONObject json = new JSONObject(sb.toString());
        int studentId = Integer.parseInt(getElement(json, "student_id"));
        String adminId = new String(String.valueOf(json.getInt("admin_id")));
        //String eventId = getElement(json, "event_id");
        // List<String> attendees =
        String clubName = json.getString("club_name");
        System.out.println(studentId);
        System.out.println(clubName);

        //int studentID = Integer.parseInt(req.getParameter("student_id"));
        //String clubName = req.getParameter("club_name");

        studentclubMapper.addAdmin(adminId, studentId, clubName);
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
