package com.unimelb.studentclub.reactapi.api;

import com.unimelb.studentclub.reactapi.domain.TicketService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.ParseException;


@WebServlet(name = "cancelTicket", urlPatterns = "/cancelTicket")
public class TicketCancel extends HttpServlet {
    private TicketService ticketService;
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

        // open a connection and create a new record(ticket) with giving name
        ticketService.cancel(req);
    }

    @Override
    public void init() throws ServletException {
        super.init();
        ticketService = (TicketService) getServletContext().getAttribute(TicketContextListener.Ticket_SERVICE);
    }
}