<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Admin Catalog" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Catalog - Sunrise Dental Clinic</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/tooth-icon.png">
</head>
<body>
<%@ include file="/WEB-INF/jspf/navbar.jspf" %>
<main class="app-main">
    <div class="page-wrap page-wrap-wide">
        <nav class="breadcrumb no-print">
            <a href="${pageContext.request.contextPath}/dashboard.jsp">Home</a>
            <span class="material-symbols-outlined">chevron_right</span>
            <span class="current">Admin Catalog</span>
        </nav>

        <div>
            <h1 class="page-title">Clinic Catalog</h1>
            <p class="page-subtitle">Manage dentists and treatments available for appointments</p>
        </div>

        <c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>
        <c:if test="${not empty successMessage}"><div class="alert alert-success">${successMessage}</div></c:if>

        <div class="grid grid-2">
            <form action="${pageContext.request.contextPath}/admin/catalog" method="post" class="card card-lg flex-col gap-3">
                <input type="hidden" name="recordType" value="dentist">
                <h2 class="section-title">Add Dentist</h2>
                <div class="form-group"><label class="form-label">Full Name</label><input name="dentistName" required class="form-input" placeholder="Dr. Firstname Lastname"></div>
                <div class="form-group"><label class="form-label">Specialization</label><input name="specialization" required class="form-input" placeholder="General Dentistry"></div>
                <div class="grid grid-2">
                    <div class="form-group"><label class="form-label">Contact Number</label><input name="contactNumber" required class="form-input"></div>
                    <div class="form-group"><label class="form-label">Email</label><input type="email" name="email" required class="form-input"></div>
                </div>
                <div class="grid grid-2">
                    <div class="form-group"><label class="form-label">Working Days</label><input name="workingDays" required class="form-input" placeholder="Mon-Fri"></div>
                    <div class="form-group"><label class="form-label">Working Hours</label><input name="workingHours" required class="form-input" placeholder="09:00-17:00"></div>
                </div>
                <button class="btn btn-primary" type="submit"><span class="material-symbols-outlined" style="font-size:18px;">person_add</span>Add Dentist</button>
            </form>

            <form action="${pageContext.request.contextPath}/admin/catalog" method="post" class="card card-lg flex-col gap-3">
                <input type="hidden" name="recordType" value="treatment">
                <h2 class="section-title">Add Treatment</h2>
                <div class="form-group"><label class="form-label">Treatment Name</label><input name="treatmentName" required class="form-input" placeholder="Dental Filling"></div>
                <div class="form-group"><label class="form-label">Description</label><textarea name="description" required class="form-input" rows="3" placeholder="Short description"></textarea></div>
                <div class="grid grid-2">
                    <div class="form-group"><label class="form-label">Treatment Cost</label><input type="number" name="baseCost" min="0" step="0.01" required class="form-input" placeholder="5000.00"></div>
                    <div class="form-group"><label class="form-label">Consultation Fee</label><input type="number" name="consultationFee" min="0" step="0.01" required class="form-input" placeholder="1000.00"></div>
                </div>
                <button class="btn btn-primary" type="submit"><span class="material-symbols-outlined" style="font-size:18px;">add_task</span>Add Treatment</button>
            </form>
        </div>

        <div class="grid grid-2">
            <div class="table-card">
                <div class="table-card-header flex-between"><h3 class="section-title">Dentists</h3><span class="text-sm text-faint">${dentists.size()} record(s)</span></div>
                <div class="table-scroll"><table class="data-table"><thead><tr><th>Name</th><th>Specialization</th><th>Schedule</th><th>Status</th><th class="text-right no-print">Actions</th></tr></thead><tbody>
                    <c:forEach var="dentist" items="${dentists}">
                    <tr>
                        <td style="font-weight:600;">${dentist.dentistName}</td>
                        <td>${dentist.specialization}</td>
                        <td>${dentist.workingDays}<br>${dentist.workingHours}</td>
                        <td><c:choose><c:when test="${dentist.active}"><span class="badge badge-success">Active</span></c:when><c:otherwise><span class="badge badge-danger">Inactive</span></c:otherwise></c:choose></td>
                        <td class="text-right no-print">
                            <div class="flex gap-2" style="justify-content:flex-end;">
                                <button type="button" class="btn btn-ghost btn-sm edit-dentist-btn"
                                        data-id="${dentist.dentistId}" data-name="${dentist.dentistName}"
                                        data-specialization="${dentist.specialization}" data-contact="${dentist.contactNumber}"
                                        data-email="${dentist.email}" data-days="${dentist.workingDays}" data-hours="${dentist.workingHours}">
                                    <span class="material-symbols-outlined" style="font-size:16px;">edit</span>
                                </button>
                                <form action="${pageContext.request.contextPath}/admin/catalog" method="post">
                                    <input type="hidden" name="recordType" value="dentist-status">
                                    <input type="hidden" name="dentistId" value="${dentist.dentistId}">
                                    <input type="hidden" name="active" value="${dentist.active ? 'false' : 'true'}">
                                    <button type="submit" class="btn btn-ghost btn-sm" title="${dentist.active ? 'Deactivate' : 'Activate'}">
                                        <span class="material-symbols-outlined" style="font-size:16px;">${dentist.active ? 'toggle_on' : 'toggle_off'}</span>
                                    </button>
                                </form>
                            </div>
                        </td>
                    </tr>
                    </c:forEach>
                </tbody></table></div>
            </div>
            <div class="table-card">
                <div class="table-card-header flex-between"><h3 class="section-title">Treatments</h3><span class="text-sm text-faint">${treatments.size()} record(s)</span></div>
                <div class="table-scroll"><table class="data-table"><thead><tr><th>Treatment</th><th>Description</th><th>Cost</th><th>Status</th><th class="text-right no-print">Actions</th></tr></thead><tbody>
                    <c:forEach var="treatment" items="${treatments}">
                    <tr>
                        <td style="font-weight:600;">${treatment.treatmentName}</td>
                        <td>${treatment.description}</td>
                        <td>Rs. ${treatment.baseCost}<br><span class="text-muted">Consult: Rs. ${treatment.consultationFee}</span></td>
                        <td><c:choose><c:when test="${treatment.active}"><span class="badge badge-success">Active</span></c:when><c:otherwise><span class="badge badge-danger">Inactive</span></c:otherwise></c:choose></td>
                        <td class="text-right no-print">
                            <div class="flex gap-2" style="justify-content:flex-end;">
                                <button type="button" class="btn btn-ghost btn-sm edit-treatment-btn"
                                        data-id="${treatment.treatmentId}" data-name="${treatment.treatmentName}"
                                        data-description="${treatment.description}" data-cost="${treatment.baseCost}"
                                        data-fee="${treatment.consultationFee}">
                                    <span class="material-symbols-outlined" style="font-size:16px;">edit</span>
                                </button>
                                <form action="${pageContext.request.contextPath}/admin/catalog" method="post">
                                    <input type="hidden" name="recordType" value="treatment-status">
                                    <input type="hidden" name="treatmentId" value="${treatment.treatmentId}">
                                    <input type="hidden" name="active" value="${treatment.active ? 'false' : 'true'}">
                                    <button type="submit" class="btn btn-ghost btn-sm" title="${treatment.active ? 'Deactivate' : 'Activate'}">
                                        <span class="material-symbols-outlined" style="font-size:16px;">${treatment.active ? 'toggle_on' : 'toggle_off'}</span>
                                    </button>
                                </form>
                            </div>
                        </td>
                    </tr>
                    </c:forEach>
                </tbody></table></div>
            </div>
        </div>
    </div>
