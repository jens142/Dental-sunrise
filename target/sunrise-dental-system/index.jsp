<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>
<div class="login-shell">
    <section class="login-panel">
        <div class="login-form-wrap">
            <div class="login-header">
                <h1>Sunrise Dental Clinic</h1>
                <h2>Welcome back</h2>
                <p>Sign in to manage your clinic.</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error"><%= request.getAttribute("error") %></div>
            <% } %>
            <% if ("logged_out".equals(request.getParameter("message"))) { %>
                <div class="alert alert-success">You have been signed out.</div>
            <% } %>

            <form id="loginForm" action="${pageContext.request.contextPath}/login" method="post">
                <div class="form-group">
                    <label class="form-label" for="username">Username</label>
                    <input class="form-input" id="username" name="username" type="text" autocomplete="username" required autofocus>
                </div>
                <div class="form-group">
                    <label class="form-label" for="password">Password</label>
                    <input class="form-input" id="password" name="password" type="password" autocomplete="current-password" required>
                </div>
                <button class="btn btn-primary btn-block" type="submit">Sign In</button>
            </form>
        </div>
    </section>
</div>
</body>
</html>
