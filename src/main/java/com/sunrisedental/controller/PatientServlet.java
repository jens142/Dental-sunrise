package com.sunrisedental.controller;

import com.sunrisedental.dao.PatientDAO;
import com.sunrisedental.model.Patient;
import com.sunrisedental.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;

@WebServlet("/patients")
public class PatientServlet extends HttpServlet {

    private final PatientDAO patientDAO = new PatientDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("patients", patientDAO.getAllPatients());
        req.getRequestDispatcher("/patients.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        try {
            if ("update".equals(action)) {
                int patientId = ValidationUtil.parsePositiveInt(req.getParameter("patientId"), "Patient");
                Patient patient = buildPatientFromRequest(req);
                patient.setPatientId(patientId);

                if (!patientDAO.updatePatient(patient)) {
                    throw new IllegalStateException("Could not update patient record.");
                }
                req.setAttribute("successMessage", "Patient details updated.");
            } else {
                Patient patient = buildPatientFromRequest(req);

                if (patientDAO.findByContactNumber(patient.getContactNumber()) != null) {
                    throw new IllegalArgumentException(
                            "A patient with this contact number is already registered.");
                }

                int newId = patientDAO.createPatient(patient);
                if (newId <= 0) {
                    throw new IllegalStateException("Could not register patient.");
                }
                req.setAttribute("successMessage", "Patient registered with ID " + newId + ".");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            req.setAttribute("error", e.getMessage());
        } catch (Exception e) {
            req.setAttribute("error", "Unexpected error while saving patient.");
        }

        req.setAttribute("patients", patientDAO.getAllPatients());
        req.getRequestDispatcher("/patients.jsp").forward(req, resp);
    }

    private Patient buildPatientFromRequest(HttpServletRequest req) {
        String name = required(req, "patientName");
        String contactNumber = required(req, "contactNumber");
        ValidationUtil.validatePhone(contactNumber, "Contact number");

        String email = req.getParameter("email");
        if (email != null && !email.isBlank()) {
            ValidationUtil.validateEmail(email, "Email");
        }

        Patient patient = new Patient();
        patient.setPatientName(ValidationUtil.sanitize(name));
        patient.setAddress(ValidationUtil.sanitize(req.getParameter("address")));
        patient.setContactNumber(contactNumber);
        patient.setEmail(email != null ? email.trim() : null);

        String dobParam = req.getParameter("dateOfBirth");
        if (dobParam != null && !dobParam.isBlank()) {
            patient.setDateOfBirth(Date.valueOf(dobParam));
        }

        String genderParam = req.getParameter("gender");
        if (genderParam != null && !genderParam.isBlank()) {
            patient.setGender(Patient.Gender.valueOf(genderParam));
        }

        String allergies = req.getParameter("allergies");
        patient.setAllergies((allergies == null || allergies.isBlank())
                ? "None" : ValidationUtil.sanitize(allergies));

        return patient;
    }

    private String required(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        ValidationUtil.requireNonBlank(value, name);
        return value.trim();
    }
}
