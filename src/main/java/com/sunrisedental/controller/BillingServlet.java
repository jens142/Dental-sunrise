package com.sunrisedental.controller;

import com.sunrisedental.model.Bill;
import com.sunrisedental.service.BillingService;
import com.sunrisedental.util.PDFGenerator;
import com.sunrisedental.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

@WebServlet("/billing/*")
public class BillingServlet extends HttpServlet {

    private final BillingService billingService = new BillingService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();

        if ("/print".equals(pathInfo)) {
            int appointmentId = ValidationUtil.parsePositiveInt(req.getParameter("appointmentId"), "Appointment");
            Bill bill = billingService.getBillForAppointment(appointmentId);

            if (bill == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Bill not found.");
                return;
            }

            byte[] logoBytes;
            try (InputStream logoStream = getServletContext().getResourceAsStream("/images/logo.png")) {
                logoBytes = logoStream != null ? logoStream.readAllBytes() : null;
            }
            byte[] pdfBytes = PDFGenerator.generateReceipt(bill, logoBytes);
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "inline; filename=receipt-" + bill.getBillId() + ".pdf");
            resp.getOutputStream().write(pdfBytes);
            return;
        }

        req.setAttribute("bills", billingService.getAllBills());
        req.getRequestDispatcher("/billing.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();

        if ("/mark-paid".equals(pathInfo)) {
            markPaid(req, resp);
            return;
        }

        generateBill(req, resp);
    }

    private void markPaid(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            int billId = ValidationUtil.parsePositiveInt(req.getParameter("billId"), "Bill");
            billingService.markPaid(billId);

            Bill bill = billingService.getBillById(billId);
            if (bill == null) {
                req.setAttribute("error", "Bill not found after update.");
            } else {
                req.setAttribute("bill", bill);
                req.setAttribute("successMessage", "Bill marked as paid.");
            }
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
        } catch (Exception e) {
            req.setAttribute("error", "Unexpected error while updating payment status.");
        }

        req.setAttribute("bills", billingService.getAllBills());
        req.getRequestDispatcher("/billing.jsp").forward(req, resp);
    }

    private void generateBill(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            int appointmentId = ValidationUtil.parsePositiveInt(req.getParameter("appointmentId"), "Appointment");
            String discountParam = req.getParameter("discount");
            BigDecimal discount = (discountParam != null && !discountParam.isBlank())
                    ? new BigDecimal(discountParam) : BigDecimal.ZERO;

            BillingService.BillingCategory category = "INSURANCE".equalsIgnoreCase(req.getParameter("category"))
                    ? BillingService.BillingCategory.INSURANCE
                    : BillingService.BillingCategory.STANDARD;

            Bill bill = billingService.generateBill(appointmentId, discount, category);
            req.setAttribute("bill", bill);
            req.setAttribute("successMessage", "Bill generated successfully.");

        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
        } catch (Exception e) {
            req.setAttribute("error", "Unexpected error while generating bill.");
        }

        req.setAttribute("bills", billingService.getAllBills());
        req.getRequestDispatcher("/billing.jsp").forward(req, resp);
    }
}