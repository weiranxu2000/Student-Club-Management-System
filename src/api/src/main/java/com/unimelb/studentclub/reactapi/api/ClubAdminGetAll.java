package com.unimelb.studentclub.reactapi.api;


import com.unimelb.studentclub.reactapi.domain.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;
import java.util.Map;

// return Admin ID	Admin Name	Club

@WebServlet(name = "GetAllClubAdmin", urlPatterns = "/getAllClubAdmin")
public class ClubAdminGetAll extends HttpServlet{
    private UserService userService;

//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        // Set the response type to JSON
//        JSONArray jsonArray = new JSONArray();
//        Map<User, Integer> relevantAdmins = userService.getAllRelevantAdmin(req);
//        for (Map.Entry<User, Integer> entry : relevantAdmins.entrySet()) {
//            User user = entry.getKey();
//
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("id", user.getStudentId());
//            jsonObject.put("name", user.getName());
//
//
//            // Add the event object to the JSON array
//            jsonArray.put(jsonObject);
//        }
//        resp.setContentType("application/json");
//        resp.setCharacterEncoding("UTF-8");
//
//        // Write the JSON array to the response
//        resp.getWriter().write(jsonArray.toString());
//    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        // Check if the user is logged in
//        HttpSession session = req.getSession(false);
//        if (session == null || session.getAttribute("user") == null) {
//            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            resp.getWriter().write("Unauthorized - Please login first.");
//            return;
//        }
//
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

        // Get information about all administrators and the clubs they manage
        List<Map<String, Object>> adminClubInfo = userService.getAllAdmin(req);

        // Create a JSON array for storing each record
        JSONArray jsonArray = new JSONArray();

        // Iterate through the list of results
        for (Map<String, Object> record : adminClubInfo) {
            // Create a new JSON object
            JSONObject jsonObject = new JSONObject();

            // Extract student_id, student_name and club_name from each record.
            jsonObject.put("id", record.get("student_id"));
            jsonObject.put("name", record.get("student_name"));
            jsonObject.put("club", record.get("club_name"));

            // Adding JSON Objects to a JSON Array
            jsonArray.put(jsonObject);
        }

        // Set the response content type to JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Write JSON array to response
        resp.getWriter().write(jsonArray.toString());
    }


    @Override
    public void init() throws ServletException {
        super.init();
        userService = (UserService) getServletContext().getAttribute(UserContextListener.USER_SERVICE);
    }
}
