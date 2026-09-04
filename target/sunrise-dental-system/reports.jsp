<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Reports & Analytics" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Reports - Sunrise Dental Clinic</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/tooth-icon.png">
</head>
<body>

<%@ include file="/WEB-INF/jspf/navbar.jspf" %>

<main class="app-main">
    <div class="page-wrap page-wrap-wide">

        <nav class="breadcrumb no-print">
            <a href="${pageContext.request.contextPath}/dashboard.jsp">Home</a>
            <span class="material-symbols-outlined">chevron_right</span>
            <span class="current">Reports &amp; Analytics</span>
        </nav>

        <div>
            <h1 class="page-title">Reports &amp; Analytics</h1>
            <p class="page-subtitle">Monitor clinic performance and support decision-making</p>
        </div>

        <c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>

        <form action="${pageContext.request.contextPath}/reports" method="get" class="card flex flex-wrap gap-4 no-print" style="align-items:flex-end;">
            <div class="form-group" style="flex:1;min-width:160px;">
                <label class="form-label">Report Type</label>
                <select name="type" id="reportTypeSelect" class="form-select">
                    <option value="DAILY_SCHEDULE" ${param.type == 'DAILY_SCHEDULE' ? 'selected' : ''}>Daily Schedule</option>
                    <option value="REVENUE" ${param.type == 'REVENUE' ? 'selected' : ''}>Revenue by Treatment</option>
                    <option value="NO_SHOW" ${param.type == 'NO_SHOW' ? 'selected' : ''}>No-Show &amp; Cancellations</option>
                </select>
            </div>
            <div class="form-group" style="flex:1;min-width:140px;">
                <label class="form-label">Start Date</label>
                <input type="date" name="startDate" value="${empty param.startDate ? reportStartDate : param.startDate}" class="form-input" required>
            </div>
            <div class="form-group" style="flex:1;min-width:140px;">
                <label class="form-label" id="endDateLabel">End Date</label>
                <input type="date" name="endDate" id="endDateInput" value="${empty param.endDate ? reportEndDate : param.endDate}" class="form-input">
                <p class="field-hint" id="endDateHint" style="display:none;">Required for this report type.</p>
            </div>
            <button type="submit" class="btn btn-primary">Run Report</button>
        </form>

        <c:if test="${not empty reportData}">
        <div class="table-card">
            <div class="table-card-header flex-between flex-wrap gap-3">
                <div>
                    <h3 class="section-title">${reportTitle}</h3>
                    <p class="text-sm text-muted" style="margin-top:4px;">${reportData.size()} record(s)</p>
                </div>
                <div class="flex gap-2 no-print">
                    <button type="button" class="btn btn-ghost btn-sm" onclick="exportTableToCSV('reportTable', 'sunrise-dental-report.csv')">
                        <span class="material-symbols-outlined" style="font-size:16px;">download</span>Export CSV
                    </button>
                    <button type="button" class="btn btn-ghost btn-sm" onclick="window.print()">
                        <span class="material-symbols-outlined" style="font-size:16px;">print</span>Print
                    </button>
                </div>
            </div>

            <c:choose>
            <c:when test="${empty reportData}">
                <div class="table-empty">No records found for this range.</div>
            </c:when>
            <c:otherwise>
            <div class="table-scroll">
                <table class="data-table" id="reportTable">
                    <thead>
                        <tr>
                            <c:forEach var="key" items="${reportData[0].keySet()}">
                                <th>${key}</th>
                            </c:forEach>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="row" items="${reportData}">
                        <tr>
                            <c:forEach var="entry" items="${row}">
                                <td>${entry.value}</td>
                            </c:forEach>
                        </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            </c:otherwise>
            </c:choose>
        </div>
        </c:if>

    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<script>
    const reportTypeSelect = document.getElementById('reportTypeSelect');
    const endDateInput = document.getElementById('endDateInput');
    const endDateLabel = document.getElementById('endDateLabel');
    const endDateHint = document.getElementById('endDateHint');

    function syncEndDateRequirement() {
        const needsEndDate = reportTypeSelect.value !== 'DAILY_SCHEDULE';
        endDateInput.required = needsEndDate;
        endDateLabel.textContent = needsEndDate ? 'End Date *' : 'End Date';
        endDateHint.style.display = needsEndDate ? 'block' : 'none';
    }

    reportTypeSelect.addEventListener('change', syncEndDateRequirement);
    syncEndDateRequirement();
</script>
</body>
</html>
