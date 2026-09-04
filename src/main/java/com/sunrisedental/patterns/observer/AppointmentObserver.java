package com.sunrisedental.patterns.observer;

import com.sunrisedental.model.Appointment;

/**
 * Observer Pattern - Observer Interface
 * -----------------------------------------
 * Any class that wants to be notified of appointment events implements this.
 */
public interface AppointmentObserver {
    void onAppointmentBooked(Appointment appointment);
}