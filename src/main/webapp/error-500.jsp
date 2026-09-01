<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Something Went Wrong - Sunrise Dental Clinic</title>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <link rel="icon" href="${pageContext.request.contextPath}/images/tooth-icon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script>document.documentElement.setAttribute('data-theme', localStorage.getItem('sdc-theme') || (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'));</script>
</head>
<body>
    <div class="flex-center" style="min-height:100vh;flex-direction:column;gap:16px;text-align:center;padding:24px;">
        <div class="card" style="max-width:420px;padding:40px;">
            <span class="material-symbols-outlined" style="font-size:56px;color:var(--color-danger-text);">error</span>
            <h1 style="font-size:2.5rem;font-weight:800;color:var(--color-text);margin-top:12px;">500</h1>
            <p class="text-muted" style="margin-top:8px;">Something went wrong on our end. Please try again, or contact the system administrator if this keeps happening.</p>
            <div class="flex gap-2 flex-center" style="margin-top:20px;flex-wrap:wrap;">
                <a href="${pageContext.request.contextPath}/dashboard.jsp" class="btn btn-primary">
                    Back to Dashboard
                </a>
                <button type="button" class="btn btn-ghost" onclick="location.reload()">
                    Try Again
                </button>
            </div>
        </div>
    </div>
</body>
</html>
