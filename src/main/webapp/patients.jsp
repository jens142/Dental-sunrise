<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Patients" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Patients - Sunrise Dental Clinic</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/tooth-icon.png">
</head>
<body>
<%@ include file="/WEB-INF/jspf/navbar.jspf" %>
<main class="app-main">
    <div class="page-wrap page-wrap-wide">
        <nav class="breadcrumb no-print">
            <a href="${pageContext.request.contextPath}/dashboard.jsp">Home</a>
            <span class="material-symbols-outlined">chevron_right</span>
            <span class="current">Patients</span>
        </nav>

        <div class="flex-between flex-wrap gap-3">
            <div>
                <h1 class="page-title">Patient Records</h1>
                <p class="page-subtitle">Register new patients and keep their contact details up to date</p>
            </div>
            <div class="flex gap-2">
                <button type="button" class="btn btn-primary" onclick="openModal('addPatientModal')">
                    <span class="material-symbols-outlined" style="font-size:18px;">person_add</span>Add Patient
                </button>
                <a href="${pageContext.request.contextPath}/appointments" class="btn btn-ghost">
                    <span class="material-symbols-outlined" style="font-size:18px;">add</span>New Appointment
                </a>
            </div>
        </div>

        <c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>
        <c:if test="${not empty successMessage}"><div class="alert alert-success">${successMessage}</div></c:if>

        <div class="table-card">
            <div class="table-card-header flex-between">
                <h3 class="section-title">All Patients</h3>
                <span class="text-sm text-faint">${patients.size()} record(s)</span>
            </div>
            <c:choose>
                <c:when test="${empty patients}">
                    <div class="table-empty">No patient records found. Use "Add Patient" to register one.</div>
                </c:when>
                <c:otherwise>
                    <div class="table-scroll">
                        <table class="data-table" id="patientsTable">
                            <thead>
                                <tr>
                                    <th data-sort-index="0">ID</th>
                                    <th data-sort-index="1">Patient</th>
                                    <th>Contact</th>
                                    <th>Email</th>
                                    <th>Date of Birth</th>
                                    <th>Gender</th>
                                    <th>Allergies</th>
                                    <th class="text-right no-print">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="patient" items="${patients}">
                                    <tr>
                                        <td>${patient.patientId}</td>
                                        <td style="font-weight:600;">${patient.patientName}</td>
                                        <td>${patient.contactNumber}</td>
                                        <td class="text-muted">${patient.email}</td>
                                        <td>${patient.dateOfBirth}</td>
                                        <td>${patient.gender}</td>
                                        <td>${patient.allergies}</td>
                                        <td class="text-right no-print">
                                            <button type="button" class="btn btn-ghost btn-sm edit-patient-btn"
                                                    data-id="${patient.patientId}" data-name="${patient.patientName}"
                                                    data-address="${patient.address}" data-contact="${patient.contactNumber}"
                                                    data-email="${patient.email}" data-dob="${patient.dateOfBirth}"
                                                    data-gender="${patient.gender}" data-allergies="${patient.allergies}">
                                                <span class="material-symbols-outlined" style="font-size:16px;">edit</span>
                                            </button>
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

<div class="modal-overlay" id="addPatientModal">
    <div class="modal-box">
        <button type="button" class="modal-close-btn" onclick="closeModal('addPatientModal')" aria-label="Close">
            <span class="material-symbols-outlined">close</span>
        </button>
        <h3>Add Patient</h3>
        <form action="${pageContext.request.contextPath}/patients" method="post" class="flex-col gap-3" style="margin-top:16px;">
            <input type="hidden" name="action" value="create">
            <div class="form-group"><label class="form-label">Full Name</label><input name="patientName" required class="form-input"></div>
            <div class="form-group"><label class="form-label">Contact Number</label><input name="contactNumber" required class="form-input" placeholder="0771234567"></div>
            <div class="form-group"><label class="form-label">Email</label><input type="email" name="email" class="form-input"></div>
            <div class="form-group"><label class="form-label">Address</label><input name="address" class="form-input"></div>
            <div class="grid grid-2">
                <div class="form-group"><label class="form-label">Date of Birth</label><input type="date" name="dateOfBirth" class="form-input"></div>
                <div class="form-group">
                    <label class="form-label">Gender</label>
                    <select name="gender" class="form-select">
                        <option value="">Not specified</option>
                        <option value="Male">Male</option>
                        <option value="Female">Female</option>
                        <option value="Other">Other</option>
                    </select>
                </div>
            </div>
            <div class="form-group"><label class="form-label">Allergies</label><textarea name="allergies" class="form-input" rows="2" placeholder="None"></textarea></div>
            <button type="submit" class="btn btn-primary btn-block">Register Patient</button>
        </form>
    </div>
</div>

<div class="modal-overlay" id="editPatientModal">
    <div class="modal-box">
        <button type="button" class="modal-close-btn" onclick="closeModal('editPatientModal')" aria-label="Close">
            <span class="material-symbols-outlined">close</span>
        </button>
        <h3>Edit Patient</h3>
        <form action="${pageContext.request.contextPath}/patients" method="post" class="flex-col gap-3" style="margin-top:16px;">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="patientId" id="editPatientId">
            <div class="form-group"><label class="form-label">Full Name</label><input name="patientName" id="editPatientName" required class="form-input"></div>
            <div class="form-group"><label class="form-label">Contact Number</label><input name="contactNumber" id="editPatientContact" required class="form-input"></div>
            <div class="form-group"><label class="form-label">Email</label><input type="email" name="email" id="editPatientEmail" class="form-input"></div>
            <div class="form-group"><label class="form-label">Address</label><input name="address" id="editPatientAddress" class="form-input"></div>
            <div class="grid grid-2">
                <div class="form-group"><label class="form-label">Date of Birth</label><input type="date" name="dateOfBirth" id="editPatientDob" class="form-input"></div>
                <div class="form-group">
                    <label class="form-label">Gender</label>
                    <select name="gender" id="editPatientGender" class="form-select">
                        <option value="">Not specified</option>
                        <option value="Male">Male</option>
                        <option value="Female">Female</option>
                        <option value="Other">Other</option>
                    </select>
                </div>
            </div>
            <div class="form-group"><label class="form-label">Allergies</label><textarea name="allergies" id="editPatientAllergies" class="form-input" rows="2"></textarea></div>
            <button type="submit" class="btn btn-primary btn-block">Save Changes</button>
        </form>
    </div>
</div>

<script>
    makeTableSortable('patientsTable');

    document.querySelectorAll('.edit-patient-btn').forEach((btn) => {
        btn.addEventListener('click', () => {
            document.getElementById('editPatientId').value = btn.dataset.id;
            document.getElementById('editPatientName').value = btn.dataset.name;
            document.getElementById('editPatientContact').value = btn.dataset.contact;
            document.getElementById('editPatientEmail').value = btn.dataset.email || '';
            document.getElementById('editPatientAddress').value = btn.dataset.address || '';
            document.getElementById('editPatientDob').value = btn.dataset.dob || '';
            document.getElementById('editPatientGender').value = btn.dataset.gender || '';
            document.getElementById('editPatientAllergies').value = btn.dataset.allergies || '';
            openModal('editPatientModal');
        });
    });
</script>
</body>
</html>