</main>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>

<div class="modal-overlay" id="editDentistModal">
    <div class="modal-box">
        <button type="button" class="modal-close-btn" onclick="closeModal('editDentistModal')" aria-label="Close">
            <span class="material-symbols-outlined">close</span>
        </button>
        <h3>Edit Dentist</h3>
        <form action="${pageContext.request.contextPath}/admin/catalog" method="post" class="flex-col gap-3" style="margin-top:16px;">
            <input type="hidden" name="recordType" value="dentist-update">
            <input type="hidden" name="dentistId" id="editDentistId">
            <div class="form-group"><label class="form-label">Full Name</label><input name="dentistName" id="editDentistName" required class="form-input"></div>
            <div class="form-group"><label class="form-label">Specialization</label><input name="specialization" id="editDentistSpecialization" required class="form-input"></div>
            <div class="form-group"><label class="form-label">Contact Number</label><input name="contactNumber" id="editDentistContact" required class="form-input"></div>
            <div class="form-group"><label class="form-label">Email</label><input type="email" name="email" id="editDentistEmail" required class="form-input"></div>
            <div class="grid grid-2">
                <div class="form-group"><label class="form-label">Working Days</label><input name="workingDays" id="editDentistDays" required class="form-input"></div>
                <div class="form-group"><label class="form-label">Working Hours</label><input name="workingHours" id="editDentistHours" required class="form-input"></div>
            </div>
            <button type="submit" class="btn btn-primary btn-block">Save Changes</button>
        </form>
    </div>
