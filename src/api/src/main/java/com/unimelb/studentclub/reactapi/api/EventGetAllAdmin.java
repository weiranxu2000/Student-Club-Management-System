package com.unimelb.studentclub.reactapi.api;

import com.unimelb.studentclub.reactapi.domain.Event;
import com.unimelb.studentclub.reactapi.domain.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
@WebServlet(name = "getAllEventAdmin", urlPatterns = "/getAllEventAdmin")
public class EventGetAllAdmin extends HttpServlet{
    private UserService userService;

    @Override
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
        Map<Event, Integer> events = userService.getAllAdminEvents(req);

        // Convert the list of events and their RSVP counts to JSON array
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<Event, Integer> entry : events.entrySet()) {
            Event event = entry.getKey();
            int rsvpCount = entry.getValue();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", event.getId());
            jsonObject.put("title", event.getTitle());
            jsonObject.put("club_name", event.getClubName());
            jsonObject.put("date", event.getDate().toString());
            jsonObject.put("cost", event.getCost());
            jsonObject.put("capacity", event.getCapacity());
            jsonObject.put("rsvp_count", rsvpCount);  // Add RSVP count

            // Add the event object to the JSON array
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
        userService = (UserService) getServletContext().getAttribute(UserContextListener.USER_SERVICE);
    }
}
