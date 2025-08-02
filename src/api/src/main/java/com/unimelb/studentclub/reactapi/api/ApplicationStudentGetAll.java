package com.unimelb.studentclub.reactapi.api;

import com.unimelb.studentclub.reactapi.domain.Application;
import com.unimelb.studentclub.reactapi.domain.ApplicationService;
import com.unimelb.studentclub.reactapi.domain.Event;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "getAllApplicationAdmin", urlPatterns = "/getAllApplicationAdmin")
public class ApplicationStudentGetAll extends HttpServlet {
    private ApplicationService applicationService;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Check if the user is logged in
//        HttpSession session = req.getSession(false);
//        if (session == null || session.getAttribute("user") == null) {
//            if (session == null){
//                System.out.println("session is null");
//            } else {
//                System.out.println("session is not null");
//            }
//            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            resp.getWriter().write("Unauthorized - Please login first.");
//            return;
//        }
//
//        // Check roles
//        String role = (String) session.getAttribute("role");
//        if (!"admin".equals(role)) {
//            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            resp.getWriter().write("Forbidden - You do not have permission to perform this action.");
//            return;
//        }

        // Fetch all events and their RSVP counts from the service
        List<Application> applicationList = applicationService.getAllAdminApplication(req);

        // Convert the list of events and their RSVP counts to JSON array
        JSONArray jsonArray = new JSONArray();
        for (Application application : applicationList) {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", application.getId());
            jsonObject.put("clubName",application.getClubName());
            jsonObject.put("clubId",application.getClubId());
            jsonObject.put("status",application.getStatus());
            jsonObject.put("amount",application.getAmount());
            jsonObject.put("description",application.getDescription());
            jsonObject.put("date",application.getDate());
            jsonArray.put(jsonObject);
        }

        // Set the response type to JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Write the JSON array to the response
        resp.getWriter().write(jsonArray.toString());
    }

    @Override
    public void init() throws ServletException {
        super.init();
        applicationService = (ApplicationService) getServletContext().getAttribute(ApplicationContextListener.APPLICATION_SERVICE);
    }
}
