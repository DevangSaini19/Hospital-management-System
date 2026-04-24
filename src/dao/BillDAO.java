package dao;

import db.DBConnection;
import models.Bill;

import java.sql.*;
import java.util.ArrayList;

/**
 * Bill Data Access Object (DAO)
 * Handles all database operations related to bills
 * 
 * Features:
 * - Generate new bill
 * - Get all bills
 * - Get bill by ID
 * - Get bills for a patient
 * - Delete bill
 */
public class BillDAO {

    private ArrayList<Bill> billList = new ArrayList<>();

    // ==================== GENERATE BILL ====================
    /**
     * Generate a new bill
     * 
     * @param bill Bill object with data
     * @return true if bill generated successfully, false otherwise
     */
    public boolean generateBill(Bill bill) {
        // Validate bill input
        if (!validateBillInput(bill)) {
            System.err.println("✗ Invalid bill data provided");
            return false;
        }

        // Calculate total
        bill.calculateTotal();

        String sql = "INSERT INTO bills (patient_id, doctor_id, doctor_fee, " +
                     "medicine_charges, room_charges, other_charges, total_amount) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setInt(1, bill.getPatientId());
            pst.setInt(2, bill.getDoctorId());
            pst.setDouble(3, bill.getDoctorFee());
            pst.setDouble(4, bill.getMedicineCharges());
            pst.setDouble(5, bill.getRoomCharges());
            pst.setDouble(6, bill.getOtherCharges());
            pst.setDouble(7, bill.getTotalAmount());

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        int billId = rs.getInt(1);
                        bill.setBillId(billId);
                        billList.add(bill);
                        System.out.println("✓ Bill generated successfully with ID: " + billId);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Database error while generating bill: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ==================== GET ALL BILLS ====================
    /**
     * Get all bills from database
     * 
     * @return ArrayList of bills
     */
    public ArrayList<Bill> getAllBills() {
        billList.clear();
        String sql = "SELECT b.bill_id, b.patient_id, b.doctor_id, b.appointment_id, " +
                     "CONCAT(p.first_name, ' ', p.last_name) as patient_name, " +
                     "d.name as doctor_name, b.doctor_fee, b.medicine_charges, " +
                     "b.room_charges, b.other_charges, b.total_amount, b.bill_date " +
                     "FROM bills b " +
                     "JOIN patients p ON b.patient_id = p.patient_id " +
                     "JOIN doctors d ON b.doctor_id = d.doctor_id " +
                     "ORDER BY b.bill_date DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Bill bill = new Bill(
                    rs.getInt("bill_id"),
                    rs.getInt("patient_id"),
                    rs.getInt("doctor_id"),
                    rs.getInt("appointment_id"),
                    rs.getString("patient_name"),
                    rs.getString("doctor_name"),
                    rs.getDouble("doctor_fee"),
                    rs.getDouble("medicine_charges"),
                    rs.getDouble("room_charges"),
                    rs.getDouble("other_charges"),
                    rs.getDouble("total_amount"),
                    rs.getTimestamp("bill_date")
                );
                billList.add(bill);
            }
            System.out.println("✓ Retrieved " + billList.size() + " bills");
        } catch (SQLException e) {
            System.err.println("✗ Database error while fetching bills: " + e.getMessage());
            e.printStackTrace();
        }
        return billList;
    }

    // ==================== GET BILL BY ID ====================
    /**
     * Get a specific bill by ID
     * 
     * @param billId Bill ID
     * @return Bill object or null
     */
    public Bill getBillById(int billId) {
        String sql = "SELECT b.bill_id, b.patient_id, b.doctor_id, b.appointment_id, " +
                     "CONCAT(p.first_name, ' ', p.last_name) as patient_name, " +
                     "d.name as doctor_name, b.doctor_fee, b.medicine_charges, " +
                     "b.room_charges, b.other_charges, b.total_amount, b.bill_date " +
                     "FROM bills b " +
                     "JOIN patients p ON b.patient_id = p.patient_id " +
                     "JOIN doctors d ON b.doctor_id = d.doctor_id " +
                     "WHERE b.bill_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, billId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Bill(
                        rs.getInt("bill_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getInt("appointment_id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getDouble("doctor_fee"),
                        rs.getDouble("medicine_charges"),
                        rs.getDouble("room_charges"),
                        rs.getDouble("other_charges"),
                        rs.getDouble("total_amount"),
                        rs.getTimestamp("bill_date")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error fetching bill: " + e.getMessage());
        }
        return null;
    }

    // ==================== GET BILLS FOR PATIENT ====================
    /**
     * Get all bills for a specific patient
     * 
     * @param patientId Patient ID
     * @return ArrayList of bills
     */
    public ArrayList<Bill> getBillsForPatient(int patientId) {
        ArrayList<Bill> patientBills = new ArrayList<>();
        String sql = "SELECT b.bill_id, b.patient_id, b.doctor_id, b.appointment_id, " +
                     "CONCAT(p.first_name, ' ', p.last_name) as patient_name, " +
                     "d.name as doctor_name, b.doctor_fee, b.medicine_charges, " +
                     "b.room_charges, b.other_charges, b.total_amount, b.bill_date " +
                     "FROM bills b " +
                     "JOIN patients p ON b.patient_id = p.patient_id " +
                     "JOIN doctors d ON b.doctor_id = d.doctor_id " +
                     "WHERE b.patient_id = ? " +
                     "ORDER BY b.bill_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, patientId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Bill bill = new Bill(
                        rs.getInt("bill_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getInt("appointment_id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getDouble("doctor_fee"),
                        rs.getDouble("medicine_charges"),
                        rs.getDouble("room_charges"),
                        rs.getDouble("other_charges"),
                        rs.getDouble("total_amount"),
                        rs.getTimestamp("bill_date")
                    );
                    patientBills.add(bill);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error fetching patient bills: " + e.getMessage());
        }
        return patientBills;
    }

    // ==================== DELETE BILL ====================
    /**
     * Delete a bill
     * 
     * @param billId Bill ID to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteBill(int billId) {
        String sql = "DELETE FROM bills WHERE bill_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, billId);
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                billList.removeIf(b -> b.getBillId() == billId);
                System.out.println("✓ Bill deleted successfully");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Database error while deleting bill: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ==================== HELPER METHODS ====================
    /**
     * Validate bill input
     */
    private boolean validateBillInput(Bill bill) {
        if (bill == null) return false;
        if (bill.getPatientId() <= 0) return false;
        if (bill.getDoctorId() <= 0) return false;
        if (bill.getDoctorFee() < 0 || bill.getMedicineCharges() < 0 ||
            bill.getRoomCharges() < 0 || bill.getOtherCharges() < 0) {
            return false;
        }
        return true;
    }

    /**
     * Get total bills count
     */
    public int getTotalBillsCount() {
        String sql = "SELECT COUNT(*) FROM bills";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error fetching bills count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get total revenue from all bills
     */
    public double getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) FROM bills";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error calculating revenue: " + e.getMessage());
        }
        return 0.0;
    }
}
