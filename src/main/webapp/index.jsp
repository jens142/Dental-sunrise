<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sunrise Dental Clinic - Login</title>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
    <link rel="icon" href="${pageContext.request.contextPath}/images/tooth-icon.png">
    <script>document.documentElement.setAttribute('data-theme', localStorage.getItem('sdc-theme') || (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'));</script>
</head>
<body>

<div class="login-theme-toggle no-print">
    <span class="material-symbols-outlined">light_mode</span>
    <label class="switch">
        <input type="checkbox" class="theme-toggle-input" id="themeToggleInput" onchange="sdcToggleTheme()">
        <span class="switch-track"></span>
    </label>
    <span class="material-symbols-outlined">dark_mode</span>
</div>

<div class="login-shell">

    <div class="login-hero">
        <div class="hero-image" style="background-image: url('${pageContext.request.contextPath}/images/login.png');"></div>
        <div class="hero-overlay"></div>
        <div class="hero-badge">
            <div class="line"></div>
            <span>Clinical System v2.0</span>
        </div>
        <div class="hero-text">
            <span class="material-symbols-outlined hero-icon">spa</span>
            <h1>Elevating Dental Care Standards.</h1>
            <p>A secure clinical portal for practitioners to manage appointments, billing, and patient records with clarity.</p>
        </div>
        <div class="hero-stats">
            <div>
                <div class="hero-stat-value">12k+</div>
                <div class="hero-stat-label">Patients served</div>
            </div>
            <div>
                <div class="hero-stat-value">99.9%</div>
                <div class="hero-stat-label">Uptime</div>
            </div>
            <div>
                <div class="hero-stat-value">24/7</div>
                <div class="hero-stat-label">Secure access</div>
            </div>
        </div>
    </div>

    <div class="login-panel">
        <div class="login-form-wrap">

            <div class="login-header">
                <img src="${pageContext.request.contextPath}/images/tooth-icon.png" alt="Sunrise Dental Clinic" class="login-logo">
                <h2>Welcome back</h2>
                <p>Sign in to your clinical dashboard</p>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-error" style="margin-bottom:16px;">${error}</div>
            </c:if>
            <c:if test="${param.error == 'session_expired'}">
                <div class="alert alert-error" style="margin-bottom:16px;">Your session expired. Please log in again.</div>
            </c:if>

            <form id="loginForm" action="${pageContext.request.contextPath}/login" method="post" novalidate>

                <div class="form-group">
                    <label for="username" class="form-label">Username</label>
                    <div class="input-icon-wrap">
                        <span class="material-symbols-outlined icon-leading">person</span>
                        <input type="text" id="username" name="username" required autocomplete="username" class="form-input">
                    </div>
                    <span class="field-error" id="username-error" style="display:none;"></span>
                </div>

                <div class="form-group">
                    <label for="password" class="form-label">Password</label>
                    <div class="input-icon-wrap">
                        <span class="material-symbols-outlined icon-leading">lock</span>
                        <input type="password" id="password" name="password" required autocomplete="current-password" class="form-input" style="padding-right:44px;">
                        <button type="button" id="togglePassword" class="toggle-password-btn">
                            <span class="material-symbols-outlined" id="visibilityIcon" style="font-size:20px;">visibility_off</span>
                        </button>
                    </div>
                    <span class="field-error" id="password-error" style="display:none;"></span>
                </div>

                <div class="login-options-row">
                    <label class="check-row">
                        <input type="checkbox" id="rememberMe" name="rememberMe">
                        <span>Remember me on this device</span>
                    </label>
                    <button type="button" class="forgot-link" onclick="openModal('forgotPasswordModal')">Forgot password?</button>
                </div>

                <button type="submit" class="btn btn-primary btn-block" id="loginSubmitBtn">
                    <span id="loginSubmitLabel">Sign In</span>
                    <span class="material-symbols-outlined" style="font-size:18px;">arrow_forward</span>
                </button>
            </form>

            <div class="login-footer">
                <span class="copyright">&copy; <%= java.time.Year.now() %> Sunrise Dental Clinic</span>
                <div class="secure-badge">
                    <span class="material-symbols-outlined">verified_user</span>
                    <span>Secure Portal</span>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="modal-overlay" id="forgotPasswordModal">
    <div class="modal-box">
        <button type="button" class="modal-close-btn" onclick="closeModal('forgotPasswordModal')" aria-label="Close">
            <span class="material-symbols-outlined">close</span>
        </button>
        <h3>Reset your password</h3>
        <p>
            For security, password resets are handled by your clinic administrator.
            Contact the front desk or IT support with your registered username, and
            they'll issue a temporary password for your next sign-in.
        </p>
        <button type="button" class="btn btn-primary btn-block" style="margin-top:20px;" onclick="closeModal('forgotPasswordModal')">
            Got it
        </button>
    </div>
</div>

<div class="toast-stack no-print" id="toastStack"></div>

<script src="${pageContext.request.contextPath}/js/theme.js"></script>
<script src="${pageContext.request.contextPath}/js/toast.js"></script>
<script src="${pageContext.request.contextPath}/js/app-shell.js"></script>
<script>
    document.getElementById('themeToggleInput').checked = document.documentElement.getAttribute('data-theme') === 'dark';

    document.getElementById('togglePassword').addEventListener('click', () => {
        const pass = document.getElementById('password');
        const icon = document.getElementById('visibilityIcon');
        const show = pass.type === 'password';
        pass.type = show ? 'text' : 'password';
        icon.textContent = show ? 'visibility' : 'visibility_off';
    });

    const rememberedUser = localStorage.getItem('sdc-remembered-username');
    if (rememberedUser) {
        document.getElementById('username').value = rememberedUser;
        document.getElementById('rememberMe').checked = true;
    }

    document.getElementById('loginForm').addEventListener('submit', (e) => {
        const remember = document.getElementById('rememberMe').checked;
        const usernameVal = document.getElementById('username').value.trim();
        if (remember && usernameVal) {
            localStorage.setItem('sdc-remembered-username', usernameVal);
        } else {
            localStorage.removeItem('sdc-remembered-username');
        }
        if (!e.defaultPrevented) {
            document.getElementById('loginSubmitBtn').classList.add('is-loading');
            document.getElementById('loginSubmitLabel').textContent = 'Signing in...';
        }
    });
</script>
<script src="${pageContext.request.contextPath}/js/validation.js"></script>
<script>
    attachLoginValidation('loginForm');
</script>
</body>
</html>
