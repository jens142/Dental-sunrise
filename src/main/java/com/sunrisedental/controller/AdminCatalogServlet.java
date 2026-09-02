package com.sunrisedental.controller;

import com.sunrisedental.dao.DentistDAO;
import com.sunrisedental.dao.TreatmentDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/admin/catalog")
public class AdminCatalogServlet extends HttpServlet {

    private final DentistDAO dentistDAO = new DentistDAO();
    private final TreatmentDAO treatmentDAO = new TreatmentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        loadCatalog(req);
        req.getRequestDispatcher("/admin-catalog.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String recordType = req.getParameter("recordType");
            if ("dentist".equals(recordType)) {
                dentistDAO.createDentist(
                        required(req, "dentistName"), required(req, "specialization"),
                        required(req, "contactNumber"), required(req, "email"),
                        required(req, "workingDays"), required(req, "workingHours"));
                req.setAttribute("successMessage", "Dentist added successfully.");
            } else if ("treatment".equals(recordType)) {
                BigDecimal baseCost = money(req, "baseCost");
                BigDecimal consultationFee = money(req, "consultationFee");
                treatmentDAO.createTreatment(required(req, "treatmentName"),
                        required(req, "description"), baseCost, consultationFee);
                req.setAttribute("successMessage", "Treatment added successfully.");
            } else if ("dentist-update".equals(recordType)) {
                int dentistId = positiveInt(req, "dentistId");
                dentistDAO.updateDentist(dentistId,
                        required(req, "dentistName"), required(req, "specialization"),
                        required(req, "contactNumber"), required(req, "email"),
                        required(req, "workingDays"), required(req, "workingHours"));
                req.setAttribute("successMessage", "Dentist details updated.");
            } else if ("treatment-update".equals(recordType)) {
                int treatmentId = positiveInt(req, "treatmentId");
                BigDecimal baseCost = money(req, "baseCost");
                BigDecimal consultationFee = money(req, "consultationFee");
                treatmentDAO.updateTreatment(treatmentId, required(req, "treatmentName"),
                        required(req, "description"), baseCost, consultationFee);
                req.setAttribute("successMessage", "Treatment details updated.");
            } else if ("dentist-status".equals(recordType)) {
                int dentistId = positiveInt(req, "dentistId");
                boolean active = "true".equals(req.getParameter("active"));
                dentistDAO.setActive(dentistId, active);
                req.setAttribute("successMessage", active ? "Dentist re-activated." : "Dentist deactivated.");
            } else if ("treatment-status".equals(recordType)) {
                int treatmentId = positiveInt(req, "treatmentId");
                boolean active = "true".equals(req.getParameter("active"));
                treatmentDAO.setActive(treatmentId, active);
                req.setAttribute("successMessage", active ? "Treatment re-activated." : "Treatment deactivated.");
            } else {
                throw new IllegalArgumentException("Select a valid record type.");
            }
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
        } catch (Exception e) {
            req.setAttribute("error", "Could not save the record. Check for duplicate names or invalid values.");
        }

        loadCatalog(req);
        req.getRequestDispatcher("/admin-catalog.jsp").forward(req, resp);
    }

    private void loadCatalog(HttpServletRequest req) {
        req.setAttribute("dentists", dentistDAO.getAllDentists());
        req.setAttribute("treatments", treatmentDAO.getAllTreatments());
    }

    private String required(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Please complete all required fields.");
        }
        return value.trim();
    }

    private int positiveInt(HttpServletRequest req, String name) {
        try {
            int value = Integer.parseInt(required(req, name));
            if (value <= 0) throw new IllegalArgumentException("Invalid record reference.");
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid record reference.");
        }
    }

    private BigDecimal money(HttpServletRequest req, String name) {
        try {
            BigDecimal value = new BigDecimal(required(req, name));
            if (value.signum() < 0) throw new IllegalArgumentException("Prices cannot be negative.");
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Enter valid prices for the treatment.");
        }
    }
}
