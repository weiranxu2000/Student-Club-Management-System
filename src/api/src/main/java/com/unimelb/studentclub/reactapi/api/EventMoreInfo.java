package com.unimelb.studentclub.reactapi.api;

import com.unimelb.studentclub.reactapi.domain.Event;
import com.unimelb.studentclub.reactapi.domain.EventService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import java.io.IOException;

@WebServlet(name = "moreInfo", urlPatterns = "/moreInfo")
public class EventMoreInfo extends HttpServlet {
    private EventService eventService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Check if the user is logged in
//        HttpSession session = req.getSession(false);  // Get session, false means no new session will be created.
//        if (session == null || session.getAttribute("user") == null) {
//            //  Returns 401 Unauthorized if there is no session or no user information.
//            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            resp.getWriter().write("Unauthorized - Please login first.");
//            return;
//        }


        // Get the event ID from the request parameter
        String eventId = req.getParameter("id");

        if (eventId == null || eventId.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Event ID is required");
            return;
        }

        // Create an Event object with the ID
        Event event = new Event();
        event.setId(eventId);

        // Fetch detailed info from the service
        event = eventService.getDetails(req);

        // Check if the event was found
        if (event == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Event not found");
            return;
        }

        // Convert the event details to JSON
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("description", event.getDescription());
        // jsonObject.put("clubName", event.getClubName());
        jsonObject.put("venueName", event.getVenueName());
        jsonObject.put("time", event.getTime().toString());
        jsonObject.put("status", event.getStatus().toString());

        // Set the response type to JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Write the JSON object to the response
        resp.getWriter().write(jsonObject.toString());
    }

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize eventService from context
        eventService = (EventService) getServletContext().getAttribute(EventContextListener.EVENT_SERVICE);
    }
}
