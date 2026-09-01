<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Billing & Payments" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Billing - Sunrise Dental Clinic</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/tooth-icon.png">
</head>
<body>

<%@ include file="/WEB-INF/jspf/navbar.jspf" %>

<main class="app-main">
    <div class="page-wrap">

        <nav class="breadcrumb no-print">
            <a href="${pageContext.request.contextPath}/dashboard.jsp">Home</a>
            <span class="material-symbols-outlined">chevron_right</span>
            <span class="current">Billing &amp; Payments</span>
        </nav>

        <div class="flex-between flex-wrap gap-3">
            <div>
                <h1 class="page-title">Billing &amp; Payments</h1>
                <p class="page-subtitle">Manage patient invoices and process transactions</p>
            </div>
            <c:if test="${not empty bill}">
            <button type="button" class="btn btn-ghost no-print" onclick="window.print()">
                <span class="material-symbols-outlined" style="font-size:18px;">print</span>Print Invoice
            </button>
            </c:if>
        </div>

        <c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>
        <c:if test="${not empty successMessage}"><div class="alert alert-success">${successMessage}</div></c:if>

        <form action="${pageContext.request.contextPath}/billing" method="post" class="search-bar no-print">
            <span class="material-symbols-outlined icon-leading">tag</span>
            <input type="number" name="appointmentId" required class="form-input" style="flex:1;" placeholder="Appointment ID">
            <select name="category" class="form-select" style="width:auto;">
                <option value="STANDARD">Standard</option>
                <option value="INSURANCE">Insurance</option>
            </select>
            <input type="number" step="0.01" name="discount" placeholder="Discount" class="form-input" style="width:120px;">
            <button type="submit" class="btn btn-primary btn-pill">Generate Bill</button>
        </form>

        <div class="table-card">
            <div class="table-card-header flex-between flex-wrap gap-3">
                <h3 class="section-title">All Invoices</h3>
                <span class="text-sm text-faint">${bills.size()} record(s)</span>
            </div>
            <c:choose>
                <c:when test="${empty bills}">
                    <div class="table-empty">No bills found. Generate a bill using an appointment ID above.</div>
                </c:when>
                <c:otherwise>
                    <div class="table-scroll">
                        <table class="data-table" id="billsTable">
                            <thead>
                                <tr>
                                    <th>Bill ID</th>
                                    <th>Appointment</th>
                                    <th>Patient</th>
                                    <th>Dentist</th>
                                    <th>Treatment</th>
                                    <th>Total</th>
                                    <th>Status</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="savedBill" items="${bills}">
                                    <tr>
                                        <td>${savedBill.billId}</td>
                                        <td>${savedBill.appointmentNumber}</td>
                                        <td style="font-weight:600;">${savedBill.patientName}</td>
                                        <td class="text-muted">${savedBill.dentistName}</td>
                                        <td class="text-muted">${savedBill.treatmentName}</td>
                                        <td>Rs. ${savedBill.totalAmount}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${savedBill.paymentStatus == 'PAID'}"><span class="badge badge-success">${savedBill.paymentStatus}</span></c:when>
                                                <c:otherwise><span class="badge badge-danger">${savedBill.paymentStatus}</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/billing/print?appointmentId=${savedBill.appointmentId}" target="_blank" class="btn btn-ghost btn-sm">
                                                <span class="material-symbols-outlined" style="font-size:16px;">picture_as_pdf</span>Receipt
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <c:if test="${not empty bill}">
        <div class="grid grid-12">

            <div class="card col-span-4 card-accent-left">
                <h2 class="section-title" style="margin-bottom:16px;">Patient Details</h2>
                <div class="flex-col gap-3 text-sm">
                    <div class="flex-between" style="border-bottom:1px solid var(--color-border);padding-bottom:8px;">
                        <span class="text-muted">Appointment</span><span style="font-weight:600;">${bill.appointmentNumber}</span>
                    </div>
                    <div class="flex-between" style="border-bottom:1px solid var(--color-border);padding-bottom:8px;">
                        <span class="text-muted">Patient</span><span style="font-weight:600;">${bill.patientName}</span>
                    </div>
                    <div class="flex-between" style="border-bottom:1px solid var(--color-border);padding-bottom:8px;">
                        <span class="text-muted">Dentist</span><span style="font-weight:600;">${bill.dentistName}</span>
                    </div>
                    <div class="flex-between">
                        <span class="text-muted">Treatment</span><span style="font-weight:600;">${bill.treatmentName}</span>
                    </div>
                </div>

                <div class="flex-col gap-2 no-print" style="margin-top:24px;">
                    <a href="${pageContext.request.contextPath}/billing/print?appointmentId=${bill.appointmentId}"
                       target="_blank" class="btn btn-ghost btn-block">
                        <span class="material-symbols-outlined" style="font-size:18px;">picture_as_pdf</span>Generate PDF Receipt
                    </a>
                    <button type="button" class="btn btn-outline btn-block" onclick="copyToClipboard('${bill.appointmentNumber}', 'Appointment number')">
                        <span class="material-symbols-outlined" style="font-size:18px;">content_copy</span>Copy Reference
                    </button>
                </div>
            </div>

            <div class="card card-lg col-span-8 flex-col">
                <div class="flex-between" style="border-bottom:1px solid var(--color-border);padding-bottom:24px;margin-bottom:24px;">
                    <div>
                        <h2 style="font-size:1.5rem;font-weight:800;color:var(--color-primary);">Invoice</h2>
                        <p class="text-sm text-muted" style="margin-top:4px;">Bill #${bill.billId}</p>
                    </div>
                    <c:choose>
                        <c:when test="${bill.paymentStatus == 'PAID'}"><span class="badge badge-success">${bill.paymentStatus}</span></c:when>
                        <c:otherwise><span class="badge badge-danger">${bill.paymentStatus}</span></c:otherwise>
                    </c:choose>
                </div>

                <div class="flex-col gap-2" style="margin-bottom:24px;">
                    <div class="flex-between text-sm" style="padding:12px;background:var(--color-bg);border-radius:8px;">
                        <span>Consultation Fee</span>
                        <span style="font-weight:600;"><fmt:formatNumber value="${bill.consultationFee}" type="currency" currencySymbol="Rs. "/></span>
                    </div>
                    <div class="flex-between text-sm" style="padding:12px;background:var(--color-bg);border-radius:8px;">
                        <span>Treatment Cost</span>
                        <span style="font-weight:600;"><fmt:formatNumber value="${bill.treatmentCost}" type="currency" currencySymbol="Rs. "/></span>
                    </div>
                    <div class="flex-between text-sm" style="padding:12px;background:var(--color-bg);border-radius:8px;color:var(--color-secondary);">
                        <span>Discount</span>
                        <span style="font-weight:600;">- <fmt:formatNumber value="${bill.discount}" type="currency" currencySymbol="Rs. "/></span>
                    </div>
                </div>

                <div class="flex-between" style="align-items:baseline;padding:16px;background:var(--color-primary-light);opacity:1;border-radius:8px;margin-bottom:24px;">
                    <span style="font-weight:700;">Total Due</span>
                    <span style="font-size:1.85rem;font-weight:800;color:var(--color-primary);">
                        <fmt:formatNumber value="${bill.totalAmount}" type="currency" currencySymbol="Rs. "/>
                    </span>
                </div>

                <c:if test="${bill.paymentStatus != 'PAID'}">
                <form action="${pageContext.request.contextPath}/billing/mark-paid" method="post" class="no-print">
                    <input type="hidden" name="billId" value="${bill.billId}">
                    <button type="submit" class="btn btn-secondary btn-block">
                        Mark as Paid <span class="material-symbols-outlined" style="font-size:18px;">arrow_forward</span>
                    </button>
                </form>
                </c:if>
            </div>
        </div>
        </c:if>

    </div>
</main>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<script>
    makeTableSortable('billsTable');
</script>
</body>
</html>
