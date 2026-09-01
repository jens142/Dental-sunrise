<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.sunrisedental.model.User" %>
<%@ page import="com.sunrisedental.dao.DashboardDAO" %>
<%@ page import="java.sql.Date" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.YearMonth" %>
<%@ page import="com.google.gson.Gson" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp?error=session_expired");
        return;
    }
    DashboardDAO dashboardDAO = new DashboardDAO();
    Date today = Date.valueOf(LocalDate.now());
    YearMonth currentMonth = YearMonth.now();
    Date monthStart = Date.valueOf(currentMonth.atDay(1));
    Date monthEnd = Date.valueOf(currentMonth.atEndOfMonth());
    request.setAttribute("dashboardAppointmentCount", dashboardDAO.countAppointmentsOn(today));
    request.setAttribute("dashboardPatientCount", dashboardDAO.countPatients());
    request.setAttribute("dashboardPendingBills", dashboardDAO.countPendingBills());
    request.setAttribute("dashboardRevenue", dashboardDAO.revenueThisMonth(monthStart, monthEnd));
    request.setAttribute("dashboardAppointments", dashboardDAO.appointmentsByDentist(today));
    request.setAttribute("dashboardRevenueByDay", dashboardDAO.revenueByDay(monthStart, monthEnd));
%>
<c:set var="pageTitle" value="Dashboard" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Sunrise Dental Clinic</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/tooth-icon.png">
</head>
<body>

<%@ include file="/WEB-INF/jspf/navbar.jspf" %>

<main class="app-main">
    <div class="page-wrap page-wrap-wide">

        <nav class="breadcrumb no-print">
            <a href="${pageContext.request.contextPath}/dashboard.jsp">Home</a>
            <span class="material-symbols-outlined">chevron_right</span>
            <span class="current">Dashboard</span>
        </nav>

        <section class="hero-banner">
            <div class="hero-bg-image" style="background-image: url('${pageContext.request.contextPath}/images/hero-banner.png');"></div>
            <div class="hero-content">
                <p class="hero-date"><span class="hero-dot"></span><%= java.time.LocalDate.now() %></p>
                <h1>Welcome back, <%= currentUser.getFullName() %></h1>
                <p><%= currentUser.getRole() %> &middot; Sunrise Dental Clinic</p>
                <div class="hero-actions no-print">
                    <a href="${pageContext.request.contextPath}/appointments" class="btn btn-primary btn-sm">
                        <span class="material-symbols-outlined" style="font-size:16px;">add</span>New Appointment
                    </a>
                    <a href="${pageContext.request.contextPath}/appointments/schedule" class="btn btn-ghost btn-sm">
                        View Schedule
                    </a>
                </div>
            </div>
        </section>

        <div class="grid grid-4">
            <div class="stat-card">
                <div class="stat-icon" style="background:rgba(205,229,255,0.4);">
                    <span class="material-symbols-outlined" style="color:var(--color-primary);">calendar_today</span>
                </div>
                <div class="stat-value" id="statAppointments">${dashboardAppointmentCount}</div>
                <div class="stat-label">Appointments Today</div>
                <div class="stat-trend up"><span class="material-symbols-outlined">trending_up</span>Live schedule</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon" style="background:rgba(179,235,255,0.4);">
                    <span class="material-symbols-outlined" style="color:var(--color-accent-dark);">group</span>
                </div>
                <div class="stat-value" id="statPatients">${dashboardPatientCount}</div>
                <div class="stat-label">Active Patients</div>
                <div class="stat-trend up"><span class="material-symbols-outlined">trending_up</span>This month</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon" style="background:var(--color-warning-bg);">
                    <span class="material-symbols-outlined" style="color:var(--color-warning-text);">pending_actions</span>
                </div>
                <div class="stat-value" id="statPendingBills">${dashboardPendingBills}</div>
                <div class="stat-label">Pending Invoices</div>
                <div class="stat-trend down"><span class="material-symbols-outlined">trending_down</span>Needs follow-up</div>
            </div>
            <div class="stat-card">
                <div class="stat-icon" style="background:rgba(156,236,251,0.4);">
                    <span class="material-symbols-outlined" style="color:var(--color-secondary);">payments</span>
                </div>
                <div class="stat-value" id="statRevenue">Rs. ${dashboardRevenue}</div>
                <div class="stat-label">Revenue This Month</div>
                <div class="stat-trend up"><span class="material-symbols-outlined">trending_up</span>Paid &amp; billed</div>
            </div>
        </div>

        <div class="grid grid-4">
            <a href="${pageContext.request.contextPath}/appointments/schedule" class="card card-hover quick-action-card">
                <div class="quick-action-icon" style="background:rgba(205,229,255,0.4);">
                    <span class="material-symbols-outlined" style="color:var(--color-primary);">calendar_month</span>
                </div>
                <h3>Appointments</h3>
                <p>View and manage today's schedule</p>
                <div class="quick-action-link" style="color:var(--color-primary);">
                    View Schedule <span class="material-symbols-outlined">arrow_forward</span>
                </div>
            </a>

            <a href="${pageContext.request.contextPath}/patients" class="card card-hover quick-action-card">
                <div class="quick-action-icon" style="background:rgba(179,235,255,0.4);">
                    <span class="material-symbols-outlined" style="color:var(--color-accent-dark);">group</span>
                </div>
                <h3>Patients</h3>
                <p>Register or search patient records</p>
                <div class="quick-action-link" style="color:var(--color-accent-dark);">
                    Manage Patients <span class="material-symbols-outlined">arrow_forward</span>
                </div>
            </a>

            <a href="${pageContext.request.contextPath}/billing" class="card card-hover quick-action-card">
                <div class="quick-action-icon" style="background:rgba(156,236,251,0.4);">
                    <span class="material-symbols-outlined" style="color:var(--color-secondary);">payments</span>
                </div>
                <h3>Billing</h3>
                <p>Generate bills and print receipts</p>
                <div class="quick-action-link" style="color:var(--color-secondary);">
                    Go to Billing <span class="material-symbols-outlined">arrow_forward</span>
                </div>
            </a>

            <c:if test="${currentUser.role == 'ADMIN'}">
            <a href="${pageContext.request.contextPath}/reports" class="card card-hover quick-action-card">
                <div class="quick-action-icon" style="background:#f3f4f6;">
                    <span class="material-symbols-outlined" style="color:#4b5563;">analytics</span>
                </div>
                <h3>Reports</h3>
                <p>Revenue, schedule &amp; analytics</p>
                <div class="quick-action-link" style="color:#4b5563;">
                    View Analytics <span class="material-symbols-outlined">arrow_forward</span>
                </div>
            </a>
            </c:if>
        </div>

        <div class="grid grid-2">
            <div class="card">
                <h3 class="section-title">Appointments Today</h3>
                <p class="text-xs text-faint uppercase" style="margin-bottom:16px;">Live from today's schedule</p>
                <div id="appointmentsChart" class="dashboard-chart" aria-label="Appointments today chart"></div>
            </div>
            <div class="card">
                <h3 class="section-title">Revenue This Month</h3>
                <p class="text-xs text-faint uppercase" style="margin-bottom:16px;">From billed &amp; paid invoices</p>
                <div id="revenueChart" class="dashboard-chart" aria-label="Revenue this month chart"></div>
            </div>
        </div>

        <div class="card">
            <div class="flex-between" style="margin-bottom:8px;">
                <h3 class="section-title">Recent Activity</h3>
                <span class="text-xs text-faint uppercase">Auto-refreshing feed</span>
            </div>
            <div class="activity-feed" id="activityFeed"></div>
        </div>

    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>

