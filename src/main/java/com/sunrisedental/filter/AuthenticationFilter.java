package com.sunrisedental.filter;

import com.sunrisedental.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Set;

/**
 * Enforces authentication + role-based access control (RBAC).
 * Runs before every request to a protected resource. Keeps auth
 * logic out of every individual Servlet (cross-cutting concern).
 */
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    // Paths that don't require login
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/index.jsp", "/login", "/css/", "/js/", "/images/", "/fonts/"
    );

    // Paths restricted to specific roles (prefix match)
    private static final Set<String> ADMIN_ONLY = Set.of("/reports", "/staff-management", "/admin");

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI().substring(request.getContextPath().length());

        if (isPublicPath(path)) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=session_expired");
            return;
        }

        // Session idle timeout is handled declaratively via web.xml <session-timeout>;
        // here we only check role authorization.
        if (isAdminOnlyPath(path) && currentUser.getRole() != User.Role.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Insufficient permissions.");
            return;
        }

        // Audit trail hook - who accessed what, when (satisfies audit log requirement)
        System.out.println("[AUDIT] " + currentUser.getUsername() + " (" + currentUser.getRole() +
                ") accessed " + path + " at " + System.currentTimeMillis());

        chain.doFilter(req, res);
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith) || path.equals("/");
    }

    private boolean isAdminOnlyPath(String path) {
        return ADMIN_ONLY.stream().anyMatch(path::startsWith);
    }
}