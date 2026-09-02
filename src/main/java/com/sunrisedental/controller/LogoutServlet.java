package com.sunrisedental.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Handles staff logout by invalidating the session, then redirecting
 * to the login page. Kept as its own Servlet (rather than a doGet
 * inside LoginServlet) since it's mapped to a distinct URL pattern
 * and has a single, focused responsibility.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/index.jsp?message=logged_out");
    }
}