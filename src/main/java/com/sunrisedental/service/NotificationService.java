package com.sunrisedental.service;

import com.sunrisedental.model.Appointment;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Handles the actual delivery mechanics for notifications.
 * Called by the Observer classes (EmailNotifier, SMSNotifier) -
 * kept separate so the Observer classes stay focused on "when to notify"
 * while this class handles "how to notify".
 */
public class NotificationService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String FROM_EMAIL = "noreply.sunrisedental@gmail.com";
    private static final String FROM_EMAIL_PASSWORD = System.getenv("SMTP_PASSWORD"); // never hardcode credentials

    public void sendEmail(Appointment appointment, String subject, String body) {
        if (appointment.getPatientEmail() == null || appointment.getPatientEmail().isBlank()) {
            // No email on file for this patient - safe to skip (SMS may still fire independently)
            System.out.println("Skipped email for " + appointment.getAppointmentNumber() +
                    ": no patient email on record.");
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, FROM_EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(appointment.getPatientEmail()));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
        } catch (MessagingException e) {
            // Logged, not thrown - one failed notification should not break the booking flow
            System.err.println("Email send failed for appointment " +
                    appointment.getAppointmentNumber() + ": " + e.getMessage());
        }
    }

    /**
     * SMS delivery - documented mock implementation.
     * Swap the body of this method for a real Twilio/gateway call when
     * an API key and budget are available; the interface used by the
     * Observer layer does not change either way.
     */
    public void sendSMS(Appointment appointment, String message) {
        try {
            // --- Real integration would look like this (Twilio example): ---
            // Message.creator(
            //     new PhoneNumber(appointment.getContactNumber()),
            //     new PhoneNumber(TWILIO_FROM_NUMBER),
            //     message
            // ).create();

            // --- Mock/simulated send for demo/examination purposes: ---
            System.out.println("[SIMULATED SMS to " + appointment.getContactNumber() + "]: " + message);
            logSimulatedSms(appointment, message);
        } catch (Exception e) {
            System.err.println("SMS send failed for appointment " +
                    appointment.getAppointmentNumber() + ": " + e.getMessage());
        }
    }

    private void logSimulatedSms(Appointment appointment, String message) {
        // In a real build this could write to an sms_log table for auditability
        System.out.println("Logged simulated SMS for " + appointment.getAppointmentNumber());
    }
}