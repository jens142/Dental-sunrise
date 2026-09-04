package com.sunrisedental.patterns.observer;

import com.sunrisedental.model.Appointment;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer Pattern - Subject
 * ------------------------------
 * Maintains a list of observers and notifies all of them when an
 * appointment is booked. This decouples the booking process from
 * the notification mechanisms (Email, SMS, future push notifications).
 */
public class AppointmentSubject {

    private final List<AppointmentObserver> observers = new ArrayList<>();

    public void addObserver(AppointmentObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(AppointmentObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Appointment appointment) {
        for (AppointmentObserver observer : observers) {
            try {
                observer.onAppointmentBooked(appointment);
            } catch (Exception e) {
                // One failing notifier (e.g. SMS API down) should not
                // block others (e.g. Email) from still being sent.
                System.err.println("Notifier failed: " + e.getMessage());
            }
        }
    }
}