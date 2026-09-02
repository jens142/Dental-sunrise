package com.sunrisedental.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Wraps BCrypt hashing/verification so no other class in the codebase
 * touches raw password bytes or a hashing algorithm directly.
 * Satisfies the "password hashing" security requirement.
 */
public class PasswordUtil {

    private static final int WORK_FACTOR = 12; // cost factor - higher = slower/safer

    public static String hash(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    public static boolean verify(String plainTextPassword, String storedHash) {
        if (plainTextPassword == null || storedHash == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainTextPassword, storedHash);
        } catch (IllegalArgumentException e) {
            // Malformed hash in DB - fail closed rather than throwing up the stack
            return false;
        }
    }
}