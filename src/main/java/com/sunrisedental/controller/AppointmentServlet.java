package com.sunrisedental.controller;

import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.User;
import com.sunrisedental.dao.DentistDAO;
import com.sunrisedental.dao.TreatmentDAO;
import com.sunrisedental.service.AppointmentService;
import com.sunrisedental.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

@WebServlet("/appointments/*")
public class AppointmentServlet extends HttpServlet {

    private final AppointmentService appointmentService = new AppointmentService();
    private final DentistDAO dentistDAO = new DentistDAO();
    private final TreatmentDAO treatmentDAO = new TreatmentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();

        if ("/schedule".equals(pathInfo)) {
            String dateParam = req.getParameter("date");
            Date date = dateParam != null ? Date.valueOf(dateParam) : new Date(System.currentTimeMillis());
            List<Appointment> schedule = appointmentService.getDailySchedule(date);
            req.setAttribute("schedule", schedule);
            req.getRequestDispatcher("/search-appointment.jsp").forward(req, resp);
            return;
        }

        if ("/search".equals(pathInfo)) {
            String number = req.getParameter("appointmentNumber");
            Appointment appointment = appointmentService.findByAppointmentNumber(number);
            req.setAttribute("appointment", appointment);
            req.getRequestDispatcher("/search-appointment.jsp").forward(req, resp);
            return;
        }

        loadAppointmentOptions(req);
        req.getRequestDispatcher("/register-appointment.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();

        if ("/update-status".equals(pathInfo)) {
            updateStatus(req, resp);
            return;
        }

        registerAppointment(req, resp);
    }

    private void registerAppointment(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            int patientId = ValidationUtil.parsePositiveInt(req.getParameter("patientId"), "Patient");
            int dentistId = ValidationUtil.parsePositiveInt(req.getParameter("dentistId"), "Dentist");
            int treatmentId = ValidationUtil.parsePositiveInt(req.getParameter("treatmentId"), "Treatment");
            Date appointmentDate = Date.valueOf(req.getParameter("appointmentDate"));
            Time appointmentTime = Time.valueOf(req.getParameter("appointmentTime") + ":00");

            User currentUser = (User) req.getSession().getAttribute("currentUser");

            String appointmentNumber = appointmentService.bookAppointment(
                    patientId, dentistId, treatmentId, appointmentDate, appointmentTime,
                    currentUser.getUserId());

            req.setAttribute("successMessage", "Appointment booked: " + appointmentNumber);

        } catch (IllegalArgumentException | IllegalStateException e) {
            req.setAttribute("error", e.getMessage());
        } catch (Exception e) {
            req.setAttribute("error", "Unexpected error while booking appointment.");
        }

        loadAppointmentOptions(req);
        req.getRequestDispatcher("/register-appointment.jsp").forward(req, resp);
    }

    private void updateStatus(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            int appointmentId = ValidationUtil.parsePositiveInt(req.getParameter("appointmentId"), "Appointment");
            Appointment.Status newStatus = Appointment.Status.valueOf(req.getParameter("status"));

            appointmentService.updateStatus(appointmentId, newStatus);
            req.setAttribute("successMessage", "Status updated.");
        } catch (Exception e) {
            req.setAttribute("error", "Could not update status: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/appointments/schedule");
    }

    private void loadAppointmentOptions(HttpServletRequest req) {
        req.setAttribute("dentists", dentistDAO.getAllActiveDentists());
        req.setAttribute("treatments", treatmentDAO.getAllActiveTreatments());
    }
}