</div>

<div class="modal-overlay" id="editTreatmentModal">
    <div class="modal-box">
        <button type="button" class="modal-close-btn" onclick="closeModal('editTreatmentModal')" aria-label="Close">
            <span class="material-symbols-outlined">close</span>
        </button>
        <h3>Edit Treatment</h3>
        <form action="${pageContext.request.contextPath}/admin/catalog" method="post" class="flex-col gap-3" style="margin-top:16px;">
            <input type="hidden" name="recordType" value="treatment-update">
            <input type="hidden" name="treatmentId" id="editTreatmentId">
            <div class="form-group"><label class="form-label">Treatment Name</label><input name="treatmentName" id="editTreatmentName" required class="form-input"></div>
            <div class="form-group"><label class="form-label">Description</label><textarea name="description" id="editTreatmentDescription" required class="form-input" rows="3"></textarea></div>
            <div class="grid grid-2">
                <div class="form-group"><label class="form-label">Treatment Cost</label><input type="number" name="baseCost" id="editTreatmentCost" min="0" step="0.01" required class="form-input"></div>
                <div class="form-group"><label class="form-label">Consultation Fee</label><input type="number" name="consultationFee" id="editTreatmentFee" min="0" step="0.01" required class="form-input"></div>
            </div>
            <button type="submit" class="btn btn-primary btn-block">Save Changes</button>
        </form>
    </div>
</div>

<script>
    document.querySelectorAll('.edit-dentist-btn').forEach((btn) => {
        btn.addEventListener('click', () => {
            document.getElementById('editDentistId').value = btn.dataset.id;
            document.getElementById('editDentistName').value = btn.dataset.name;
            document.getElementById('editDentistSpecialization').value = btn.dataset.specialization;
            document.getElementById('editDentistContact').value = btn.dataset.contact;
            document.getElementById('editDentistEmail').value = btn.dataset.email;
            document.getElementById('editDentistDays').value = btn.dataset.days;
            document.getElementById('editDentistHours').value = btn.dataset.hours;
            openModal('editDentistModal');
        });
    });

    document.querySelectorAll('.edit-treatment-btn').forEach((btn) => {
        btn.addEventListener('click', () => {
            document.getElementById('editTreatmentId').value = btn.dataset.id;
            document.getElementById('editTreatmentName').value = btn.dataset.name;
            document.getElementById('editTreatmentDescription').value = btn.dataset.description;
            document.getElementById('editTreatmentCost').value = btn.dataset.cost;
            document.getElementById('editTreatmentFee').value = btn.dataset.fee;
            openModal('editTreatmentModal');
        });
    });
</script>
</body>
</html>
