package com.sunrisedental.dao;

import com.sunrisedental.model.Bill;
import com.sunrisedental.patterns.singleton.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    private final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    /**
     * Calls sp_calculate_bill stored procedure which computes and inserts
     * the bill row, then returns the computed values.
     */
    public Bill calculateAndSaveBill(int appointmentId, BigDecimal discount) {
        String sql = "{CALL sp_calculate_bill(?, ?)}";

        try (Connection conn = dbConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, appointmentId);
            stmt.setBigDecimal(2, discount != null ? discount : BigDecimal.ZERO);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Bill bill = new Bill();
                    bill.setAppointmentId(appointmentId);
                    bill.setConsultationFee(rs.getBigDecimal("consultation_fee"));
                    bill.setTreatmentCost(rs.getBigDecimal("treatment_cost"));
                    bill.setDiscount(rs.getBigDecimal("discount"));
                    bill.setTotalAmount(rs.getBigDecimal("total_amount"));
                    return bill;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating bill", e);
        }
        return null;
    }

    public Bill findByAppointmentId(int appointmentId) {
        String sql = "SELECT b.*, a.appointment_number, p.patient_name, " +
                     "d.dentist_name, t.treatment_name " +
                     "FROM bills b " +
                     "JOIN appointments a ON b.appointment_id = a.appointment_id " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN dentists d ON a.dentist_id = d.dentist_id " +
                     "JOIN treatments t ON a.treatment_id = t.treatment_id " +
                     "WHERE b.appointment_id = ? " +
                     "ORDER BY b.bill_id DESC LIMIT 1";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding bill", e);
        }
        return null;
    }

    public Bill findById(int billId) {
        String sql = "SELECT b.*, a.appointment_number, p.patient_name, " +
                     "d.dentist_name, t.treatment_name " +
                     "FROM bills b " +
                     "JOIN appointments a ON b.appointment_id = a.appointment_id " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN dentists d ON a.dentist_id = d.dentist_id " +
                     "JOIN treatments t ON a.treatment_id = t.treatment_id " +
                     "WHERE b.bill_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, billId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding bill", e);
        }
        return null;
    }

    public List<Bill> getAllBills() {
        String sql = "SELECT b.*, a.appointment_number, p.patient_name, " +
                     "d.dentist_name, t.treatment_name " +
                     "FROM bills b " +
                     "JOIN appointments a ON b.appointment_id = a.appointment_id " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN dentists d ON a.dentist_id = d.dentist_id " +
                     "JOIN treatments t ON a.treatment_id = t.treatment_id " +
                     "ORDER BY b.generated_on DESC, b.bill_id DESC";
        List<Bill> bills = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                bills.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching bills", e);
        }
        return bills;
    }

    public boolean markAsPaid(int billId) {
        String sql = "UPDATE bills SET payment_status = 'PAID' WHERE bill_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, billId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating payment status", e);
        }
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setBillId(rs.getInt("bill_id"));
        b.setAppointmentId(rs.getInt("appointment_id"));
        b.setConsultationFee(rs.getBigDecimal("consultation_fee"));
        b.setTreatmentCost(rs.getBigDecimal("treatment_cost"));
        b.setDiscount(rs.getBigDecimal("discount"));
        b.setTotalAmount(rs.getBigDecimal("total_amount"));
        b.setPaymentStatus(Bill.PaymentStatus.valueOf(rs.getString("payment_status")));
        b.setGeneratedOn(rs.getTimestamp("generated_on"));
        b.setAppointmentNumber(rs.getString("appointment_number"));
        b.setPatientName(rs.getString("patient_name"));
        b.setDentistName(rs.getString("dentist_name"));
        b.setTreatmentName(rs.getString("treatment_name"));
        return b;
    }
}