# Billing Module - Comprehensive Documentation
## Hospital Management System

---

## Table of Contents
1. [Overview](#overview)
2. [Data Model](#data-model)
3. [Database Schema & Queries](#database-schema--queries)
4. [DAO (Data Access Object) Functionality](#dao-data-access-object-functionality)
5. [Java Swing UI Components](#java-swing-ui-components)
6. [Features & Workflow](#features--workflow)
7. [Billing Calculations](#billing-calculations)
8. [Error Handling & Validation](#error-handling--validation)
9. [Code Examples](#code-examples)

---

## OVERVIEW

The Billing Module is the financial management component of the Hospital Management System. It handles the complete lifecycle of patient bills, from generation through tracking, with automatic calculation of charges from multiple components.

### Key Features
- ✅ Generate bills for patients
- ✅ Calculate total charges automatically
- ✅ View all bills with patient/doctor details
- ✅ Search bills by patient or doctor
- ✅ Charge breakdown visualization
- ✅ Bill history and tracking
- ✅ Real-time database synchronization
- ✅ Input validation and error handling

### Architecture Overview
```
User Interface (BillingForm.java - JPanel)
        ↓
Business Logic (BillDAO.java)
        ↓
Models (Doctor, Patient, Appointment, Bill)
        ↓
Database (MySQL - bills, doctors, patients, appointments tables)
```

### Bill Components

```
TOTAL BILL AMOUNT
│
├─ Doctor Fee (₹)              [From doctor's consultation_fee]
├─ Medicine Charges (₹)        [Prescription medicines]
├─ Room Charges (₹)            [Hospital room/bed rent]
└─ Other Charges (₹)           [Lab tests, equipment, etc.]

Total = Doctor Fee + Medicine + Room + Other
```

---

## DATA MODEL

### Bill Class (POJO)

#### Location: `src/models/Bill.java`

```java
package models;
import java.sql.Timestamp;

public class Bill {
    
    // ========== ATTRIBUTES ==========
    private int billId;              // Unique bill ID (Auto-generated)
    private int patientId;           // Reference to patient
    private int doctorId;            // Reference to doctor
    private int appointmentId;       // Reference to appointment
    private String patientName;      // Patient full name (denormalized)
    private String doctorName;       // Doctor full name (denormalized)
    private double doctorFee;        // Doctor consultation fee (₹)
    private double medicineCharges;  // Medicine/drug charges (₹)
    private double roomCharges;      // Hospital room charges (₹)
    private double otherCharges;     // Miscellaneous charges (₹)
    private double totalAmount;      // Total bill (auto-calculated)
    private Timestamp billDate;      // Bill generation timestamp
    
    // ========== CONSTRUCTORS ==========
    
    // Constructor for new bill (without ID)
    public Bill(int patientId, int doctorId, double doctorFee,
                double medicineCharges, double roomCharges, double otherCharges) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.doctorFee = doctorFee;
        this.medicineCharges = medicineCharges;
        this.roomCharges = roomCharges;
        this.otherCharges = otherCharges;
        this.totalAmount = doctorFee + medicineCharges + roomCharges + otherCharges;
    }
    
    // Full constructor (with all fields including ID)
    public Bill(int billId, int patientId, int doctorId, int appointmentId, 
                String patientName, String doctorName, double doctorFee,
                double medicineCharges, double roomCharges, double otherCharges,
                double totalAmount, Timestamp billDate) {
        this.billId = billId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.doctorFee = doctorFee;
        this.medicineCharges = medicineCharges;
        this.roomCharges = roomCharges;
        this.otherCharges = otherCharges;
        this.totalAmount = totalAmount;
        this.billDate = billDate;
    }
    
    // ========== KEY METHODS ==========
    
    /**
     * Calculate total amount from component charges
     */
    public void calculateTotal() {
        this.totalAmount = doctorFee + medicineCharges + roomCharges + otherCharges;
    }
    
    // ========== GETTERS & SETTERS ==========
    // All getters and setters for attributes
}
```

### Attribute Specifications

| Attribute | Type | Constraints | Description | Example |
|-----------|------|-------------|-------------|---------|
| billId | int | PK, AUTO_INCREMENT | Unique bill identifier | 1, 2, 3... |
| patientId | int | FK, NOT NULL | Reference to patient | 1 |
| doctorId | int | FK, NOT NULL | Reference to doctor | 1 |
| appointmentId | int | FK, NULLABLE | Reference to appointment | 1 |
| patientName | String | - | Patient full name | Ravi Sharma |
| doctorName | String | - | Doctor full name | Dr. Ramesh Sharma |
| doctorFee | double | NOT NULL, DEFAULT 0 | Consultation fee (₹) | 500.0 |
| medicineCharges | double | NOT NULL, DEFAULT 0 | Medicine costs (₹) | 350.0 |
| roomCharges | double | NOT NULL, DEFAULT 0 | Room rent (₹) | 1500.0 |
| otherCharges | double | NOT NULL, DEFAULT 0 | Miscellaneous (₹) | 150.0 |
| totalAmount | double | NOT NULL, DEFAULT 0 | Total bill (₹) | 2500.0 |
| billDate | Timestamp | DEFAULT CURRENT_TIMESTAMP | Bill generation time | 2024-04-27 10:30:45 |

### Data Validation Rules

```
Patient ID:
  - Must be > 0
  - Must exist in patients table
  - Foreign key constraint

Doctor ID:
  - Must be > 0
  - Must exist in doctors table
  - Foreign key constraint

Doctor Fee:
  - Non-negative (≥ 0)
  - Usually sourced from doctor's consultation_fee
  - Range: ₹0 - ₹10000

Medicine Charges:
  - Non-negative (≥ 0)
  - Typical range: ₹0 - ₹5000
  - Based on prescribed medicines

Room Charges:
  - Non-negative (≥ 0)
  - Per day basis
  - Range: ₹0 - ₹5000/day

Other Charges:
  - Non-negative (≥ 0)
  - Lab tests, equipment usage, etc.
  - Range: ₹0 - ₹2000

Total Amount:
  - Calculated automatically
  - Sum of all charges
  - Formula: doctorFee + medicine + room + other
```

---

## DATABASE SCHEMA & QUERIES

### Table Structure

#### MySQL Table: `bills`

```sql
CREATE TABLE IF NOT EXISTS bills (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_id INT,
    doctor_fee DOUBLE NOT NULL DEFAULT 0,
    medicine_charges DOUBLE NOT NULL DEFAULT 0,
    room_charges DOUBLE NOT NULL DEFAULT 0,
    other_charges DOUBLE NOT NULL DEFAULT 0,
    total_amount DOUBLE NOT NULL DEFAULT 0,
    bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE,
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id) ON DELETE SET NULL,
    INDEX idx_patient_id (patient_id),
    INDEX idx_doctor_id (doctor_id),
    INDEX idx_bill_date (bill_date)
);
```

### Database Indexes

```sql
-- Primary Key Index (Auto-created)
ALTER TABLE bills ADD PRIMARY KEY (bill_id);

-- Foreign Key Indexes (Recommended)
ALTER TABLE bills ADD INDEX idx_patient_id (patient_id);
ALTER TABLE bills ADD INDEX idx_doctor_id (doctor_id);

-- Query Performance Indexes
ALTER TABLE bills ADD INDEX idx_bill_date (bill_date);
ALTER TABLE bills ADD INDEX idx_total_amount (total_amount);

-- Composite Indexes (Advanced)
ALTER TABLE bills ADD INDEX idx_patient_date (patient_id, bill_date);
ALTER TABLE bills ADD INDEX idx_doctor_date (doctor_id, bill_date);
```

### Related Tables

#### Dependencies for Bills

```
bills table
├─ Requires: patients table (patient_id → patient_id)
├─ Requires: doctors table (doctor_id → doctor_id)
└─ Requires: appointments table (appointment_id → appointment_id, optional)
```

### SQL Query Examples

#### 1. INSERT - Generate New Bill

```sql
-- Basic bill generation
INSERT INTO bills (patient_id, doctor_id, doctor_fee, 
                   medicine_charges, room_charges, other_charges, total_amount) 
VALUES (1, 1, 500.0, 350.0, 1500.0, 150.0, 2500.0);

-- Bill with appointment reference
INSERT INTO bills (patient_id, doctor_id, appointment_id, doctor_fee, 
                   medicine_charges, room_charges, other_charges, total_amount) 
VALUES (1, 1, 1, 500.0, 300.0, 1200.0, 100.0, 2100.0);

-- Multiple bills at once
INSERT INTO bills (patient_id, doctor_id, doctor_fee, 
                   medicine_charges, room_charges, other_charges, total_amount) 
VALUES 
(1, 1, 500.0, 350.0, 1500.0, 150.0, 2500.0),
(2, 1, 500.0, 200.0, 1200.0, 100.0, 2000.0),
(3, 2, 600.0, 400.0, 1800.0, 200.0, 3000.0);
```

#### 2. SELECT - Retrieve Bills

```sql
-- Get all bills with patient and doctor details
SELECT b.bill_id, b.patient_id, b.doctor_id, b.appointment_id, 
       CONCAT(p.first_name, ' ', p.last_name) as patient_name, 
       d.name as doctor_name, 
       b.doctor_fee, b.medicine_charges, b.room_charges, 
       b.other_charges, b.total_amount, b.bill_date 
FROM bills b 
JOIN patients p ON b.patient_id = p.patient_id 
JOIN doctors d ON b.doctor_id = d.doctor_id 
ORDER BY b.bill_date DESC;

-- Get specific bill by ID
SELECT b.*, 
       CONCAT(p.first_name, ' ', p.last_name) as patient_name, 
       d.name as doctor_name 
FROM bills b 
JOIN patients p ON b.patient_id = p.patient_id 
JOIN doctors d ON b.doctor_id = d.doctor_id 
WHERE b.bill_id = 1;

-- Get all bills for a patient
SELECT b.* 
FROM bills b 
WHERE b.patient_id = 1 
ORDER BY b.bill_date DESC;

-- Get all bills for a doctor
SELECT b.* 
FROM bills b 
WHERE b.doctor_id = 1 
ORDER BY b.bill_date DESC;

-- Get bills in date range
SELECT * FROM bills 
WHERE DATE(bill_date) BETWEEN '2024-01-01' AND '2024-12-31' 
ORDER BY bill_date DESC;

-- Get high-value bills (> ₹5000)
SELECT * FROM bills 
WHERE total_amount > 5000 
ORDER BY total_amount DESC;

-- Get bills sorted by total amount
SELECT * FROM bills 
ORDER BY total_amount DESC;

-- Get recent bills (last 10)
SELECT * FROM bills 
ORDER BY bill_date DESC 
LIMIT 10;

-- Search bills by patient name
SELECT b.* 
FROM bills b 
JOIN patients p ON b.patient_id = p.patient_id 
WHERE CONCAT(p.first_name, ' ', p.last_name) LIKE '%Sharma%'
ORDER BY b.bill_date DESC;
```

#### 3. UPDATE - Modify Bill Information

```sql
-- Update charges (use with caution)
UPDATE bills 
SET medicine_charges = 400.0, other_charges = 200.0 
WHERE bill_id = 1;

-- Update total amount (if re-calculated)
UPDATE bills 
SET total_amount = doctor_fee + medicine_charges + room_charges + other_charges 
WHERE bill_id = 1;

-- Recalculate all bill totals
UPDATE bills 
SET total_amount = doctor_fee + medicine_charges + room_charges + other_charges;
```

#### 4. DELETE - Remove Bill

```sql
-- Delete specific bill
DELETE FROM bills WHERE bill_id = 1;

-- Delete bills for a patient (dangerous)
DELETE FROM bills WHERE patient_id = 1;

-- Delete bills before a date (for archiving)
DELETE FROM bills WHERE bill_date < '2023-01-01';
```

#### 5. AGGREGATE Queries - Analytics

```sql
-- Total bills generated
SELECT COUNT(*) as total_bills FROM bills;

-- Total revenue from all bills
SELECT SUM(total_amount) as total_revenue FROM bills;

-- Revenue by doctor
SELECT b.doctor_id, d.name, COUNT(b.bill_id) as bill_count, 
       SUM(b.total_amount) as total_revenue,
       AVG(b.total_amount) as average_bill
FROM bills b 
JOIN doctors d ON b.doctor_id = d.doctor_id 
GROUP BY b.doctor_id, d.name
ORDER BY total_revenue DESC;

-- Revenue by patient
SELECT b.patient_id, CONCAT(p.first_name, ' ', p.last_name) as patient_name,
       COUNT(b.bill_id) as bill_count,
       SUM(b.total_amount) as total_spent
FROM bills b 
JOIN patients p ON b.patient_id = p.patient_id 
GROUP BY b.patient_id
ORDER BY total_spent DESC;

-- Charge component analysis
SELECT 
    COUNT(*) as total_bills,
    SUM(doctor_fee) as total_doctor_fees,
    SUM(medicine_charges) as total_medicine,
    SUM(room_charges) as total_room,
    SUM(other_charges) as total_other,
    SUM(total_amount) as grand_total,
    AVG(doctor_fee) as avg_doctor_fee,
    AVG(medicine_charges) as avg_medicine,
    AVG(room_charges) as avg_room,
    AVG(other_charges) as avg_other
FROM bills;

-- Monthly revenue report
SELECT DATE_FORMAT(bill_date, '%Y-%m') as month,
       COUNT(*) as bill_count,
       SUM(total_amount) as monthly_revenue,
       AVG(total_amount) as avg_bill_amount
FROM bills 
GROUP BY DATE_FORMAT(bill_date, '%Y-%m')
ORDER BY month DESC;

-- Doctor fee vs other charges analysis
SELECT 
    SUM(doctor_fee) as doctor_revenue,
    SUM(medicine_charges + room_charges + other_charges) as non_doctor_charges,
    SUM(total_amount) as total_revenue,
    ROUND((SUM(doctor_fee) / SUM(total_amount)) * 100, 2) as doctor_fee_percentage
FROM bills;
```

#### 6. Complex Analytical Queries

```sql
-- Patient's total medical expenses
SELECT p.patient_id,
       CONCAT(p.first_name, ' ', p.last_name) as patient_name,
       COUNT(b.bill_id) as total_bills,
       SUM(b.total_amount) as total_expenses,
       AVG(b.total_amount) as average_bill_amount,
       MAX(b.total_amount) as highest_bill,
       MIN(b.total_amount) as lowest_bill
FROM patients p 
LEFT JOIN bills b ON p.patient_id = b.patient_id 
GROUP BY p.patient_id, p.first_name, p.last_name
ORDER BY total_expenses DESC;

-- Doctor's billing statistics
SELECT d.doctor_id, d.name, d.specialization,
       COUNT(b.bill_id) as total_bills,
       SUM(b.doctor_fee) as total_doctor_fees,
       SUM(b.total_amount) as total_bill_value,
       AVG(b.total_amount) as average_bill_value,
       MAX(b.total_amount) as highest_bill
FROM doctors d 
LEFT JOIN bills b ON d.doctor_id = b.doctor_id 
GROUP BY d.doctor_id, d.name, d.specialization
ORDER BY total_bill_value DESC;

-- Medicine charges analysis
SELECT 
    DATE_FORMAT(bill_date, '%Y-%m') as month,
    COUNT(*) as bill_count,
    SUM(medicine_charges) as total_medicine_charges,
    AVG(medicine_charges) as avg_medicine_charge,
    MAX(medicine_charges) as highest_medicine,
    MIN(medicine_charges) as lowest_medicine
FROM bills 
GROUP BY DATE_FORMAT(bill_date, '%Y-%m')
ORDER BY month DESC;

-- Bills with breakdown percentage
SELECT b.bill_id, 
       CONCAT(p.first_name, ' ', p.last_name) as patient_name, 
       d.name as doctor_name, 
       b.doctor_fee, 
       b.medicine_charges,
       b.room_charges,
       b.other_charges,
       b.total_amount,
       ROUND((b.doctor_fee / b.total_amount) * 100, 2) as doctor_fee_pct,
       ROUND((b.medicine_charges / b.total_amount) * 100, 2) as medicine_pct,
       ROUND((b.room_charges / b.total_amount) * 100, 2) as room_pct,
       ROUND((b.other_charges / b.total_amount) * 100, 2) as other_pct
FROM bills b 
JOIN patients p ON b.patient_id = p.patient_id 
JOIN doctors d ON b.doctor_id = d.doctor_id 
ORDER BY b.bill_date DESC;
```

---

## DAO (DATA ACCESS OBJECT) FUNCTIONALITY

### Location: `src/dao/BillDAO.java`

The BillDAO class provides all database operations for the Bill entity. It manages the complete bill lifecycle with proper error handling and validation.

### Class Overview

```java
package dao;

import db.DBConnection;
import models.Bill;
import java.sql.*;
import java.util.ArrayList;

public class BillDAO {
    private ArrayList<Bill> billList = new ArrayList<>();
    // Methods...
}
```

### DAO Methods

#### 1. GENERATE BILL

```java
/**
 * Generate a new bill
 * @param bill Bill object with data
 * @return true if bill generated successfully, false otherwise
 */
public boolean generateBill(Bill bill) {
    // Step 1: Validate bill input
    if (!validateBillInput(bill)) {
        System.err.println("✗ Invalid bill data provided");
        return false;
    }

    // Step 2: Calculate total amount
    bill.calculateTotal();

    // Step 3: Prepare SQL statement
    String sql = "INSERT INTO bills (patient_id, doctor_id, doctor_fee, " +
                 "medicine_charges, room_charges, other_charges, total_amount) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pst = conn.prepareStatement(sql, 
                                 Statement.RETURN_GENERATED_KEYS)) {

        // Step 4: Set parameters
        pst.setInt(1, bill.getPatientId());
        pst.setInt(2, bill.getDoctorId());
        pst.setDouble(3, bill.getDoctorFee());
        pst.setDouble(4, bill.getMedicineCharges());
        pst.setDouble(5, bill.getRoomCharges());
        pst.setDouble(6, bill.getOtherCharges());
        pst.setDouble(7, bill.getTotalAmount());

        // Step 5: Execute insert
        int rowsAffected = pst.executeUpdate();

        if (rowsAffected > 0) {
            // Step 6: Get generated ID
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

// Usage Example:
Bill bill = new Bill(1, 1, 500.0, 350.0, 1500.0, 150.0);
boolean success = billDAO.generateBill(bill);
if (success) {
    System.out.println("Bill ID: " + bill.getBillId() + ", Total: ₹" + bill.getTotalAmount());
}
```

#### 2. GET ALL BILLS

```java
/**
 * Get all bills from database
 * @return ArrayList of all bills with full details
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

// Usage Example:
ArrayList<Bill> allBills = billDAO.getAllBills();
System.out.println("Total bills: " + allBills.size());
```

#### 3. GET BILL BY ID

```java
/**
 * Get a specific bill by ID
 * @param billId Bill ID
 * @return Bill object or null if not found
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

// Usage Example:
Bill bill = billDAO.getBillById(1);
if (bill != null) {
    System.out.println("Patient: " + bill.getPatientName() + ", Total: ₹" + bill.getTotalAmount());
}
```

#### 4. GET BILLS FOR PATIENT

```java
/**
 * Get all bills for a specific patient
 * @param patientId Patient ID
 * @return ArrayList of patient's bills
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

// Usage Example:
ArrayList<Bill> patientBills = billDAO.getBillsForPatient(1);
double totalSpent = patientBills.stream().mapToDouble(Bill::getTotalAmount).sum();
System.out.println("Patient total spent: ₹" + totalSpent);
```

#### 5. DELETE BILL

```java
/**
 * Delete a bill
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

// Usage Example:
boolean deleted = billDAO.deleteBill(1);
if (deleted) {
    System.out.println("Bill removed from system");
}
```

#### 6. HELPER METHODS

```java
/**
 * Validate bill input
 */
private boolean validateBillInput(Bill bill) {
    if (bill == null) return false;
    if (bill.getPatientId() <= 0) return false;
    if (bill.getDoctorId() <= 0) return false;
    if (bill.getDoctorFee() < 0) return false;
    if (bill.getMedicineCharges() < 0) return false;
    if (bill.getRoomCharges() < 0) return false;
    if (bill.getOtherCharges() < 0) return false;
    return true;
}

/**
 * Get total bill count
 */
public int getTotalBillCount() {
    String sql = "SELECT COUNT(*) FROM bills";
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        if (rs.next()) {
            return rs.getInt(1);
        }
    } catch (SQLException e) {
        System.err.println("✗ Error fetching bill count: " + e.getMessage());
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
        System.err.println("✗ Error fetching total revenue: " + e.getMessage());
    }
    return 0.0;
}
```

---

## JAVA SWING UI COMPONENTS

### Location: `src/ui/BillingForm.java`

The BillingForm is a custom JPanel that provides the user interface for bill generation and management.

### UI Structure

```
┌──────────────────────────────────────────────────┐
│  💳 Billing System                               │
├──────────────────────────────────────────────────┤
│  ┌────────────────────────────────────────────┐ │
│  │ Generate New Bill (Top Panel)              │ │
│  │ Patient: [Dropdown]  Doctor: [Dropdown]    │ │
│  │ Doctor Fee: [Field]  Medicine: [Field]     │ │
│  │ Room Charges: [Field] Other: [Field]       │ │
│  │ Total: [Field - Read Only]                 │ │
│  │ [Generate Bill] [Clear] [Print] [View]     │ │
│  └────────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────────┐ │
│  │ Bills Table (Bottom Panel)                 │ │
│  │ [Bill ID | Patient | Doctor | Total | Date] │ │
│  │ ...                                         │ │
│  └────────────────────────────────────────────┘ │
│  Message: [Status Label]                        │
└──────────────────────────────────────────────────┘
```

### Component Definition

#### Class Declaration

```java
package ui;

import dao.BillDAO;
import dao.PatientDAO;
import dao.DoctorDAO;
import models.Bill;
import models.Patient;
import models.Doctor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Billing Form - Bill Generation and Management
 * Handles bill creation with automatic charge calculations
 */
public class BillingForm extends JPanel {
    
    // ========== UI COMPONENTS ==========
    
    // ComboBoxes for Selection
    private JComboBox<String> cbPatient;           // Select patient
    private JComboBox<String> cbDoctor;            // Select doctor
    
    // Text Fields for Charges
    private JTextField txtDoctorFee;               // Auto-filled
    private JTextField txtMedicineCharges;         // Enter amount
    private JTextField txtRoomCharges;             // Enter amount
    private JTextField txtOtherCharges;            // Enter amount
    private JTextField txtTotal;                   // Auto-calculated
    
    // Action Buttons
    private JButton btnGenerateBill;               // Create bill
    private JButton btnClear;                      // Clear form
    private JButton btnPrint;                      // Print bill (optional)
    private JButton btnViewBills;                  // View all bills
    
    // Labels and Display
    private JLabel lblMessage;                     // Status messages
    private JTable billTable;                      // Display bills
    private DefaultTableModel tableModel;          // Table data model
    
    // References
    private BillDAO billDAO;                       // Bill operations
    private PatientDAO patientDAO;                 // Patient data
    private DoctorDAO doctorDAO;                   // Doctor data
    private MainDashboard dashboard;               // Parent dashboard
    
    // Design Constants
    private static final Color MAROON = new Color(139, 0, 0);
    private static final Color LIGHT_GRAY = new Color(245, 248, 252);
    private static final Color WHITE_BG = new Color(255, 255, 255);
}
```

### Constructor

```java
public BillingForm(BillDAO billDAO, PatientDAO patientDAO, 
                   DoctorDAO doctorDAO, MainDashboard dashboard) {
    this.billDAO = billDAO;
    this.patientDAO = patientDAO;
    this.doctorDAO = doctorDAO;
    this.dashboard = dashboard;

    // Set panel layout
    setLayout(new BorderLayout());
    setBackground(LIGHT_GRAY);

    // Create header
    JLabel heading = new JLabel("  💳 Billing System", SwingConstants.LEFT);
    heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
    heading.setForeground(MAROON);
    heading.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 0));
    add(heading, BorderLayout.NORTH);

    // Create main panel
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setOpaque(false);
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

    // Add form panel (top)
    JPanel formPanel = createFormPanel();
    mainPanel.add(formPanel, BorderLayout.NORTH);

    // Add table panel (bottom)
    JPanel tablePanel = createTablePanel();
    mainPanel.add(tablePanel, BorderLayout.CENTER);

    add(mainPanel, BorderLayout.CENTER);
    
    // Load initial data
    loadPatientComboBox();
    loadDoctorComboBox();
    refreshBillTable();
}
```

### Form Panel Creation

```java
private JPanel createFormPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(WHITE_BG);
    panel.setBorder(BorderFactory.createTitledBorder("Generate New Bill"));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // ===== Row 0: Patient Selection =====
    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(new JLabel("Select Patient *"), gbc);
    gbc.gridx = 1;
    cbPatient = new JComboBox<>();
    cbPatient.addActionListener(e -> onPatientSelected());
    panel.add(cbPatient, gbc);

    gbc.gridx = 2;
    panel.add(new JLabel("Select Doctor *"), gbc);
    gbc.gridx = 3;
    cbDoctor = new JComboBox<>();
    cbDoctor.addActionListener(e -> onDoctorSelected());
    panel.add(cbDoctor, gbc);

    // ===== Row 1: Doctor Fee (Auto-filled) =====
    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(new JLabel("Doctor Fee (₹)"), gbc);
    gbc.gridx = 1;
    txtDoctorFee = new JTextField(15);
    txtDoctorFee.setText("0.0");
    txtDoctorFee.setEditable(false);  // Read-only
    panel.add(txtDoctorFee, gbc);

    gbc.gridx = 2;
    panel.add(new JLabel("Medicine Charges (₹) *"), gbc);
    gbc.gridx = 3;
    txtMedicineCharges = new JTextField(15);
    txtMedicineCharges.setText("0.0");
    txtMedicineCharges.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent e) {
            calculateTotal();
        }
    });
    panel.add(txtMedicineCharges, gbc);

    // ===== Row 2: Room and Other Charges =====
    gbc.gridx = 0;
    gbc.gridy = 2;
    panel.add(new JLabel("Room Charges (₹) *"), gbc);
    gbc.gridx = 1;
    txtRoomCharges = new JTextField(15);
    txtRoomCharges.setText("0.0");
    txtRoomCharges.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent e) {
            calculateTotal();
        }
    });
    panel.add(txtRoomCharges, gbc);

    gbc.gridx = 2;
    panel.add(new JLabel("Other Charges (₹)"), gbc);
    gbc.gridx = 3;
    txtOtherCharges = new JTextField(15);
    txtOtherCharges.setText("0.0");
    txtOtherCharges.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent e) {
            calculateTotal();
        }
    });
    panel.add(txtOtherCharges, gbc);

    // ===== Row 3: Total Amount =====
    gbc.gridx = 0;
    gbc.gridy = 3;
    panel.add(new JLabel("Total Amount (₹)"), gbc);
    gbc.gridx = 1;
    txtTotal = new JTextField(15);
    txtTotal.setText("0.0");
    txtTotal.setEditable(false);  // Read-only
    txtTotal.setFont(new Font("Arial", Font.BOLD, 14));
    panel.add(txtTotal, gbc);

    // ===== Row 4: Buttons =====
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 4;
    
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
    
    btnGenerateBill = new JButton("Generate Bill");
    btnGenerateBill.setBackground(MAROON);
    btnGenerateBill.setForeground(Color.WHITE);
    btnGenerateBill.addActionListener(e -> onGenerateBill());
    buttonPanel.add(btnGenerateBill);
    
    btnClear = new JButton("Clear");
    btnClear.setBackground(new Color(100, 100, 100));
    btnClear.setForeground(Color.WHITE);
    btnClear.addActionListener(e -> onClearForm());
    buttonPanel.add(btnClear);
    
    btnViewBills = new JButton("View All Bills");
    btnViewBills.setBackground(MAROON);
    btnViewBills.setForeground(Color.WHITE);
    buttonPanel.add(btnViewBills);
    
    panel.add(buttonPanel, gbc);

    // ===== Row 5: Message Label =====
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.gridwidth = 4;
    lblMessage = new JLabel(" ");
    lblMessage.setForeground(Color.RED);
    panel.add(lblMessage, gbc);

    return panel;
}
```

### Table Panel Creation

```java
private JPanel createTablePanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(WHITE_BG);
    panel.setBorder(BorderFactory.createTitledBorder("Bill History"));

    // Define table columns
    String[] columns = {"Bill ID", "Patient", "Doctor", "Doctor Fee", 
                       "Medicine", "Room", "Other", "Total (₹)", "Date"};
    tableModel = new DefaultTableModel(columns, 0);
    billTable = new JTable(tableModel);
    
    // Configure table
    billTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    billTable.setRowHeight(25);
    billTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    // Add table to scroll pane
    JScrollPane scrollPane = new JScrollPane(billTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
}
```

### Button Action Methods

```java
/**
 * Handle Generate Bill button click
 */
private void onGenerateBill() {
    try {
        // Validate selections
        if (cbPatient.getSelectedItem() == null || cbDoctor.getSelectedItem() == null) {
            lblMessage.setText("❌ Please select patient and doctor!");
            lblMessage.setForeground(Color.RED);
            return;
        }

        // Get selected IDs
        int patientId = getSelectedPatientId();
        int doctorId = getSelectedDoctorId();

        if (patientId == -1 || doctorId == -1) {
            lblMessage.setText("❌ Invalid selection!");
            lblMessage.setForeground(Color.RED);
            return;
        }

        // Get charges
        double doctorFee = Double.parseDouble(txtDoctorFee.getText());
        double medicineCharges = Double.parseDouble(txtMedicineCharges.getText());
        double roomCharges = Double.parseDouble(txtRoomCharges.getText());
        double otherCharges = Double.parseDouble(txtOtherCharges.getText());

        // Create and generate bill
        Bill bill = new Bill(patientId, doctorId, doctorFee, 
                            medicineCharges, roomCharges, otherCharges);

        if (billDAO.generateBill(bill)) {
            lblMessage.setText("✓ Bill generated successfully! Bill ID: " + bill.getBillId());
            lblMessage.setForeground(new Color(0, 128, 0));
            onClearForm();
            refreshBillTable();
        } else {
            lblMessage.setText("❌ Failed to generate bill!");
            lblMessage.setForeground(Color.RED);
        }
    } catch (NumberFormatException e) {
        lblMessage.setText("❌ Invalid amount entered!");
        lblMessage.setForeground(Color.RED);
    }
}

/**
 * Handle doctor selection - auto-fill fee
 */
private void onDoctorSelected() {
    int doctorId = getSelectedDoctorId();
    if (doctorId != -1) {
        Doctor doctor = doctorDAO.getDoctorById(doctorId);
        if (doctor != null) {
            txtDoctorFee.setText(String.valueOf(doctor.getConsultationFee()));
            calculateTotal();
        }
    }
}

/**
 * Calculate total bill amount
 */
private void calculateTotal() {
    try {
        double doctorFee = Double.parseDouble(txtDoctorFee.getText());
        double medicine = Double.parseDouble(txtMedicineCharges.getText());
        double room = Double.parseDouble(txtRoomCharges.getText());
        double other = Double.parseDouble(txtOtherCharges.getText());

        double total = doctorFee + medicine + room + other;
        txtTotal.setText(String.format("%.2f", total));
    } catch (NumberFormatException e) {
        txtTotal.setText("0.0");
    }
}

/**
 * Clear all form fields
 */
private void onClearForm() {
    cbPatient.setSelectedIndex(-1);
    cbDoctor.setSelectedIndex(-1);
    txtDoctorFee.setText("0.0");
    txtMedicineCharges.setText("0.0");
    txtRoomCharges.setText("0.0");
    txtOtherCharges.setText("0.0");
    txtTotal.setText("0.0");
    lblMessage.setText(" ");
}

/**
 * Load bills into table
 */
private void refreshBillTable() {
    tableModel.setRowCount(0);
    ArrayList<Bill> bills = billDAO.getAllBills();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    for (Bill bill : bills) {
        Object[] row = {
            bill.getBillId(),
            bill.getPatientName(),
            bill.getDoctorName(),
            bill.getDoctorFee(),
            bill.getMedicineCharges(),
            bill.getRoomCharges(),
            bill.getOtherCharges(),
            bill.getTotalAmount(),
            sdf.format(bill.getBillDate())
        };
        tableModel.addRow(row);
    }
}
```

---

## FEATURES & WORKFLOW

### Complete Billing Workflow

```
┌─ Patient Registration
│
├─ Appointment Booking with Doctor
│
└─ Bill Generation
   ├─ Select Patient from dropdown
   ├─ Select Doctor from dropdown
   ├─ Doctor Fee auto-filled
   ├─ Enter Charges:
   │  ├─ Medicine Charges
   │  ├─ Room Charges
   │  └─ Other Charges
   ├─ Total Auto-calculated
   ├─ Click "Generate Bill"
   ├─ Bill created in database
   └─ Display in table with timestamp
```

### Key Features

1. **Auto-fill Doctor Fee**
   - When doctor selected, fee automatically fills
   - Prevents manual entry errors
   - Real-time from database

2. **Automatic Total Calculation**
   - Updates as charges entered
   - Formula: Doctor + Medicine + Room + Other
   - Real-time preview

3. **Comprehensive Bill Tracking**
   - All bills visible in table
   - Searchable and sortable
   - Date-ordered display

4. **Multi-Component Charging**
   - Doctor consultation fee
   - Medicine charges
   - Room/bed charges
   - Miscellaneous charges

---

## BILLING CALCULATIONS

### Formula

```
Total Bill Amount = Doctor Fee + Medicine Charges + Room Charges + Other Charges
```

### Example Scenarios

#### Scenario 1: OPD Consultation
```
Doctor Fee:      ₹500  (from doctor record)
Medicine:        ₹200  (prescribed)
Room:            ₹0    (no room stay)
Other:           ₹50   (lab test)
─────────────────────
TOTAL:           ₹750
```

#### Scenario 2: Hospital Admission (1 day)
```
Doctor Fee:      ₹500
Medicine:        ₹800  (multiple medicines)
Room:           ₹1500  (single room per day)
Other:           ₹300  (lab tests, equipment)
─────────────────────
TOTAL:          ₹3100
```

#### Scenario 3: Surgery/Major Treatment
```
Doctor Fee:     ₹2000  (specialist surgeon)
Medicine:       ₹1500
Room:           ₹5000  (ICU, 1 day)
Other:          ₹3000  (equipment, blood tests)
─────────────────────
TOTAL:         ₹11500
```

---

## ERROR HANDLING & VALIDATION

### Input Validation

| Field | Rules | Example |
|-------|-------|---------|
| **Patient** | Must be selected from list | Ravi Sharma |
| **Doctor** | Must be selected from list | Dr. Ramesh Sharma |
| **Doctor Fee** | Non-negative number | 500.0 |
| **Medicine** | Non-negative number | 200.0 |
| **Room** | Non-negative number | 1500.0 |
| **Other** | Non-negative number | 150.0 |

### Database Constraints

```sql
-- Foreign key constraints
ALTER TABLE bills ADD CONSTRAINT fk_patient 
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE;

ALTER TABLE bills ADD CONSTRAINT fk_doctor 
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE;

-- Check constraints for non-negative values
ALTER TABLE bills ADD CONSTRAINT chk_doctor_fee 
    CHECK (doctor_fee >= 0);

ALTER TABLE bills ADD CONSTRAINT chk_medicine 
    CHECK (medicine_charges >= 0);

ALTER TABLE bills ADD CONSTRAINT chk_room 
    CHECK (room_charges >= 0);

ALTER TABLE bills ADD CONSTRAINT chk_other 
    CHECK (other_charges >= 0);

ALTER TABLE bills ADD CONSTRAINT chk_total 
    CHECK (total_amount >= 0);
```

### Error Messages

| Error | Message | Cause |
|-------|---------|-------|
| No Selection | "Please select patient and doctor!" | Missing selection |
| Invalid Amount | "Invalid amount entered!" | Non-numeric input |
| DB Error | "Failed to generate bill!" | SQL exception |
| Invalid Patient | "Patient not found!" | Non-existent patient |
| Invalid Doctor | "Doctor not found!" | Non-existent doctor |

---

## CODE EXAMPLES

### Example 1: Generate Bill Programmatically

```java
// Get patient and doctor
PatientDAO patientDAO = new PatientDAO();
DoctorDAO doctorDAO = new DoctorDAO();

Patient patient = patientDAO.getPatientById(1);
Doctor doctor = doctorDAO.getDoctorById(1);

// Create bill
Bill bill = new Bill(
    patient.getPatientId(),
    doctor.getDoctorId(),
    doctor.getConsultationFee(),  // 500.0
    200.0,   // Medicine
    1500.0,  // Room
    150.0    // Other
);

// Generate
BillDAO billDAO = new BillDAO();
if (billDAO.generateBill(bill)) {
    System.out.println("Bill ID: " + bill.getBillId());
    System.out.println("Total: ₹" + bill.getTotalAmount());
}
```

### Example 2: Get Patient's Total Medical Expenses

```java
BillDAO billDAO = new BillDAO();
ArrayList<Bill> patientBills = billDAO.getBillsForPatient(1);

double totalExpense = 0;
for (Bill bill : patientBills) {
    totalExpense += bill.getTotalAmount();
}

System.out.println("Patient total expense: ₹" + totalExpense);
```

### Example 3: Get Doctor's Revenue

```java
String sql = "SELECT d.name, SUM(b.total_amount) as revenue " +
             "FROM bills b " +
             "JOIN doctors d ON b.doctor_id = d.doctor_id " +
             "WHERE d.doctor_id = ? " +
             "GROUP BY d.doctor_id";

// Execute query and get revenue
```

### Example 4: Generate Monthly Report

```java
String sql = "SELECT DATE_FORMAT(bill_date, '%Y-%m') as month, " +
             "COUNT(*) as bill_count, " +
             "SUM(total_amount) as monthly_revenue " +
             "FROM bills " +
             "GROUP BY DATE_FORMAT(bill_date, '%Y-%m') " +
             "ORDER BY month DESC";

// Execute and display monthly reports
```

---

## INTEGRATION WITH OTHER MODULES

### Doctor Module Integration
- Doctor selection → Auto-fill consultation fee
- Doctor deletion → Cascade delete related bills

### Patient Module Integration
- Patient selection → Auto-fill patient name
- Patient deletion → Cascade delete related bills

### Appointment Module Integration
- Optional appointment_id reference
- Track bills against appointments
- Appointment-to-bill mapping

---

**Document Version:** 1.0  
**Last Updated:** April 2026  
**System:** Hospital Management System  
**Module:** Billing Management
