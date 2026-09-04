package com.sunrisedental.dao;

import com.sunrisedental.model.Treatment;
import com.sunrisedental.patterns.singleton.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TreatmentDAO {

    private final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public List<Treatment> getAllActiveTreatments() {
        String sql = "SELECT * FROM treatments WHERE is_active = TRUE ORDER BY treatment_name";
        List<Treatment> list = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching treatments", e);
        }
        return list;
    }

    public List<Treatment> getAllTreatments() {
        String sql = "SELECT * FROM treatments ORDER BY treatment_name";
        List<Treatment> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching treatments", e);
        }
        return list;
    }

    public void createTreatment(String name, String description, java.math.BigDecimal baseCost,
                                java.math.BigDecimal consultationFee) {
        String sql = "INSERT INTO treatments (treatment_name, description, base_cost, consultation_fee) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setBigDecimal(3, baseCost);
            stmt.setBigDecimal(4, consultationFee);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating treatment", e);
        }
    }

    public void updateTreatment(int treatmentId, String name, String description,
                                java.math.BigDecimal baseCost, java.math.BigDecimal consultationFee) {
        String sql = "UPDATE treatments SET treatment_name = ?, description = ?, base_cost = ?, " +
                     "consultation_fee = ? WHERE treatment_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setBigDecimal(3, baseCost);
            stmt.setBigDecimal(4, consultationFee);
            stmt.setInt(5, treatmentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating treatment", e);
        }
    }

    public void setActive(int treatmentId, boolean active) {
        String sql = "UPDATE treatments SET is_active = ? WHERE treatment_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, active);
            stmt.setInt(2, treatmentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating treatment status", e);
        }
    }

    public Treatment findById(int treatmentId) {
        String sql = "SELECT * FROM treatments WHERE treatment_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, treatmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding treatment", e);
        }
        return null;
    }

    private Treatment mapRow(ResultSet rs) throws SQLException {
        Treatment t = new Treatment();
        t.setTreatmentId(rs.getInt("treatment_id"));
        t.setTreatmentName(rs.getString("treatment_name"));
        t.setDescription(rs.getString("description"));
        t.setBaseCost(rs.getBigDecimal("base_cost"));
        t.setConsultationFee(rs.getBigDecimal("consultation_fee"));
        t.setActive(rs.getBoolean("is_active"));
        return t;
    }
}