package com.sunrisedental.patterns.observer;

import com.sunrisedental.model.Appointment;
import com.sunrisedental.service.NotificationService;

public class EmailNotifier implements AppointmentObserver {

    private final NotificationService notificationService;

    public EmailNotifier(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void onAppointmentBooked(Appointment appointment) {
        String subject = "Appointment Confirmation - " + appointment.getAppointmentNumber();
        String body = buildEmailBody(appointment);
        notificationService.sendEmail(appointment, subject, body);
    }

    private String buildEmailBody(Appointment appointment) {
        return "Dear " + appointment.getPatientName() + ",\n\n" +
               "Your appointment has been booked successfully.\n\n" +
               "Appointment No: " + appointment.getAppointmentNumber() + "\n" +
               "Dentist: " + appointment.getDentistName() + "\n" +
               "Treatment: " + appointment.getTreatmentName() + "\n" +
               "Date: " + appointment.getAppointmentDate() + "\n" +
               "Time: " + appointment.getAppointmentTime() + "\n\n" +
               "Please arrive 10 minutes early.\n\n" +
               "Sunrise Dental Clinic, Colombo";
    }
}