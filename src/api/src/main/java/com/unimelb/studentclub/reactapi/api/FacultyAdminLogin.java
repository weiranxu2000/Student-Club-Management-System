package com.unimelb.studentclub.reactapi.api;

import com.unimelb.studentclub.reactapi.domain.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONObject;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import jakarta.servlet.http.Cookie;

@WebServlet(name = "loginAdmin", urlPatterns = "/loginAdmin")
public class FacultyAdminLogin extends HttpServlet {
    private UserService userService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<String> passwords = userService.getPassword(req);

            //Boolean isAdmin = "true".equals(passwords.get(2));
            JSONObject jsonResponse = new JSONObject();

            if (passwords.get(0).equals(passwords.get(1))) {
//                // Create session
//                HttpSession session = req.getSession(true);
//                session.setAttribute("id",passwords.get(3));
//                session.setAttribute("user", passwords.get(0));  // Storing user passwords
//                session.setAttribute("role", isAdmin ? "admin" : "student");  // Storing user roles


//                // 查找并设置 JSESSIONID Cookie 的 Secure 属性
//                Cookie[] cookies = req.getCookies();
//                if (cookies != null) {
//                    for (Cookie cookie : cookies) {
//                        if ("JSESSIONID".equals(cookie.getName())) {
//                            // 设置 Secure 标志为 true
//                            // cookie.setPath("/api_war_exploded");
//                            cookie.setSecure(true);
//                            cookie.setHttpOnly(true);  // 可选项，增强安全性，防止客户端脚本访问此 Cookie
//                            // 将修改后的 Cookie 添加到响应
//                            resp.addCookie(cookie);
//                        }
//                    }
//                }

                jsonResponse.put("success", true);
                jsonResponse.put("message", "Login Successful");
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Login failed");
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(jsonResponse.toString());

        } catch (Exception e) {
            throw new ServletException("Error processing login request", e);
        }
    }



    @Override
    public void init() throws ServletException {
        super.init();
        userService = (UserService) getServletContext().getAttribute(UserContextListener.USER_SERVICE);
    }
}
