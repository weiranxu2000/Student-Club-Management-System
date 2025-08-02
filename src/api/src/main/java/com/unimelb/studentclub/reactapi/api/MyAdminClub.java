package com.unimelb.studentclub.reactapi.api;
import com.unimelb.studentclub.reactapi.domain.EventService;
import com.unimelb.studentclub.reactapi.domain.StudentClubService;
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
@WebServlet(name = "myAdminClub", urlPatterns = "/myAdminClub")
public class MyAdminClub extends HttpServlet {
    private StudentClubService studentClubService;

    @Override
    public void init() throws ServletException {
        super.init();
        // Get the StudentClubService in context.
        studentClubService = (StudentClubService) getServletContext().getAttribute(StudentClubContextListener.STUDENTCLUB_SERVICE);

        //Checks if the studentClubService was successfully obtained.
        if (studentClubService == null) {
            System.out.println("StudentClubService is not initialized in ServletContext.");
            throw new ServletException("StudentClubService is not available in ServletContext.");
        } else {
            System.out.println("StudentClubService initialized successfully.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Check if the user is logged in
//        HttpSession session = req.getSession(false);
//        if (session == null || session.getAttribute("user") == null) {
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

        List<String> clubs = studentClubService.clubOfStudent(req);
        JSONArray jsonArray = new JSONArray();
        for (String club : clubs) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", club);
            jsonArray.put(jsonObject);
        }
        // Set the response type to JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Write JSON data to the response
        resp.getWriter().write(jsonArray.toString());
    }
}
