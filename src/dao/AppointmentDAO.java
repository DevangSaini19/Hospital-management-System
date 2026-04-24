package dao;

import db.DBConnection;
import models.Appointment;

import java.sql.*;
import java.util.ArrayList;

/**
 * Appointment Data Access Object (DAO)
 * Handles all database operations related to appointments
 * 
 * Features:
 * - Book new appointment with validation
 * - Prevent duplicate appointments (same doctor, same time)
 * - Validate patient and doctor existence
 * - Get all appointments with doctor/patient info
 * - Update appointment status
 * - Proper exception handling
 */
public class AppointmentDAO {

    // Data Structure: ArrayList for appointment list
    private ArrayList<Appointment> appointmentList = new ArrayList<>();

    // ==================== BOOK APPOINTMENT ====================
    /**
     * Book a new appointment
     * Validates patient, doctor, and prevents duplicate appointments
     * 
     * SQL Query:
     * INSERT INTO appointments (patient_id, doctor_id, appointment_date, 
     *                           appointment_time, department, reason, status, token_number) 
     * VALUES (?,?,?,?,?,?,?,?)
     * 
     * @param appt Appointment object with data
     * @return true if appointment booked successfully, false otherwise
     */
    public boolean bookAppointment(Appointment appt) {
        // Validate appointment input
        if (!validateAppointmentInput(appt)) {
            System.err.println("✗ Invalid appointment data provided");
            return false;
        }

        // Validate patient exists
        if (!isPatientExists(appt.getPatientId())) {
            System.err.println("✗ Error: Patient ID " + appt.getPatientId() + " does not exist!");
            return false;
        }

        // Validate doctor exists
        if (!isDoctorExists(appt.getDoctorId())) {
            System.err.println("✗ Error: Doctor ID " + appt.getDoctorId() + " does not exist!");
            return false;
        }

        // Check for duplicate appointment (same doctor, same time, same date)
        if (isDuplicateAppointment(appt.getDoctorId(), appt.getAppointmentDate(), appt.getAppointmentTime())) {
            System.err.println("✗ Error: Doctor is not available at this date and time!");
            return false;
        }

        // Generate token number
        int tokenNumber = generateTokenNumber(appt.getAppointmentDate().toString());

        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, " +
                     "appointment_time, department, reason, status, token_number) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setInt(1, appt.getPatientId());
            pst.setInt(2, appt.getDoctorId());
            pst.setDate(3, appt.getAppointmentDate());
            pst.setTime(4, appt.getAppointmentTime());
            pst.setString(5, appt.getDepartment());
            pst.setString(6, appt.getReason());
            pst.setString(7, "Scheduled");
            pst.setInt(8, tokenNumber);

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        int appointmentId = rs.getInt(1);
                        appt.setTokenNumber(tokenNumber);
                        appointmentList.add(appt);
                        System.out.println("✓ Appointment booked successfully with ID: " + appointmentId + 
                                         ", Token: " + tokenNumber);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Database error while booking appointment: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("✗ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ==================== VALIDATION METHODS ====================
    /**
     * Validate appointment input
     * 
     * @param appt Appointment to validate
     * @return true if all required fields are valid
     */
    private boolean validateAppointmentInput(Appointment appt) {
        if (appt == null) {
            System.err.println("Appointment object is null");
            return false;
        }

        if (appt.getPatientId() <= 0) {
            System.err.println("Invalid patient ID");
            return false;
        }
        if (appt.getDoctorId() <= 0) {
            System.err.println("Invalid doctor ID");
            return false;
        }
        if (appt.getAppointmentDate() == null) {
            System.err.println("Appointment date is missing");
            return false;
        }
        if (appt.getAppointmentTime() == null) {
            System.err.println("Appointment time is missing");
            return false;
        }
        if (appt.getDepartment() == null || appt.getDepartment().trim().isEmpty()) {
            System.err.println("Department is missing");
            return false;
        }

        return true;
    }

    /**
     * Check if patient exists in database
     * 
     * SQL Query:
     * SELECT COUNT(*) FROM patients WHERE patient_id = ?
     * 
     * @param patientId Patient ID to verify
     * @return true if patient exists, false otherwise
     */
    public boolean isPatientExists(int patientId) {
        String sql = "SELECT COUNT(*) FROM patients WHERE patient_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, patientId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking patient existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if doctor exists in database
     * 
     * SQL Query:
     * SELECT COUNT(*) FROM doctors WHERE doctor_id = ?
     * 
     * @param doctorId Doctor ID to verify
     * @return true if doctor exists, false otherwise
     */
    public boolean isDoctorExists(int doctorId) {
        String sql = "SELECT COUNT(*) FROM doctors WHERE doctor_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, doctorId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking doctor existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check for duplicate appointment (same doctor at same time)
     * Prevents double-booking of doctors
     * 
     * SQL Query:
     * SELECT COUNT(*) FROM appointments 
     * WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? 
     * AND status IN ('Scheduled', 'Pending')
     * 
     * @param doctorId Doctor ID
     * @param appointmentDate Appointment date
     * @param appointmentTime Appointment time
     * @return true if duplicate exists, false otherwise
     */
    public boolean isDuplicateAppointment(int doctorId, Date appointmentDate, Time appointmentTime) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? " +
                     "AND appointment_date = ? AND appointment_time = ? " +
                     "AND status IN ('Scheduled', 'Pending')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, doctorId);
            pst.setDate(2, appointmentDate);
            pst.setTime(3, appointmentTime);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking duplicate appointment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ==================== RETRIEVE APPOINTMENTS ====================
    /**
     * Get all appointments with patient and doctor details
     * Uses JOIN to combine appointment, patient, and doctor information
     * 
     * SQL Query:
     * SELECT a.*, CONCAT(p.first_name,' ',p.last_name) AS patient_name, 
     *        d.name AS doctor_name FROM appointments a 
     * JOIN patients p ON a.patient_id = p.patient_id 
     * JOIN doctors d ON a.doctor_id = d.doctor_id 
     * ORDER BY a.appointment_date DESC, a.appointment_time ASC
     * 
     * @return ArrayList of all appointments with patient and doctor info
     */
    public ArrayList<Appointment> getAllAppointments() {
        ArrayList<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, CONCAT(p.first_name,' ',p.last_name) AS patient_name, " +
                     "d.name AS doctor_name FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "ORDER BY a.appointment_id ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Appointment appointment = new Appointment(
                    rs.getInt("appointment_id"),
                    rs.getInt("patient_id"),
                    rs.getInt("doctor_id"),
                    rs.getString("patient_name"),
                    rs.getString("doctor_name"),
                    rs.getDate("appointment_date"),
                    rs.getTime("appointment_time"),
                    rs.getString("department"),
                    rs.getString("reason"),
                    rs.getString("status"),
                    rs.getInt("token_number"),
                    rs.getString("notes")
                );
                list.add(appointment);
            }
            System.out.println("✓ Retrieved " + list.size() + " appointments from database");
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get appointments for a specific patient
     * 
     * SQL Query:
     * SELECT a.*, d.name AS doctor_name FROM appointments a 
     * JOIN doctors d ON a.doctor_id = d.doctor_id 
     * WHERE a.patient_id = ? ORDER BY a.appointment_date DESC
     * 
     * @param patientId Patient ID
     * @return ArrayList of patient's appointments
     */
    public ArrayList<Appointment> getPatientAppointments(int patientId) {
        ArrayList<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, CONCAT(p.first_name,' ',p.last_name) AS patient_name, " +
                     "d.name AS doctor_name FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "WHERE a.patient_id = ? ORDER BY a.appointment_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, patientId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Appointment appointment = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getDate("appointment_date"),
                        rs.getTime("appointment_time"),
                        rs.getString("department"),
                        rs.getString("reason"),
                        rs.getString("status"),
                        rs.getInt("token_number"),
                        rs.getString("notes")
                    );
                    list.add(appointment);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving patient appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get today's appointments count
     * 
     * SQL Query:
     * SELECT COUNT(*) FROM appointments WHERE appointment_date = CURDATE()
     * 
     * @return Number of appointments today
     */
    public int getTodayAppointmentsCount() {
        String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_date = CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("✓ Today's appointments: " + count);
                return count;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error counting today's appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Generate unique token number for appointment on a specific date
     * Token number increments for each appointment on the same day
     * 
     * SQL Query:
     * SELECT COUNT(*) FROM appointments WHERE appointment_date = ?
     * 
     * @param date Appointment date
     * @return Next token number
     */
    private int generateTokenNumber(String date) {
        // Generate globally unique token number using MAX(token_number) + 1
        String sql = "SELECT MAX(token_number) FROM appointments";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int maxToken = rs.getInt(1);
                    // If no appointments exist, maxToken will be 0
                    int nextToken = maxToken + 1;
                    System.out.println("✓ Generated unique token number: " + nextToken);
                    return nextToken;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error generating token number: " + e.getMessage());
            e.printStackTrace();
        }
        return 1; // Default to 1 if error or no appointments exist
    }

    /**
     * Get all doctors from database
     * Used to populate doctor dropdown in UI
     * 
     * SQL Query:
     * SELECT doctor_id, name, specialization FROM doctors ORDER BY name
     * 
     * @return ArrayList of doctor information [id, name, specialization]
     */
    public ArrayList<String[]> getAllDoctors() {
        ArrayList<String[]> doctors = new ArrayList<>();
        String sql = "SELECT doctor_id, name, specialization FROM doctors ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String[] doctor = {
                    String.valueOf(rs.getInt("doctor_id")),
                    rs.getString("name"),
                    rs.getString("specialization")
                };
                doctors.add(doctor);
            }
            System.out.println("✓ Retrieved " + doctors.size() + " doctors");
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving doctors: " + e.getMessage());
            e.printStackTrace();
        }
        return doctors;
    }

    /**
     * Update appointment status
     * 
     * SQL Query:
     * UPDATE appointments SET status = ? WHERE appointment_id = ?
     * 
     * @param appointmentId Appointment ID
     * @param newStatus New status (Scheduled, Completed, Cancelled, Pending)
     * @return true if update successful, false otherwise
     */
    public boolean updateAppointmentStatus(int appointmentId, String newStatus) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, newStatus);
            pst.setInt(2, appointmentId);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✓ Appointment status updated to: " + newStatus);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error updating appointment status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get total appointment count
     * 
     * SQL Query:
     * SELECT COUNT(*) FROM appointments
     * 
     * @return Total number of appointments
     */
    public int getTotalAppointmentCount() {
        String sql = "SELECT COUNT(*) FROM appointments";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error counting appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get total number of doctors from database
     * 
     * SQL Query:
     * SELECT COUNT(*) FROM doctors
     * 
     * @return Total number of doctors
     */
    public int getTotalDoctorCount() {
        String sql = "SELECT COUNT(*) FROM doctors";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error counting doctors: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}
