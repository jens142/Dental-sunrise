<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="New Appointment" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>New Appointment - Sunrise Dental Clinic</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/tooth-icon.png">
</head>
<body>

<%@ include file="/WEB-INF/jspf/navbar.jspf" %>

<main class="app-main">
    <div class="page-wrap page-wrap-narrow">

        <nav class="breadcrumb no-print">
            <a href="${pageContext.request.contextPath}/dashboard.jsp">Home</a>
            <span class="material-symbols-outlined">chevron_right</span>
            <a href="${pageContext.request.contextPath}/appointments/schedule">Appointments</a>
            <span class="material-symbols-outlined">chevron_right</span>
            <span class="current">New Appointment</span>
        </nav>

        <div>
            <h1 class="page-title">New Appointment</h1>
            <p class="page-subtitle">Schedule a patient visit with the appropriate dentist and treatment</p>
        </div>

        <c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>
        <c:if test="${not empty successMessage}"><div class="alert alert-success">${successMessage}</div></c:if>

        <div class="grid grid-12">
            <form id="appointmentForm" action="${pageContext.request.contextPath}/appointments" method="post" class="card card-lg flex-col gap-4 col-span-8">

                <div class="grid grid-2" style="gap:20px;">

                    <div class="form-group">
                        <label class="form-label">Patient ID</label>
                        <input type="number" name="patientId" id="patientIdInput" required class="form-input" placeholder="e.g. 2041">
                    </div>

                    <div class="form-group">
                        <label class="form-label">Dentist</label>
                        <select name="dentistId" id="dentistSelect" required class="form-select">
                            <option value="">Select dentist...</option>
                            <c:forEach var="d" items="${dentists}">
                                <option value="${d.dentistId}">${d.dentistName} - ${d.specialization}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Treatment</label>
                        <select name="treatmentId" id="treatmentSelect" required class="form-select">
                            <option value="">Select treatment...</option>
                            <c:forEach var="t" items="${treatments}">
                                <option value="${t.treatmentId}">${t.treatmentName} (Rs. ${t.baseCost})</option>
                            </c:forEach>
                        </select>
                        <p id="costPreview" class="text-sm" style="color:var(--color-primary);font-weight:700;margin-top:4px;display:none;"></p>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Date</label>
                        <input type="date" name="appointmentDate" id="appointmentDateInput" required class="form-input">
                    </div>

                    <div class="form-group">
                        <label class="form-label">Time</label>
                        <input type="time" name="appointmentTime" id="appointmentTimeInput" required class="form-input">
                        <p class="field-hint">Clinic hours are 8:00 AM to 6:00 PM, Monday to Saturday.</p>
                    </div>

                </div>

                <button type="submit" class="btn btn-primary" id="bookAppointmentBtn" style="align-self:flex-start;">
                    <span id="bookAppointmentLabel">Book Appointment</span>
                    <span class="material-symbols-outlined" style="font-size:18px;">arrow_forward</span>
                </button>
            </form>

            <aside class="card col-span-4 flex-col gap-3 no-print" style="align-self:flex-start;">
                <h3 class="section-title">Appointment Summary</h3>
                <div class="flex-col gap-2 text-sm">
                    <div class="flex-between" style="border-bottom:1px solid var(--color-border);padding-bottom:8px;">
                        <span class="text-muted">Patient</span><span id="summaryPatient" style="font-weight:600;">&mdash;</span>
                    </div>
                    <div class="flex-between" style="border-bottom:1px solid var(--color-border);padding-bottom:8px;">
                        <span class="text-muted">Dentist</span><span id="summaryDentist" style="font-weight:600;">&mdash;</span>
                    </div>
                    <div class="flex-between" style="border-bottom:1px solid var(--color-border);padding-bottom:8px;">
                        <span class="text-muted">Treatment</span><span id="summaryTreatment" style="font-weight:600;">&mdash;</span>
                    </div>
                    <div class="flex-between" style="border-bottom:1px solid var(--color-border);padding-bottom:8px;">
                        <span class="text-muted">Date &amp; Time</span><span id="summaryDateTime" style="font-weight:600;">&mdash;</span>
                    </div>
                    <div class="flex-between">
                        <span class="text-muted">Est. Cost</span><span id="summaryCost" style="font-weight:700;color:var(--color-primary);">&mdash;</span>
                    </div>
                </div>
            </aside>
        </div>

    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>

<script src="${pageContext.request.contextPath}/js/validation.js"></script>
<script src="${pageContext.request.contextPath}/js/appointment.js"></script>
<script>
    const summaryFields = {
        patientId: document.getElementById('patientIdInput'),
        dentist: document.getElementById('dentistSelect'),
        treatment: document.getElementById('treatmentSelect'),
        date: document.getElementById('appointmentDateInput'),
        time: document.getElementById('appointmentTimeInput')
    };

    function refreshSummary() {
        document.getElementById('summaryPatient').textContent = summaryFields.patientId.value ? ('#' + summaryFields.patientId.value) : '\u2014';

        const dentistOpt = summaryFields.dentist.options[summaryFields.dentist.selectedIndex];
        document.getElementById('summaryDentist').textContent = (dentistOpt && dentistOpt.value) ? dentistOpt.text : '\u2014';

        const treatmentOpt = summaryFields.treatment.options[summaryFields.treatment.selectedIndex];
        document.getElementById('summaryTreatment').textContent = (treatmentOpt && treatmentOpt.value) ? treatmentOpt.text.replace(/\s*\(Rs\..*\)/, '') : '\u2014';

        const dateVal = summaryFields.date.value;
        const timeVal = summaryFields.time.value;
        document.getElementById('summaryDateTime').textContent = (dateVal || timeVal) ? ((dateVal || '') + ' ' + (timeVal || '')).trim() : '\u2014';

        const match = treatmentOpt && treatmentOpt.text.match(/Rs\.\s*([\d,]+(\.\d+)?)/);
        document.getElementById('summaryCost').textContent = match ? ('Rs. ' + match[1]) : '\u2014';
    }

    Object.values(summaryFields).forEach((el) => el.addEventListener('input', refreshSummary));
    Object.values(summaryFields).forEach((el) => el.addEventListener('change', refreshSummary));

    document.addEventListener('DOMContentLoaded', () => {
        document.getElementById('appointmentForm').addEventListener('submit', (e) => {
            if (!e.defaultPrevented) {
                document.getElementById('bookAppointmentBtn').classList.add('is-loading');
                document.getElementById('bookAppointmentLabel').textContent = 'Booking...';
            }
        });
    });
</script>
</body>
</html>
