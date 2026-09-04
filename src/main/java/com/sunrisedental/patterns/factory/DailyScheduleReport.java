package com.sunrisedental.patterns.factory;

import com.sunrisedental.patterns.singleton.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DailyScheduleReport implements Report {

    @Override
    public List<Map<String, Object>> generate(Date startDate, Date endDate) {
        // Uses only startDate - represents "today's" or a chosen day's schedule
        List<Map<String, Object>> rows = new ArrayList<>();
        String sql = "{CALL sp_get_daily_schedule(?)}";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setDate(1, startDate);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("appointmentNumber", rs.getString("appointment_number"));
                    row.put("patientName", rs.getString("patient_name"));
                    row.put("dentistName", rs.getString("dentist_name"));
                    row.put("treatmentName", rs.getString("treatment_name"));
                    row.put("appointmentTime", rs.getTime("appointment_time"));
                    row.put("status", rs.getString("status"));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error generating daily schedule report", e);
        }
        return rows;
    }

    @Override
    public String getReportTitle() {
        return "Daily Appointment Schedule";
    }
}