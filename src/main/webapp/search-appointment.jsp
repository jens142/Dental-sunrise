<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Appointments" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Appointments - Sunrise Dental Clinic</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/tooth-icon.png">
</head>
<body>

<%@ include file="/WEB-INF/jspf/navbar.jspf" %>

<main class="app-main">
    <div class="page-wrap">

        <nav class="breadcrumb no-print">
            <a href="${pageContext.request.contextPath}/dashboard.jsp">Home</a>
            <span class="material-symbols-outlined">chevron_right</span>
            <span class="current">Appointments</span>
        </nav>

        <div class="flex-between flex-wrap gap-3">
            <div>
                <h1 class="page-title">Appointments</h1>
                <p class="page-subtitle">Today's schedule and appointment lookup</p>
            </div>
            <div class="flex gap-2">
                <button type="button" class="btn btn-ghost no-print" onclick="window.print()">
                    <span class="material-symbols-outlined" style="font-size:18px;">print</span>Print
                </button>
                <a href="${pageContext.request.contextPath}/appointments" class="btn btn-primary">
                    <span class="material-symbols-outlined" style="font-size:18px;">add</span>New Appointment
                </a>
            </div>
        </div>

        <c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>

        <form action="${pageContext.request.contextPath}/appointments/search" method="get" class="search-bar no-print">
            <span class="material-symbols-outlined icon-leading">tag</span>
            <input type="text" name="appointmentNumber" required class="form-input" style="flex:1;"
                   placeholder="Enter Appointment Number (e.g. SDC-2026-000124)">
            <button type="submit" class="btn btn-accent btn-pill">Search</button>
        </form>

        <c:if test="${not empty appointment}">
        <div class="card flex-col gap-2 text-sm">
            <div class="flex-between" style="border-bottom:1px solid var(--color-border);padding-bottom:8px;">
                <span class="text-muted">Appointment No.</span><span style="font-weight:600;">${appointment.appointmentNumber}</span>
            </div>
            <div class="flex-between" style="border-bottom:1px solid var(--color-border);padding-bottom:8px;">
                <span class="text-muted">Patient</span><span style="font-weight:600;">${appointment.patientName}</span>
            </div>
            <div class="flex-between" style="border-bottom:1px solid var(--color-border);padding-bottom:8px;">
                <span class="text-muted">Dentist</span><span style="font-weight:600;">${appointment.dentistName}</span>
            </div>
            <div class="flex-between" style="border-bottom:1px solid var(--color-border);padding-bottom:8px;">
                <span class="text-muted">Treatment</span><span style="font-weight:600;">${appointment.treatmentName}</span>
            </div>
            <div class="flex-between" style="border-bottom:1px solid var(--color-border);padding-bottom:8px;">
                <span class="text-muted">Date &amp; Time</span><span style="font-weight:600;">${appointment.appointmentDate} ${appointment.appointmentTime}</span>
            </div>
            <div class="flex-between">
                <span class="text-muted">Status</span>
                <span class="badge badge-info">${appointment.status}</span>
            </div>
        </div>
        </c:if>

        <div class="table-card">
            <div class="table-card-header flex-between flex-wrap gap-3">
                <h3 class="section-title">Today's Schedule</h3>
                <span class="text-sm text-faint">${schedule.size()} appointment(s)</span>
            </div>
            <div class="table-card-header no-print" style="border-top:1px solid var(--color-border);padding:16px 24px;">
                <div class="chip-row" id="statusChipRow">
                    <button type="button" class="chip active" data-status="ALL">All</button>
                    <button type="button" class="chip" data-status="CONFIRMED">Confirmed</button>
                    <button type="button" class="chip" data-status="COMPLETED">Completed</button>
                    <button type="button" class="chip" data-status="CANCELLED">Cancelled</button>
                    <button type="button" class="chip" data-status="NO_SHOW">No-show</button>
                </div>
            </div>
            <c:choose>
            <c:when test="${empty schedule}">
                <div class="table-empty">No appointments scheduled.</div>
            </c:when>
            <c:otherwise>
            <div class="table-scroll">
            <table class="data-table" id="scheduleTable">
                <thead>
                    <tr>
                        <th data-sort-index="0">Time <span class="material-symbols-outlined">unfold_more</span></th>
                        <th data-sort-index="1">Patient <span class="material-symbols-outlined">unfold_more</span></th>
                        <th data-sort-index="2">Dentist <span class="material-symbols-outlined">unfold_more</span></th>
                        <th data-sort-index="3">Treatment <span class="material-symbols-outlined">unfold_more</span></th>
                        <th data-sort-index="4">Status <span class="material-symbols-outlined">unfold_more</span></th>
                        <th class="text-right">Action</th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="appt" items="${schedule}">
                    <tr>
                        <td style="font-weight:600;">${appt.appointmentTime}</td>
                        <td>${appt.patientName}</td>
                        <td class="text-muted">${appt.dentistName}</td>
                        <td class="text-muted">${appt.treatmentName}</td>
                        <td>
                            <c:choose>
                                <c:when test="${appt.status == 'COMPLETED'}"><span class="badge badge-success">${appt.status}</span></c:when>
                                <c:when test="${appt.status == 'NO_SHOW' || appt.status == 'CANCELLED'}"><span class="badge badge-danger">${appt.status}</span></c:when>
                                <c:otherwise><span class="badge badge-info">${appt.status}</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-right">
                            <form action="${pageContext.request.contextPath}/appointments/update-status" method="post" style="display:inline;" class="no-print">
                                <input type="hidden" name="appointmentId" value="${appt.appointmentId}">
                                <select name="status" onchange="this.form.submit()" class="form-select" style="height:32px;font-size:0.75rem;width:auto;">
                                    <option value="">Change...</option>
                                    <option value="CONFIRMED">Confirmed</option>
                                    <option value="COMPLETED">Completed</option>
                                    <option value="CANCELLED">Cancelled</option>
                                    <option value="NO_SHOW">No-show</option>
                                </select>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            </div>
            </c:otherwise>
            </c:choose>
        </div>

    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>

<script>
    makeTableSortable('scheduleTable');
    initStatusFilterChips('statusChipRow', 'scheduleTable', 4);
</script>
</body>
</html>
