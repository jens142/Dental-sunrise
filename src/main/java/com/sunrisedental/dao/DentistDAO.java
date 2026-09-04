package com.sunrisedental.dao;

import com.sunrisedental.model.Dentist;
import com.sunrisedental.patterns.singleton.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DentistDAO {

    private final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public List<Dentist> getAllActiveDentists() {
        String sql = "SELECT * FROM dentists WHERE is_active = TRUE ORDER BY dentist_name";
        List<Dentist> list = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching dentists", e);
        }
        return list;
    }

    public List<Dentist> getAllDentists() {
        String sql = "SELECT * FROM dentists ORDER BY dentist_name";
        List<Dentist> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching dentists", e);
        }
        return list;
    }

    public void createDentist(String name, String specialization, String contact,
                              String email, String workingDays, String workingHours) {
        String sql = "INSERT INTO dentists (dentist_name, specialization, contact_number, email, " +
                     "working_days, working_hours) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, specialization);
            stmt.setString(3, contact);
            stmt.setString(4, email);
            stmt.setString(5, workingDays);
            stmt.setString(6, workingHours);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating dentist", e);
        }
    }

    public void updateDentist(int dentistId, String name, String specialization, String contact,
                              String email, String workingDays, String workingHours) {
        String sql = "UPDATE dentists SET dentist_name = ?, specialization = ?, contact_number = ?, " +
                     "email = ?, working_days = ?, working_hours = ? WHERE dentist_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, specialization);
            stmt.setString(3, contact);
            stmt.setString(4, email);
            stmt.setString(5, workingDays);
            stmt.setString(6, workingHours);
            stmt.setInt(7, dentistId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating dentist", e);
        }
    }

    public void setActive(int dentistId, boolean active) {
        String sql = "UPDATE dentists SET is_active = ? WHERE dentist_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, active);
            stmt.setInt(2, dentistId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating dentist status", e);
        }
    }

    public Dentist findById(int dentistId) {
        String sql = "SELECT * FROM dentists WHERE dentist_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dentistId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding dentist", e);
        }
        return null;
    }

    public boolean isSlotAvailable(int dentistId, Date appointmentDate, Time appointmentTime) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE dentist_id = ? " +
                     "AND appointment_date = ? AND appointment_time = ? " +
                     "AND status != 'CANCELLED'";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dentistId);
            stmt.setDate(2, appointmentDate);
            stmt.setTime(3, appointmentTime);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking slot availability", e);
        }
        return false;
    }

    private Dentist mapRow(ResultSet rs) throws SQLException {
        Dentist d = new Dentist();
        d.setDentistId(rs.getInt("dentist_id"));
        d.setDentistName(rs.getString("dentist_name"));
        d.setSpecialization(rs.getString("specialization"));
        d.setContactNumber(rs.getString("contact_number"));
        d.setEmail(rs.getString("email"));
        d.setWorkingDays(rs.getString("working_days"));
        d.setWorkingHours(rs.getString("working_hours"));
        d.setActive(rs.getBoolean("is_active"));
        return d;
    }
}