package com.unimelb.studentclub.reactapi.api;

import com.unimelb.studentclub.reactapi.domain.Application;
import com.unimelb.studentclub.reactapi.domain.ApplicationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;

@WebServlet(name = "createApplication", urlPatterns = "/createApplication")
public class ApplicationCreate extends HttpServlet {
    private ApplicationService applicationService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Check if the user is logged in
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
        try {
            Application app = applicationService.create(req);
            JSONObject jsonResponse = new JSONObject();
            if(app!=null){
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Application successfully submitted");
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            else{
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Application submission unsuccessful");
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
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
        applicationService = (ApplicationService) getServletContext().getAttribute(ApplicationContextListener.APPLICATION_SERVICE);
    }
}




