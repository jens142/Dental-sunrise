package com.sunrisedental.patterns.factory;

import com.sunrisedental.patterns.singleton.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RevenueReport implements Report {

    @Override
    public List<Map<String, Object>> generate(Date startDate, Date endDate) {
        List<Map<String, Object>> rows = new ArrayList<>();
        String sql = "{CALL sp_revenue_report(?, ?)}";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("treatmentName", rs.getString("treatment_name"));
                    row.put("totalBills", rs.getInt("total_bills"));
                    row.put("totalRevenue", rs.getBigDecimal("total_revenue"));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error generating revenue report", e);
        }
        return rows;
    }

    @Override
    public String getReportTitle() {
        return "Revenue Report by Treatment Type";
    }
}