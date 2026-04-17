package dao;

import db.DBConnection;
import models.Patient;
import utils.ValidationUtil;

import java.sql.*;
import java.util.LinkedList;

/**
 * Patient Data Access Object (DAO)
 * Handles all database operations related to patients
 * 
 * Features:
 * - Add new patient with validation
 * - Unique phone number validation
 * - Search patients by name/phone
 * - Get all patients
 * - Update patient information
 * - Proper exception handling
 */
public class PatientDAO {

    // Data structure: LinkedList to hold patients in memory (queue simulation)
    private LinkedList<Patient> patientQueue = new LinkedList<>();

    // ==================== ADD PATIENT ====================
    /**
     * Add a new patient to the database
     * Validates unique phone number before insertion
     * 
     * SQL Query:
     * INSERT INTO patients (first_name, last_name, age, gender, blood_group, 
     *                       phone, email, address, medical_history, status) 
     * VALUES (?,?,?,?,?,?,?,?,?,?)
     * 
     * @param patient Patient object with data
     * @return true if patient added successfully, false otherwise
     */
    public boolean addPatient(Patient patient) {
        // Input validation
        if (!validatePatientInput(patient)) {
            System.err.println("Invalid patient data provided");
            return false;
        }

        // Check for duplicate phone number
        if (isPhoneExists(patient.getPhone())) {
            System.err.println("✗ Error: Phone number already registered!");
            return false;
        }

        String sql = "INSERT INTO patients (first_name, last_name, age, gender, blood_group, " +
                     "phone, email, address, medical_history, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Set parameters safely using PreparedStatement
            pst.setString(1, patient.getFirstName());
            pst.setString(2, patient.getLastName());
            pst.setInt(3, patient.getAge());
            pst.setString(4, patient.getGender());
            pst.setString(5, patient.getBloodGroup());
            pst.setString(6, patient.getPhone());
            pst.setString(7, patient.getEmail());
            pst.setString(8, patient.getAddress());
            pst.setString(9, patient.getMedicalHistory());
            pst.setString(10, patient.getStatus());

            // Execute update
            int rowsAffected = pst.executeUpdate();

            // Get auto-generated patient ID
            if (rowsAffected > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        int patientId = rs.getInt(1);
                        patient.setPatientId(patientId);
                        patientQueue.addLast(patient); // Add to queue
                        System.out.println("✓ Patient added successfully with ID: " + patientId);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry error
                System.err.println("✗ Error: Phone number already exists in the database!");
            } else {
                System.err.println("✗ Database error while adding patient: " + e.getMessage());
            }
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("✗ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ==================== VALIDATION METHODS ====================
    /**
     * Validate patient input before database operations
     * 
     * @param patient Patient to validate
     * @return true if all required fields are valid
     */
    private boolean validatePatientInput(Patient patient) {
        if (patient == null) {
            System.err.println("Patient object is null");
            return false;
        }

        String firstName = ValidationUtil.sanitizeString(patient.getFirstName());
        String lastName = ValidationUtil.sanitizeString(patient.getLastName());
        String phone = ValidationUtil.sanitizeString(patient.getPhone());
        String gender = patient.getGender();
        String bloodGroup = patient.getBloodGroup();

        // Validate required fields
        if (!ValidationUtil.isValidName(firstName)) {
            System.err.println("Invalid first name");
            return false;
        }
        if (!ValidationUtil.isValidName(lastName)) {
            System.err.println("Invalid last name");
            return false;
        }
        if (!ValidationUtil.isValidAge(patient.getAge())) {
            System.err.println("Invalid age (must be between 1 and 149)");
            return false;
        }
        if (!ValidationUtil.isValidGender(gender)) {
            System.err.println("Invalid gender");
            return false;
        }
        if (!ValidationUtil.isValidBloodGroup(bloodGroup)) {
            System.err.println("Invalid blood group");
            return false;
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            System.err.println("Invalid phone number (must be 10 digits)");
            return false;
        }

        // Validate optional email if provided
        if (patient.getEmail() != null && !patient.getEmail().isEmpty()) {
            if (!ValidationUtil.isValidEmail(patient.getEmail())) {
                System.err.println("Invalid email format");
                return false;
            }
        }

        return true;
    }

    /**
     * Check if phone number already exists in database
     * 
     * SQL Query:
     * SELECT COUNT(*) FROM patients WHERE phone = ?
     * 
     * @param phone Phone number to check
     * @return true if phone exists, false otherwise
     */
    public boolean isPhoneExists(String phone) {
        String sql = "SELECT COUNT(*) FROM patients WHERE phone = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, phone);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking phone existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ==================== RETRIEVE PATIENTS ====================
    /**
     * Get all patients from database
     * Returns LinkedList (queue structure)
     * 
     * SQL Query:
     * SELECT * FROM patients ORDER BY registration_date DESC
     * 
     * @return LinkedList of all patients
     */
    public LinkedList<Patient> getAllPatients() {
        LinkedList<Patient> list = new LinkedList<>();
        String sql = "SELECT * FROM patients ORDER BY patient_id ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Patient patient = new Patient(
                    rs.getInt("patient_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getInt("age"),
                    rs.getString("gender"),
                    rs.getString("blood_group"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getString("medical_history"),
                    rs.getDate("registration_date"),
                    rs.getString("status")
                );
                list.add(patient);
            }
            System.out.println("✓ Retrieved " + list.size() + " patients from database");
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving patients: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Search patients by name or phone
     * 
     * SQL Query:
     * SELECT * FROM patients WHERE first_name LIKE ? OR last_name LIKE ? OR phone LIKE ?
     * 
     * @param keyword Search keyword
     * @return LinkedList of matching patients
     */
    public LinkedList<Patient> searchPatients(String keyword) {
        LinkedList<Patient> list = new LinkedList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            System.err.println("Search keyword cannot be empty");
            return list;
        }

        String sql = "SELECT * FROM patients WHERE first_name LIKE ? OR last_name LIKE ? OR phone LIKE ? ORDER BY first_name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);
            pst.setString(3, searchPattern);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Patient patient = new Patient(
                        rs.getInt("patient_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("blood_group"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("medical_history"),
                        rs.getDate("registration_date"),
                        rs.getString("status")
                    );
                    list.add(patient);
                }
            }
            System.out.println("✓ Found " + list.size() + " patients matching: " + keyword);
        } catch (SQLException e) {
            System.err.println("✗ Error searching patients: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get patient by ID
     * 
     * SQL Query:
     * SELECT * FROM patients WHERE patient_id = ?
     * 
     * @param patientId Patient ID
     * @return Patient object if found, null otherwise
     */
    public Patient getPatientById(int patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, patientId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Patient(
                        rs.getInt("patient_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("blood_group"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("medical_history"),
                        rs.getDate("registration_date"),
                        rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving patient: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update patient information
     * 
     * SQL Query:
     * UPDATE patients SET first_name=?, last_name=?, age=?, gender=?, blood_group=?,
     *                    email=?, address=?, medical_history=?, status=?
     * WHERE patient_id=?
     * 
     * @param patient Patient object with updated data
     * @return true if update successful, false otherwise
     */
    public boolean updatePatient(Patient patient) {
        if (patient == null || patient.getPatientId() <= 0) {
            System.err.println("Invalid patient ID");
            return false;
        }

        String sql = "UPDATE patients SET first_name=?, last_name=?, age=?, gender=?, blood_group=?, " +
                     "email=?, address=?, medical_history=?, status=? WHERE patient_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, patient.getFirstName());
            pst.setString(2, patient.getLastName());
            pst.setInt(3, patient.getAge());
            pst.setString(4, patient.getGender());
            pst.setString(5, patient.getBloodGroup());
            pst.setString(6, patient.getEmail());
            pst.setString(7, patient.getAddress());
            pst.setString(8, patient.getMedicalHistory());
            pst.setString(9, patient.getStatus());
            pst.setInt(10, patient.getPatientId());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✓ Patient updated successfully");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error updating patient: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get patient count
     * 
     * SQL Query:
     * SELECT COUNT(*) FROM patients
     * 
     * @return Total number of patients
     */
    public int getTotalPatientCount() {
        String sql = "SELECT COUNT(*) FROM patients";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error counting patients: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // ==================== QUEUE OPERATIONS ====================
    /**
     * Peek next patient in queue (Data Structure)
     * @return Next patient without removing
     */
    public Patient peekNextInQueue() {
        return patientQueue.peekFirst();
    }

    /**
     * Serve next patient (dequeue from LinkedList)
     * @return Next patient to be served
     */
    public Patient serveNextPatient() {
        return patientQueue.pollFirst();
    }
}
