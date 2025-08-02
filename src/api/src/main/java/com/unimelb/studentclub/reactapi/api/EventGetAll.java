package com.unimelb.studentclub.reactapi.api;

import com.unimelb.studentclub.reactapi.domain.Event;
import com.unimelb.studentclub.reactapi.domain.EventService;
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

// Implement lazy load for getting all the events use case.
@WebServlet(name = "getAllEvent", urlPatterns = "/getAllEvent")
public class EventGetAll extends HttpServlet {
    private EventService eventService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        // Check if the user is logged in
//        HttpSession session = req.getSession(false);  // Get session, false means no new session will be created.
//        if (session == null || session.getAttribute("user") == null) {
//            // Returns 401 Unauthorized if there is no session or no user information.
//            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            resp.getWriter().write("Unauthorized - Please login first.");
//            return;
//        }

        // If the user is logged in, continue to get all events
        Map<Event, Integer> events = eventService.getAll();

        // Convert events and their RSVP counts to JSON arrays
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
            jsonObject.put("rsvp_count", rsvpCount);  // Adding RSVP Counts

            // Adding event objects to a JSON array
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
        eventService = (EventService) getServletContext().getAttribute(EventContextListener.EVENT_SERVICE);
    }
}
