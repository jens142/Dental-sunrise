package com.sunrisedental.dao;

import com.sunrisedental.patterns.singleton.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardDAO {

    private final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    public int countAppointmentsOn(Date date) {
        return count("SELECT COUNT(*) FROM appointments WHERE appointment_date = ?", date);
    }

    public int countPatients() {
        return count("SELECT COUNT(*) FROM patients");
    }

    public int countPendingBills() {
        return count("SELECT COUNT(*) FROM bills WHERE payment_status <> 'PAID'");
    }

    public BigDecimal revenueThisMonth(Date startDate, Date endDate) {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM bills " +
                     "WHERE payment_status = 'PAID' AND DATE(generated_on) BETWEEN ? AND ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading dashboard revenue", e);
        }
    }

    public List<Map<String, Object>> appointmentsByDentist(Date date) {
        String sql = "SELECT d.dentist_name, COUNT(a.appointment_id) AS appointment_count " +
                     "FROM dentists d LEFT JOIN appointments a ON a.dentist_id = d.dentist_id " +
                     "AND a.appointment_date = ? GROUP BY d.dentist_name " +
                     "ORDER BY d.dentist_name";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, date);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("label", rs.getString("dentist_name"));
                    row.put("count", rs.getInt("appointment_count"));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading appointment chart", e);
        }
        return rows;
    }

    public List<Map<String, Object>> revenueByDay(Date startDate, Date endDate) {
        String sql = "SELECT DATE(generated_on) AS revenue_date, SUM(total_amount) AS amount " +
                     "FROM bills WHERE payment_status = 'PAID' AND DATE(generated_on) BETWEEN ? AND ? " +
                     "GROUP BY DATE(generated_on) ORDER BY revenue_date";
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("label", rs.getDate("revenue_date").toString());
                    row.put("amount", rs.getBigDecimal("amount"));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading revenue chart", e);
        }
        return rows;
    }

    private int count(String sql, Object... parameters) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                stmt.setObject(i + 1, parameters[i]);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading dashboard count", e);
        }
    }
}
