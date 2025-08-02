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


@WebServlet(name = "cancelEvent", urlPatterns = "/cancelEvent")
public class EventCancel extends HttpServlet {
    private EventService eventService;
    // private ObjectMapper mapper;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        // Check if the user is logged in
//        HttpSession session = req.getSession(false);
//        if (session == null || session.getAttribute("user") == null) {
//            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            resp.getWriter().write("Unauthorized - Please login first.");
//            return;
//        }
//
//        // Check role
//        String role = (String) session.getAttribute("role");
//        if (!"admin".equals(role)) {
//            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            resp.getWriter().write("Forbidden - You do not have permission to perform this action.");
//            return;
//        }

        // open a connection and create a new record(event) with giving name
        eventService.cancel(req);
    }

    @Override
    public void init() throws ServletException {
        super.init();
        eventService = (EventService) getServletContext().getAttribute(EventContextListener.EVENT_SERVICE);
    }
}