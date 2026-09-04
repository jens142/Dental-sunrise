package com.sunrisedental.patterns.factory;

import com.sunrisedental.patterns.singleton.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NoShowReport implements Report {

    @Override
    public List<Map<String, Object>> generate(Date startDate, Date endDate) {
        List<Map<String, Object>> rows = new ArrayList<>();
        String sql = "{CALL sp_no_show_report(?, ?)}";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("dentistName", rs.getString("dentist_name"));
                    row.put("noShows", rs.getInt("no_shows"));
                    row.put("cancellations", rs.getInt("cancellations"));
                    row.put("totalAppointments", rs.getInt("total_appointments"));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error generating no-show report", e);
        }
        return rows;
    }

    @Override
    public String getReportTitle() {
        return "No-Show & Cancellation Analysis";
    }
}