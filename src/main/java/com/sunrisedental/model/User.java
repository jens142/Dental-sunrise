package com.sunrisedental.model;

import java.sql.Timestamp;

/**
 * Represents a staff user (Admin / Receptionist / Dentist) who can log in.
 */
public class User {

    public enum Role {
        ADMIN, RECEPTIONIST, DENTIST
    }

    private int userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private Role role;
    private String email;
    private boolean active;
    private Timestamp createdAt;

    public User() {}

    public User(int userId, String username, String passwordHash, String fullName,
                Role role, String email, boolean active, Timestamp createdAt) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.email = email;
        this.active = active;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
