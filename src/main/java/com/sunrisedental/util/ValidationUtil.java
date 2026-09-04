package com.sunrisedental.util;

import java.util.regex.Pattern;

/**
 * Shared server-side validation helpers.
 * Pairs with client-side checks in validation.js - server-side is the
 * authoritative layer since client-side JS can always be bypassed.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^0\\d{9}$"); // Sri Lankan local format, e.g. 0771234567

    public static int parsePositiveInt(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed <= 0) {
                throw new IllegalArgumentException(fieldName + " must be a positive number.");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }

    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static void validateEmail(String email, String fieldName) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException(fieldName + " must be a valid email address.");
        }
    }

    public static void validatePhone(String phone, String fieldName) {
        if (!isValidPhone(phone)) {
            throw new IllegalArgumentException(fieldName + " must be a valid 10-digit phone number.");
        }
    }

    /** Basic sanitisation for text fields going into reports/PDFs, not a substitute for PreparedStatement. */
    public static String sanitize(String input) {
        return input == null ? "" : input.trim().replaceAll("[<>]", "");
    }
}