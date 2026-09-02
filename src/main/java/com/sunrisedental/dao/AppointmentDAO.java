package com.sunrisedental.dao;

import com.sunrisedental.model.Appointment;
import com.sunrisedental.patterns.singleton.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    private final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    /**
     * Registers a new appointment using the stored procedure sp_register_appointment.
     * The stored procedure handles double-booking checks and appointment number
     * generation at the database level (business rule enforcement).
     *
     * @return the generated appointment_number, or null if failed
     */
    public String registerAppointment(int patientId, int dentistId, int treatmentId,
                                       Date appointmentDate, Time appointmentTime, int createdBy) {

        String sql = "{CALL sp_register_appointment(?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = dbConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, patientId);
            stmt.setInt(2, dentistId);
            stmt.setInt(3, treatmentId);
            stmt.setDate(4, appointmentDate);
            stmt.setTime(5, appointmentTime);
            stmt.setInt(6, createdBy);
            stmt.registerOutParameter(7, Types.VARCHAR);

            stmt.execute();

            return stmt.getString(7);

        } catch (SQLException e) {
            // SIGNAL SQLSTATE '45000' from the trigger/procedure surfaces here
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Appointment findByAppointmentNumber(String appointmentNumber) {
        String sql = "SELECT a.*, p.patient_name, p.contact_number, p.email AS patient_email, " +
                     "d.dentist_name, t.treatment_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN dentists d ON a.dentist_id = d.dentist_id " +
                     "JOIN treatments t ON a.treatment_id = t.treatment_id " +
                     "WHERE a.appointment_number = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, appointmentNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowWithJoins(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding appointment", e);
        }
        return null;
    }

    public List<Appointment> getScheduleByDate(Date date) {
        String sql = "SELECT a.*, p.patient_name, p.contact_number, p.email AS patient_email, " +
                     "d.dentist_name, t.treatment_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN dentists d ON a.dentist_id = d.dentist_id " +
                     "JOIN treatments t ON a.treatment_id = t.treatment_id " +
                     "WHERE a.appointment_date = ? " +
                     "ORDER BY d.dentist_name, a.appointment_time";

        List<Appointment> list = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, date);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowWithJoins(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching schedule", e);
        }
        return list;
    }

    public boolean updateStatus(int appointmentId, Appointment.Status newStatus) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus.name());
            stmt.setInt(2, appointmentId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating appointment status", e);
        }
    }

    private Appointment mapRowWithJoins(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("appointment_id"));
        a.setAppointmentNumber(rs.getString("appointment_number"));
        a.setPatientId(rs.getInt("patient_id"));
        a.setDentistId(rs.getInt("dentist_id"));
        a.setTreatmentId(rs.getInt("treatment_id"));
        a.setAppointmentDate(rs.getDate("appointment_date"));
        a.setAppointmentTime(rs.getTime("appointment_time"));
        a.setStatus(Appointment.Status.valueOf(rs.getString("status")));
        a.setCreatedBy(rs.getInt("created_by"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        a.setPatientName(rs.getString("patient_name"));
        a.setDentistName(rs.getString("dentist_name"));
        a.setTreatmentName(rs.getString("treatment_name"));
        a.setContactNumber(rs.getString("contact_number"));
        a.setPatientEmail(rs.getString("patient_email"));
        return a;
    }
}