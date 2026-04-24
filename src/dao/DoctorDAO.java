package dao;

import db.DBConnection;
import models.Doctor;
import utils.ValidationUtil;

import java.sql.*;
import java.util.ArrayList;

/**
 * Doctor Data Access Object (DAO)
 * Handles all database operations related to doctors
 * 
 * Features:
 * - Add new doctor
 * - Get all doctors
 * - Delete doctor
 * - Update doctor information
 * - Proper exception handling
 */
public class DoctorDAO {

    private ArrayList<Doctor> doctorList = new ArrayList<>();

    // ==================== ADD DOCTOR ====================
    /**
     * Add a new doctor to the database
     * 
     * @param doctor Doctor object with data
     * @return true if doctor added successfully, false otherwise
     */
    public boolean addDoctor(Doctor doctor) {
        // Input validation
        if (!validateDoctorInput(doctor)) {
            System.err.println("✗ Invalid doctor data provided. Please check all fields.");
            return false;
        }

        // Check for duplicate phone
        if (isPhoneExists(doctor.getPhone())) {
            System.err.println("✗ Error: Phone number already registered!");
            return false;
        }

        String sql = "INSERT INTO doctors (name, specialization, phone, consultation_fee) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, doctor.getName());
            pst.setString(2, doctor.getSpecialization());
            pst.setString(3, doctor.getPhone());
            pst.setDouble(4, doctor.getConsultationFee());

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        int doctorId = rs.getInt(1);
                        doctor.setDoctorId(doctorId);
                        doctorList.add(doctor);
                        System.out.println("✓ Doctor added successfully with ID: " + doctorId);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("✗ Error: Phone number already exists!");
            } else {
                System.err.println("✗ Database error while adding doctor: " + e.getMessage());
            }
            e.printStackTrace();
        }
        return false;
    }

    // ==================== GET ALL DOCTORS ====================
    /**
     * Get all doctors from database
     * 
     * @return ArrayList of doctors
     */
    public ArrayList<Doctor> getAllDoctors() {
        doctorList.clear();
        String sql = "SELECT * FROM doctors ORDER BY doctor_id ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Doctor doctor = new Doctor(
                    rs.getInt("doctor_id"),
                    rs.getString("name"),
                    rs.getString("specialization"),
                    rs.getString("phone"),
                    rs.getDouble("consultation_fee"),
                    rs.getTimestamp("created_at")
                );
                doctorList.add(doctor);
            }
            System.out.println("✓ Retrieved " + doctorList.size() + " doctors");
        } catch (SQLException e) {
            System.err.println("✗ Database error while fetching doctors: " + e.getMessage());
            e.printStackTrace();
        }
        return doctorList;
    }

    // ==================== DELETE DOCTOR ====================
    /**
     * Delete a doctor from database
     * 
     * @param doctorId Doctor ID to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteDoctor(int doctorId) {
        String sql = "DELETE FROM doctors WHERE doctor_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, doctorId);
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                doctorList.removeIf(d -> d.getDoctorId() == doctorId);
                System.out.println("✓ Doctor deleted successfully");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Database error while deleting doctor: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ==================== UPDATE DOCTOR ====================
    /**
     * Update doctor information
     * 
     * @param doctor Doctor object with updated data
     * @return true if updated successfully, false otherwise
     */
    public boolean updateDoctor(Doctor doctor) {
        if (!validateDoctorInput(doctor)) {
            System.err.println("✗ Invalid doctor data provided");
            return false;
        }

        String sql = "UPDATE doctors SET name = ?, specialization = ?, phone = ?, consultation_fee = ? " +
                     "WHERE doctor_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, doctor.getName());
            pst.setString(2, doctor.getSpecialization());
            pst.setString(3, doctor.getPhone());
            pst.setDouble(4, doctor.getConsultationFee());
            pst.setInt(5, doctor.getDoctorId());

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✓ Doctor updated successfully");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Database error while updating doctor: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ==================== HELPER METHODS ====================
    /**
     * Check if phone number already exists
     */
    private boolean isPhoneExists(String phone) {
        String sql = "SELECT COUNT(*) FROM doctors WHERE phone = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, phone);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error checking phone existence: " + e.getMessage());
        }
        return false;
    }

    /**
     * Validate doctor input
     */
    private boolean validateDoctorInput(Doctor doctor) {
        if (doctor == null) return false;
        if (doctor.getName() == null || doctor.getName().trim().isEmpty()) return false;
        if (doctor.getSpecialization() == null || doctor.getSpecialization().trim().isEmpty()) return false;
        if (doctor.getPhone() == null || !ValidationUtil.isValidPhone(doctor.getPhone())) return false;
        if (doctor.getConsultationFee() < 0) return false;
        return true;
    }

    /**
     * Get total doctor count
     */
    public int getTotalDoctorCount() {
        String sql = "SELECT COUNT(*) FROM doctors";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error fetching doctor count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get doctor by ID
     */
    public Doctor getDoctorById(int doctorId) {
        String sql = "SELECT * FROM doctors WHERE doctor_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, doctorId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Doctor(
                        rs.getInt("doctor_id"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getString("phone"),
                        rs.getDouble("consultation_fee"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error fetching doctor: " + e.getMessage());
        }
        return null;
    }
}
