package com.sunrisedental.service;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.dao.DentistDAO;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.patterns.observer.AppointmentObserver;
import com.sunrisedental.patterns.observer.AppointmentSubject;
import com.sunrisedental.patterns.observer.EmailNotifier;
import com.sunrisedental.patterns.observer.SMSNotifier;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

/**
 * Orchestrates the appointment booking workflow:
 * 1. Validates input
 * 2. Delegates persistence + double-booking enforcement to the DB layer
 *    (stored procedure sp_register_appointment + trigger)
 * 3. Fires the Observer chain so Email/SMS notifiers react automatically
 */
public class AppointmentService {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final DentistDAO dentistDAO = new DentistDAO();
    private final AppointmentSubject appointmentSubject = new AppointmentSubject();

    public AppointmentService() {
        // Wire up Observer pattern once at construction time
        NotificationService notificationService = new NotificationService();
        appointmentSubject.addObserver(new EmailNotifier(notificationService));
        appointmentSubject.addObserver(new SMSNotifier(notificationService));
    }

    /** Allows Servlets/tests to add/remove observers dynamically if needed */
    public void registerObserver(AppointmentObserver observer) {
        appointmentSubject.addObserver(observer);
    }

    public String bookAppointment(int patientId, int dentistId, int treatmentId,
                                   Date appointmentDate, Time appointmentTime, int createdBy) {

        validateBookingInput(patientId, dentistId, treatmentId, appointmentDate, appointmentTime);

        // Application-level pre-check for a fast, friendly error message.
        // The DB trigger is still the authoritative guard against race conditions.
        if (!dentistDAO.isSlotAvailable(dentistId, appointmentDate, appointmentTime)) {
            throw new IllegalStateException("Selected dentist is already booked at this date/time.");
        }

        String appointmentNumber = appointmentDAO.registerAppointment(
                patientId, dentistId, treatmentId, appointmentDate, appointmentTime, createdBy);

        if (appointmentNumber == null) {
            throw new RuntimeException("Failed to register appointment.");
        }

        Appointment booked = appointmentDAO.findByAppointmentNumber(appointmentNumber);
        if (booked != null) {
            appointmentSubject.notifyObservers(booked); // triggers Email + SMS
        }

        return appointmentNumber;
    }

    public boolean updateStatus(int appointmentId, Appointment.Status newStatus) {
        if (appointmentId <= 0 || newStatus == null) {
            throw new IllegalArgumentException("Invalid appointment id or status.");
        }
        return appointmentDAO.updateStatus(appointmentId, newStatus);
    }

    public List<Appointment> getDailySchedule(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date is required.");
        }
        return appointmentDAO.getScheduleByDate(date);
    }

    public Appointment findByAppointmentNumber(String appointmentNumber) {
        if (appointmentNumber == null || appointmentNumber.isBlank()) {
            throw new IllegalArgumentException("Appointment number is required.");
        }
        return appointmentDAO.findByAppointmentNumber(appointmentNumber);
    }

    private void validateBookingInput(int patientId, int dentistId, int treatmentId,
                                       Date appointmentDate, Time appointmentTime) {
        if (patientId <= 0 || dentistId <= 0 || treatmentId <= 0) {
            throw new IllegalArgumentException("Patient, dentist and treatment must all be selected.");
        }
        if (appointmentDate == null || appointmentTime == null) {
            throw new IllegalArgumentException("Appointment date and time are required.");
        }
        if (appointmentDate.before(new Date(System.currentTimeMillis() - 86_400_000L))) {
            throw new IllegalArgumentException("Cannot book an appointment in the past.");
        }
    }
}