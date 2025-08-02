package com.unimelb.studentclub.reactapi.api;

import com.unimelb.studentclub.reactapi.domain.RSVP;
import com.unimelb.studentclub.reactapi.domain.RSVPService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;

@WebServlet(name = "createRSVP", urlPatterns = "/createRSVP")
public class RSVPCreate extends HttpServlet {
    private RSVPService rsvpService;

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

        try {
            boolean success = rsvpService.create(req);
            System.out.println(success);

            JSONObject jsonResponse = new JSONObject();
            if (success) {
                jsonResponse.put("success", true);
                jsonResponse.put("message", "RSVP submitted successfully!");
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "RSVP failed due to ticket or event issues.");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(jsonResponse.toString());

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() throws ServletException {
        super.init();
        rsvpService = (RSVPService) getServletContext().getAttribute(RSVPContextListener.RSVP_SERVICE);
    }
}
