package com.sunrisedental.patterns.observer;

import com.sunrisedental.model.Appointment;
import com.sunrisedental.service.NotificationService;

public class SMSNotifier implements AppointmentObserver {

    private final NotificationService notificationService;

    public SMSNotifier(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void onAppointmentBooked(Appointment appointment) {
        String message = "Sunrise Dental: Appt " + appointment.getAppointmentNumber() +
                " confirmed with " + appointment.getDentistName() +
                " on " + appointment.getAppointmentDate() +
                " at " + appointment.getAppointmentTime() + ".";

        notificationService.sendSMS(appointment, message);
    }
}