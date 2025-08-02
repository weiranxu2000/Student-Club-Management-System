package com.unimelb.studentclub.reactapi.api;

import com.unimelb.studentclub.reactapi.domain.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.ParseException;


@WebServlet(name = "addAdmin", urlPatterns = "/addAdmin")
public class AdminAdd extends HttpServlet {
    private UserService userService;
    // private ObjectMapper mapper;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Check if the user is logged in
//        HttpSession session = req.getSession(false);
//        if (session == null || session.getAttribute("user") == null) {
//            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            resp.getWriter().write("Unauthorized - Please login first.");
//            return;
//        }

//        String id = (String) session.getAttribute("id");
//        System.out.println(id+" in the session");
//
//        // check role
//        String role = (String) session.getAttribute("role");
//        if (!"admin".equals(role)) {
//            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            resp.getWriter().write("Forbidden - You do not have permission to perform this action.");
//            return;
//        }

        userService.addAdmin(req);
    }

    @Override
    public void init() throws ServletException {
        super.init();
        userService = (UserService) getServletContext().getAttribute(UserContextListener.USER_SERVICE);
    }
}