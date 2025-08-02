package com.unimelb.studentclub.reactapi.api;

import com.unimelb.studentclub.reactapi.domain.Ticket;
import com.unimelb.studentclub.reactapi.domain.RSVPService;
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
import java.util.Map;
import java.util.List;

@WebServlet(name = "getAllTicket", urlPatterns = "/getAllTicket")
public class RSVPGetAllTicket extends HttpServlet {
    private RSVPService rsvpService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Check if the user is logged in
//        HttpSession session = req.getSession(false);  // Get session, false means no new session will be created.
//        if (session == null || session.getAttribute("user") == null) {
//            // Returns 401 Unauthorized if there is no session or no user information.
//            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            resp.getWriter().write("Unauthorized - Please login first.");
//            return;
//        }

        // Fetch all tickets from the service
        Map<Ticket, String> ticketsWithEventIds;
        try {
            ticketsWithEventIds = rsvpService.getAllTicket(req);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Convert the list of tickets to JSON array
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<Ticket, String> entry : ticketsWithEventIds.entrySet()) {
            Ticket ticket = entry.getKey();
            String eventId = entry.getValue();

            // Debug the output to make sure that each ticket and eventId matches correctly
            System.out.println("Processing Ticket: " + ticket.getRsvpId() + ", Event ID: " + eventId);

            // Create JSON object for each ticket and its corresponding eventId
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("rsvp_id", ticket.getRsvpId());
            jsonObject.put("student_id", ticket.getStudentId());
            jsonObject.put("event_id", eventId);

            // Add the ticket object to the JSON array
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
        rsvpService = (RSVPService) getServletContext().getAttribute(RSVPContextListener.RSVP_SERVICE);
    }
}