<script src="${pageContext.request.contextPath}/js/dashboard-charts.js"></script>
<script type="application/json" id="dashboardData">
{
    "appointmentsToday": <%= new Gson().toJson(request.getAttribute("dashboardAppointments")) %>,
    "revenueThisMonth": <%= new Gson().toJson(request.getAttribute("dashboardRevenueByDay")) %>,
    "appointmentCount": <%= request.getAttribute("dashboardAppointmentCount") %>,
    "patientCount": <%= request.getAttribute("dashboardPatientCount") %>,
    "pendingBills": <%= request.getAttribute("dashboardPendingBills") %>,
    "revenue": <%= request.getAttribute("dashboardRevenue") %>
}
</script>
<script>
    const dashboardData = JSON.parse(document.getElementById('dashboardData').textContent);

    initDashboardCharts({
        appointmentsToday: dashboardData.appointmentsToday,
        revenueThisMonth: dashboardData.revenueThisMonth
    });

    animateCounter(document.getElementById('statAppointments'), dashboardData.appointmentCount);
    animateCounter(document.getElementById('statPatients'), dashboardData.patientCount);
    animateCounter(document.getElementById('statPendingBills'), dashboardData.pendingBills);
    document.getElementById('statRevenue').textContent = 'Rs. ' + dashboardData.revenue;

    const activityFeed = document.getElementById('activityFeed');
    const activityItems = [
        { icon: 'event_available', text: 'Dashboard connected to the live scheduling system.', time: 'Just now' },
        { icon: 'receipt_long', text: 'Billing summaries refresh automatically each session.', time: 'Today' },
        { icon: 'shield', text: 'Your session is protected with an automatic sign-out timer.', time: 'Today' }
    ];
    activityItems.forEach((item) => {
        const row = document.createElement('div');
        row.className = 'activity-row';
        row.innerHTML =
            '<div class="activity-icon"><span class="material-symbols-outlined">' + item.icon + '</span></div>' +
            '<div><div class="activity-text">' + item.text + '</div><div class="activity-time">' + item.time + '</div></div>';
        activityFeed.appendChild(row);
    });
</script>
</body>
</html>
