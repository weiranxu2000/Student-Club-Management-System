package com.unimelb.studentclub.reactapi.api;

import com.unimelb.studentclub.reactapi.domain.EventService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.ParseException;


@WebServlet(name = "createEvent", urlPatterns = "/createEvent")
public class EventCreate extends HttpServlet {
    private EventService eventService;
    // private ObjectMapper mapper;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Check if the user is logged in
//        HttpSession session = req.getSession(false);  // Get session, false means no new session will be created.
//        if (session == null || session.getAttribute("user") == null) {
//            // Returns 401 Unauthorized if there is no session or no user information.
//            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            resp.getWriter().write("Unauthorized - Please login first.");
//            return;
//        }

//        String id = (String) session.getAttribute("id");
//        System.out.println(id+" in the session");

        // If the user is logged in, continue with the event creation operation
        try {
            if(eventService.create(req) != null){  // create event
                resp.setStatus(HttpServletResponse.SC_OK);  // create success
                resp.getWriter().write("Event created successfully.");}
            else {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);  // create fail
                resp.getWriter().write("Event created unsuccessful: You do not have permission to perform this action");}
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() throws ServletException {
        super.init();
        eventService = (EventService) getServletContext().getAttribute(EventContextListener.EVENT_SERVICE);
    }
}