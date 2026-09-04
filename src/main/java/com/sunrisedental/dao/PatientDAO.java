package com.sunrisedental.dao;

import com.sunrisedental.model.Patient;
import com.sunrisedental.patterns.singleton.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    private final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public int createPatient(Patient patient) {
        String sql = "INSERT INTO patients (patient_name, address, contact_number, email, " +
                     "date_of_birth, gender, allergies) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, patient.getPatientName());
            stmt.setString(2, patient.getAddress());
            stmt.setString(3, patient.getContactNumber());
            stmt.setString(4, patient.getEmail());
            stmt.setDate(5, patient.getDateOfBirth());
            stmt.setString(6, patient.getGender() != null ? patient.getGender().name() : null);
            stmt.setString(7, patient.getAllergies() != null ? patient.getAllergies() : "None");

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating patient", e);
        }
        return -1;
    }

    public Patient findById(int patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding patient", e);
        }
        return null;
    }

    public List<Patient> getAllPatients() {
        String sql = "SELECT * FROM patients ORDER BY patient_name";
        List<Patient> patients = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                patients.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching patients", e);
        }
        return patients;
    }

    /** Search patients by contact number - useful to avoid duplicate registration */
    public Patient findByContactNumber(String contactNumber) {
        String sql = "SELECT * FROM patients WHERE contact_number = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contactNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding patient by contact", e);
        }
        return null;
    }

    public List<Patient> searchByName(String namePattern) {
        String sql = "SELECT * FROM patients WHERE patient_name LIKE ? ORDER BY patient_name";
        List<Patient> results = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + namePattern + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching patients", e);
        }
        return results;
    }

    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET patient_name=?, address=?, contact_number=?, " +
                     "email=?, date_of_birth=?, gender=?, allergies=? WHERE patient_id=?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patient.getPatientName());
            stmt.setString(2, patient.getAddress());
            stmt.setString(3, patient.getContactNumber());
            stmt.setString(4, patient.getEmail());
            stmt.setDate(5, patient.getDateOfBirth());
            stmt.setString(6, patient.getGender() != null ? patient.getGender().name() : null);
            stmt.setString(7, patient.getAllergies());
            stmt.setInt(8, patient.getPatientId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating patient", e);
        }
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setPatientId(rs.getInt("patient_id"));
        p.setPatientName(rs.getString("patient_name"));
        p.setAddress(rs.getString("address"));
        p.setContactNumber(rs.getString("contact_number"));
        p.setEmail(rs.getString("email"));
        p.setDateOfBirth(rs.getDate("date_of_birth"));
        String gender = rs.getString("gender");
        if (gender != null) p.setGender(Patient.Gender.valueOf(gender));
        p.setAllergies(rs.getString("allergies"));
        p.setRegisteredOn(rs.getTimestamp("registered_on"));
        return p;
    }
}