# Doctor Module - Comprehensive Documentation
## Hospital Management System

---

## Table of Contents
1. [Overview](#overview)
2. [Data Model](#data-model)
3. [Database Schema & Queries](#database-schema--queries)
4. [DAO (Data Access Object) Functionality](#dao-data-access-object-functionality)
5. [Java Swing UI Components](#java-swing-ui-components)
6. [Features & Workflow](#features--workflow)
7. [Error Handling & Validation](#error-handling--validation)
8. [Code Examples](#code-examples)

---

## OVERVIEW

The Doctor Module is the core component for managing doctor information in the Hospital Management System. It handles the complete lifecycle of doctor records from registration to updates and deletions, with a user-friendly Swing-based GUI.

### Key Features
- ✅ Add new doctors with validation
- ✅ View all doctors in a table format
- ✅ Update doctor information
- ✅ Delete doctor records
- ✅ Phone number uniqueness validation
- ✅ Real-time database synchronization
- ✅ Input validation and error handling

### Architecture Overview
```
User Interface (DoctorForm.java - JPanel)
        ↓
Business Logic (DoctorDAO.java)
        ↓
Database (MySQL - doctors table)
        ↓
Model (Doctor.java - POJO)
```

---

## DATA MODEL

### Doctor Class (POJO)

#### Location: `src/models/Doctor.java`

```java
package models;
import java.sql.Timestamp;

public class Doctor {
    
    // ========== ATTRIBUTES ==========
    private int doctorId;              // Unique ID (Auto-generated)
    private String name;               // Full name of doctor
    private String specialization;     // Medical specialization
    private String phone;              // Contact number (10 digits)
    private double consultationFee;    // Fee in ₹ (Indian Rupees)
    private Timestamp createdAt;       // Record creation timestamp
    
    // ========== CONSTRUCTORS ==========
    
    // Constructor for new doctor (without ID)
    public Doctor(String name, String specialization, String phone, double consultationFee) {
        this.name = name;
        this.specialization = specialization;
        this.phone = phone;
        this.consultationFee = consultationFee;
    }
    
    // Full constructor (with all fields including ID)
    public Doctor(int doctorId, String name, String specialization, 
                  String phone, double consultationFee, Timestamp createdAt) {
        this.doctorId = doctorId;
        this.name = name;
        this.specialization = specialization;
        this.phone = phone;
        this.consultationFee = consultationFee;
        this.createdAt = createdAt;
    }
    
    // ========== GETTERS & SETTERS ==========
    // All getters and setters for attributes
}
```

### Attribute Specifications

| Attribute | Type | Constraints | Description | Example |
|-----------|------|-------------|-------------|---------|
| doctorId | int | PK, AUTO_INCREMENT | Unique identifier | 1, 2, 3... |
| name | String (100) | NOT NULL | Doctor's full name | Dr. Ramesh Sharma |
| specialization | String (100) | NOT NULL | Medical field | Cardiology, Neurology |
| phone | String (15) | UNIQUE, NOT NULL | Contact number | 9876500001 |
| consultationFee | double | NOT NULL, DEFAULT 0 | Fee in ₹ | 500.0, 600.0 |
| createdAt | Timestamp | DEFAULT CURRENT_TIMESTAMP | Creation time | 2024-04-27 10:30:45 |

### Data Validation Rules

```
Name:
  - Non-null and non-empty
  - Should contain only letters and spaces
  - Length: 3-100 characters

Specialization:
  - Non-null and non-empty
  - Examples: Cardiology, Neurology, Orthopedics, Pediatrics, etc.
  - Length: 3-100 characters

Phone:
  - Must be exactly 10 digits
  - Only numbers allowed
  - Unique across all doctors in database
  - Indian format: 98765XXXXX

Consultation Fee:
  - Must be non-negative (≥ 0)
  - In Indian Rupees (₹)
  - Typical range: ₹300 - ₹1000
```

---

## DATABASE SCHEMA & QUERIES

### Table Structure

#### MySQL Table: `doctors`

```sql
CREATE TABLE IF NOT EXISTS doctors (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    consultation_fee DOUBLE NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Database Indexes
```sql
-- Primary Key Index (Auto-created)
ALTER TABLE doctors ADD PRIMARY KEY (doctor_id);

-- Recommended Indexes for Performance
ALTER TABLE doctors ADD UNIQUE INDEX idx_phone (phone);
ALTER TABLE doctors ADD INDEX idx_specialization (specialization);
ALTER TABLE doctors ADD INDEX idx_created_at (created_at);
```

### SQL Query Examples

#### 1. INSERT - Add New Doctor
```sql
-- Basic insert
INSERT INTO doctors (name, specialization, phone, consultation_fee) 
VALUES ('Dr. Ramesh Sharma', 'Cardiology', '9876500001', 500.0);

-- With explicit ID (not recommended)
INSERT INTO doctors (doctor_id, name, specialization, phone, consultation_fee) 
VALUES (1, 'Dr. Priya Gupta', 'Neurology', '9876500002', 600.0);

-- Multiple doctors at once
INSERT INTO doctors (name, specialization, phone, consultation_fee) 
VALUES 
('Dr. Ramesh Sharma', 'Cardiology', '9876500001', 500.0),
('Dr. Priya Gupta', 'Neurology', '9876500002', 600.0),
('Dr. Amit Kumar', 'Orthopedics', '9876500003', 550.0);
```

#### 2. SELECT - Retrieve Doctors

```sql
-- Get all doctors
SELECT * FROM doctors;

-- Get all doctors ordered by ID
SELECT * FROM doctors ORDER BY doctor_id ASC;

-- Get all doctors ordered by name
SELECT * FROM doctors ORDER BY name ASC;

-- Get specific doctor by ID
SELECT * FROM doctors WHERE doctor_id = 1;

-- Get all doctors in a specialization
SELECT * FROM doctors WHERE specialization = 'Cardiology';

-- Get doctors with fee in range
SELECT * FROM doctors 
WHERE consultation_fee BETWEEN 400 AND 700 
ORDER BY consultation_fee DESC;

-- Get recently added doctors (last 5)
SELECT * FROM doctors 
ORDER BY created_at DESC 
LIMIT 5;

-- Search doctors by partial name
SELECT * FROM doctors 
WHERE name LIKE '%Sharma%' 
ORDER BY name ASC;

-- Get doctors with high consultation fee
SELECT doctor_id, name, specialization, consultation_fee 
FROM doctors 
WHERE consultation_fee > 600 
ORDER BY consultation_fee DESC;

-- Count doctors by specialization
SELECT specialization, COUNT(*) as doctor_count 
FROM doctors 
GROUP BY specialization 
ORDER BY doctor_count DESC;
```

#### 3. UPDATE - Modify Doctor Information

```sql
-- Update single field
UPDATE doctors 
SET consultation_fee = 550.0 
WHERE doctor_id = 1;

-- Update multiple fields
UPDATE doctors 
SET name = 'Dr. Ramesh Sharma', 
    consultation_fee = 600.0 
WHERE doctor_id = 1;

-- Update all records (use with caution)
UPDATE doctors 
SET consultation_fee = consultation_fee * 1.1;

-- Conditional update based on specialization
UPDATE doctors 
SET consultation_fee = 700.0 
WHERE specialization = 'Cardiology';
```

#### 4. DELETE - Remove Doctor

```sql
-- Delete specific doctor by ID
DELETE FROM doctors WHERE doctor_id = 1;

-- Delete doctors from specific specialization (dangerous)
DELETE FROM doctors WHERE specialization = 'Neurology';

-- Delete all doctors (dangerous - use with caution)
DELETE FROM doctors;
```

#### 5. CHECK - Validate Data

```sql
-- Check if phone number exists
SELECT COUNT(*) FROM doctors WHERE phone = '9876500001';

-- Get the result as: COUNT(*) > 0 means phone exists

-- Check total number of doctors
SELECT COUNT(*) as total_doctors FROM doctors;

-- Check doctors in each specialization
SELECT specialization, COUNT(*) as count 
FROM doctors 
GROUP BY specialization;
```

#### 6. Complex Queries

```sql
-- Get doctor with highest consultation fee
SELECT * FROM doctors 
ORDER BY consultation_fee DESC 
LIMIT 1;

-- Get doctors sorted by specialization and name
SELECT * FROM doctors 
ORDER BY specialization ASC, name ASC;

-- Get statistics on consultation fees
SELECT 
    specialization,
    COUNT(*) as doctor_count,
    AVG(consultation_fee) as avg_fee,
    MIN(consultation_fee) as min_fee,
    MAX(consultation_fee) as max_fee
FROM doctors 
GROUP BY specialization;

-- Get doctors added in specific month
SELECT * FROM doctors 
WHERE YEAR(created_at) = 2024 AND MONTH(created_at) = 4
ORDER BY created_at DESC;
```

---

## DAO (DATA ACCESS OBJECT) FUNCTIONALITY

### Location: `src/dao/DoctorDAO.java`

The DoctorDAO class provides all database operations for the Doctor entity. It implements CRUD (Create, Read, Update, Delete) operations with proper error handling and validation.

### Class Overview

```java
package dao;

import db.DBConnection;
import models.Doctor;
import utils.ValidationUtil;
import java.sql.*;
import java.util.ArrayList;

public class DoctorDAO {
    private ArrayList<Doctor> doctorList = new ArrayList<>();
    // Methods...
}
```

### DAO Methods

#### 1. ADD DOCTOR

```java
/**
 * Add a new doctor to the database
 * @param doctor Doctor object with data
 * @return true if doctor added successfully, false otherwise
 */
public boolean addDoctor(Doctor doctor) {
    // Step 1: Input validation
    if (!validateDoctorInput(doctor)) {
        System.err.println("✗ Invalid doctor data provided. Please check all fields.");
        return false;
    }

    // Step 2: Check for duplicate phone
    if (isPhoneExists(doctor.getPhone())) {
        System.err.println("✗ Error: Phone number already registered!");
        return false;
    }

    // Step 3: Prepare SQL statement
    String sql = "INSERT INTO doctors (name, specialization, phone, consultation_fee) " +
                 "VALUES (?, ?, ?, ?)";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pst = conn.prepareStatement(sql, 
                                 Statement.RETURN_GENERATED_KEYS)) {
        
        // Step 4: Set parameters
        pst.setString(1, doctor.getName());
        pst.setString(2, doctor.getSpecialization());
        pst.setString(3, doctor.getPhone());
        pst.setDouble(4, doctor.getConsultationFee());

        // Step 5: Execute insert
        int rowsAffected = pst.executeUpdate();

        if (rowsAffected > 0) {
            // Step 6: Get generated ID
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

// Usage Example:
Doctor doctor = new Doctor("Dr. John Doe", "Cardiology", "9876543210", 500.0);
boolean success = doctorDAO.addDoctor(doctor);
if (success) {
    System.out.println("Doctor ID: " + doctor.getDoctorId());
}
```

#### 2. GET ALL DOCTORS

```java
/**
 * Get all doctors from database
 * @return ArrayList of all doctors
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

// Usage Example:
ArrayList<Doctor> doctors = doctorDAO.getAllDoctors();
for (Doctor doc : doctors) {
    System.out.println(doc.getName() + " - " + doc.getSpecialization());
}
```

#### 3. GET DOCTOR BY ID

```java
/**
 * Get a specific doctor by ID
 * @param doctorId Doctor ID
 * @return Doctor object or null if not found
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

// Usage Example:
Doctor doctor = doctorDAO.getDoctorById(1);
if (doctor != null) {
    System.out.println("Doctor: " + doctor.getName());
}
```

#### 4. UPDATE DOCTOR

```java
/**
 * Update doctor information
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

// Usage Example:
Doctor doctor = doctorDAO.getDoctorById(1);
doctor.setConsultationFee(600.0);
doctorDAO.updateDoctor(doctor);
```

#### 5. DELETE DOCTOR

```java
/**
 * Delete a doctor from database
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

// Usage Example:
boolean deleted = doctorDAO.deleteDoctor(1);
if (deleted) {
    System.out.println("Doctor removed from system");
}
```

#### 6. HELPER METHODS

```java
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
```

---

## JAVA SWING UI COMPONENTS

### Location: `src/ui/DoctorForm.java`

The DoctorForm is a custom JPanel that provides the user interface for doctor management operations.

### UI Structure

```
┌─────────────────────────────────────────────────────┐
│  👨‍⚕️ Doctor Management                              │
├──────────────────────────────────────────────────────┤
│  ┌──────────────────────┐  ┌────────────────────┐  │
│  │   Form Panel (LEFT)  │  │  Table Panel (RIGHT) │  │
│  │                      │  │                     │  │
│  │ Name: [Field]       │  │   Doctor Table      │  │
│  │ Specialization: [Fd]│  │  [All Doctors]      │  │
│  │ Phone: [Field]      │  │                     │  │
│  │ Fee: [Field]        │  │                     │  │
│  │ [Add] [Update]      │  │                     │  │
│  │ [Delete] [Clear]    │  │                     │  │
│  │                     │  │                     │  │
│  └──────────────────────┘  └────────────────────┘  │
│  Message: [Status Label]                            │
└──────────────────────────────────────────────────────┘
```

### Component Definition

#### Class Declaration

```java
package ui;

import dao.DoctorDAO;
import models.Doctor;
import utils.ValidationUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Doctor Management Form
 * Handles adding, viewing, updating, and deleting doctors
 */
public class DoctorForm extends JPanel {
    
    // ========== UI COMPONENTS ==========
    
    // Text Fields for Input
    private JTextField txtName;              // Doctor name input
    private JTextField txtSpecialization;   // Specialization input
    private JTextField txtPhone;            // Phone number input
    private JTextField txtConsultationFee;  // Consultation fee input
    
    // Action Buttons
    private JButton btnAdd;                 // Add new doctor
    private JButton btnUpdate;              // Update selected doctor
    private JButton btnDelete;              // Delete selected doctor
    private JButton btnClear;               // Clear form fields
    
    // Labels and Display
    private JLabel lblMessage;              // Status/Error messages
    private JTable doctorTable;             // Table to display doctors
    private DefaultTableModel tableModel;   // Table data model
    
    // References
    private DoctorDAO doctorDAO;            // Data Access Object
    private MainDashboard dashboard;        // Parent dashboard
    private int selectedDoctorId = -1;      // Currently selected doctor ID
    
    // Design Constants
    private static final Color MAROON = new Color(139, 0, 0);
    private static final Color LIGHT_GRAY = new Color(245, 248, 252);
    private static final Color WHITE_BG = new Color(255, 255, 255);
}
```

### Constructor

```java
public DoctorForm(DoctorDAO dao, MainDashboard dashboard) {
    this.doctorDAO = dao;
    this.dashboard = dashboard;
    
    // Set panel layout
    setLayout(new BorderLayout());
    setBackground(LIGHT_GRAY);

    // Create header
    JLabel heading = new JLabel("  👨‍⚕️ Doctor Management", SwingConstants.LEFT);
    heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
    heading.setForeground(MAROON);
    heading.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 0));
    add(heading, BorderLayout.NORTH);

    // Create main panel with form and table
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setOpaque(false);
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

    // Add form panel (left side)
    JPanel formPanel = createFormPanel();
    mainPanel.add(formPanel, BorderLayout.WEST);

    // Add table panel (right side)
    JPanel tablePanel = createTablePanel();
    mainPanel.add(tablePanel, BorderLayout.CENTER);

    add(mainPanel, BorderLayout.CENTER);

    // Load doctors into table
    refreshTable();
}
```

### Form Panel Creation

```java
private JPanel createFormPanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBackground(WHITE_BG);
    panel.setBorder(BorderFactory.createTitledBorder("Add/Update Doctor"));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // ===== Name Field =====
    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(new JLabel("Name *"), gbc);
    gbc.gridx = 1;
    txtName = new JTextField(20);
    panel.add(txtName, gbc);

    // ===== Specialization Field =====
    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(new JLabel("Specialization *"), gbc);
    gbc.gridx = 1;
    txtSpecialization = new JTextField(20);
    panel.add(txtSpecialization, gbc);

    // ===== Phone Field =====
    gbc.gridx = 0;
    gbc.gridy = 2;
    panel.add(new JLabel("Phone (10 digits) *"), gbc);
    gbc.gridx = 1;
    txtPhone = new JTextField(20);
    panel.add(txtPhone, gbc);

    // ===== Consultation Fee Field =====
    gbc.gridx = 0;
    gbc.gridy = 3;
    panel.add(new JLabel("Consultation Fee (₹) *"), gbc);
    gbc.gridx = 1;
    txtConsultationFee = new JTextField(20);
    panel.add(txtConsultationFee, gbc);

    // ===== Buttons Panel =====
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 2;
    
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
    
    btnAdd = new JButton("Add");
    btnAdd.setBackground(MAROON);
    btnAdd.setForeground(Color.WHITE);
    btnAdd.addActionListener(e -> onAddDoctor());
    buttonPanel.add(btnAdd);
    
    btnUpdate = new JButton("Update");
    btnUpdate.setBackground(MAROON);
    btnUpdate.setForeground(Color.WHITE);
    btnUpdate.addActionListener(e -> onUpdateDoctor());
    buttonPanel.add(btnUpdate);
    
    btnDelete = new JButton("Delete");
    btnDelete.setBackground(new Color(200, 0, 0));
    btnDelete.setForeground(Color.WHITE);
    btnDelete.addActionListener(e -> onDeleteDoctor());
    buttonPanel.add(btnDelete);
    
    btnClear = new JButton("Clear");
    btnClear.setBackground(new Color(100, 100, 100));
    btnClear.setForeground(Color.WHITE);
    btnClear.addActionListener(e -> onClearForm());
    buttonPanel.add(btnClear);
    
    panel.add(buttonPanel, gbc);

    // ===== Message Label =====
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.gridwidth = 2;
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
    panel.setBorder(BorderFactory.createTitledBorder("Doctor List"));

    // Define table columns
    String[] columns = {"ID", "Name", "Specialization", "Phone", "Fee (₹)"};
    tableModel = new DefaultTableModel(columns, 0);
    doctorTable = new JTable(tableModel);
    
    // Configure table appearance
    doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    doctorTable.setRowHeight(25);
    doctorTable.getSelectionModel().addListSelectionListener(e -> {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow >= 0) {
            loadDoctorToForm(selectedRow);
        }
    });

    // Add table to scroll pane
    JScrollPane scrollPane = new JScrollPane(doctorTable);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
}
```

### Button Action Methods

```java
/**
 * Handle Add Doctor button click
 */
private void onAddDoctor() {
    try {
        // Get form values
        String name = txtName.getText().trim();
        String specialization = txtSpecialization.getText().trim();
        String phone = txtPhone.getText().trim();
        String feeStr = txtConsultationFee.getText().trim();

        // Validate inputs
        if (name.isEmpty() || specialization.isEmpty() || phone.isEmpty() || feeStr.isEmpty()) {
            lblMessage.setText("❌ All fields are required!");
            lblMessage.setForeground(Color.RED);
            return;
        }

        // Validate phone format
        if (!ValidationUtil.isValidPhone(phone)) {
            lblMessage.setText("❌ Phone must be 10 digits!");
            lblMessage.setForeground(Color.RED);
            return;
        }

        // Parse fee
        double fee = Double.parseDouble(feeStr);
        if (fee < 0) {
            lblMessage.setText("❌ Fee cannot be negative!");
            lblMessage.setForeground(Color.RED);
            return;
        }

        // Create and add doctor
        Doctor doctor = new Doctor(name, specialization, phone, fee);
        if (doctorDAO.addDoctor(doctor)) {
            lblMessage.setText("✓ Doctor added successfully!");
            lblMessage.setForeground(new Color(0, 128, 0));
            onClearForm();
            refreshTable();
        } else {
            lblMessage.setText("❌ Failed to add doctor - phone may already exist!");
            lblMessage.setForeground(Color.RED);
        }
    } catch (NumberFormatException e) {
        lblMessage.setText("❌ Invalid fee format!");
        lblMessage.setForeground(Color.RED);
    }
}

/**
 * Handle Update Doctor button click
 */
private void onUpdateDoctor() {
    if (selectedDoctorId == -1) {
        lblMessage.setText("❌ Please select a doctor to update!");
        lblMessage.setForeground(Color.RED);
        return;
    }

    try {
        String name = txtName.getText().trim();
        String specialization = txtSpecialization.getText().trim();
        String phone = txtPhone.getText().trim();
        String feeStr = txtConsultationFee.getText().trim();

        if (name.isEmpty() || specialization.isEmpty() || phone.isEmpty() || feeStr.isEmpty()) {
            lblMessage.setText("❌ All fields are required!");
            lblMessage.setForeground(Color.RED);
            return;
        }

        double fee = Double.parseDouble(feeStr);
        Doctor doctor = new Doctor(selectedDoctorId, name, specialization, phone, fee, null);

        if (doctorDAO.updateDoctor(doctor)) {
            lblMessage.setText("✓ Doctor updated successfully!");
            lblMessage.setForeground(new Color(0, 128, 0));
            onClearForm();
            refreshTable();
        } else {
            lblMessage.setText("❌ Failed to update doctor!");
            lblMessage.setForeground(Color.RED);
        }
    } catch (NumberFormatException e) {
        lblMessage.setText("❌ Invalid fee format!");
        lblMessage.setForeground(Color.RED);
    }
}

/**
 * Handle Delete Doctor button click
 */
private void onDeleteDoctor() {
    if (selectedDoctorId == -1) {
        lblMessage.setText("❌ Please select a doctor to delete!");
        lblMessage.setForeground(Color.RED);
        return;
    }

    int response = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to delete this doctor?",
        "Confirm Delete",
        JOptionPane.YES_NO_OPTION
    );

    if (response == JOptionPane.YES_OPTION) {
        if (doctorDAO.deleteDoctor(selectedDoctorId)) {
            lblMessage.setText("✓ Doctor deleted successfully!");
            lblMessage.setForeground(new Color(0, 128, 0));
            onClearForm();
            refreshTable();
        } else {
            lblMessage.setText("❌ Failed to delete doctor!");
            lblMessage.setForeground(Color.RED);
        }
    }
}

/**
 * Clear all form fields
 */
private void onClearForm() {
    txtName.setText("");
    txtSpecialization.setText("");
    txtPhone.setText("");
    txtConsultationFee.setText("");
    lblMessage.setText(" ");
    selectedDoctorId = -1;
    doctorTable.clearSelection();
}

/**
 * Load selected doctor data into form
 */
private void loadDoctorToForm(int rowIndex) {
    selectedDoctorId = (int) tableModel.getValueAt(rowIndex, 0);
    String name = (String) tableModel.getValueAt(rowIndex, 1);
    String specialization = (String) tableModel.getValueAt(rowIndex, 2);
    String phone = (String) tableModel.getValueAt(rowIndex, 3);
    double fee = (double) tableModel.getValueAt(rowIndex, 4);

    txtName.setText(name);
    txtSpecialization.setText(specialization);
    txtPhone.setText(phone);
    txtConsultationFee.setText(String.valueOf(fee));
    lblMessage.setText("✓ Doctor selected - ready to update or delete");
    lblMessage.setForeground(new Color(0, 128, 0));
}

/**
 * Refresh doctor table with latest data
 */
private void refreshTable() {
    tableModel.setRowCount(0);
    ArrayList<Doctor> doctors = doctorDAO.getAllDoctors();

    for (Doctor doctor : doctors) {
        Object[] row = {
            doctor.getDoctorId(),
            doctor.getName(),
            doctor.getSpecialization(),
            doctor.getPhone(),
            doctor.getConsultationFee()
        };
        tableModel.addRow(row);
    }
}
```

---

## FEATURES & WORKFLOW

### Complete Doctor Management Workflow

```
┌─ Start Application
│
├─ Load DoctorForm
│  └─ Initialize DoctorDAO
│  └─ Fetch all doctors from database
│  └─ Display in table
│
├─ Add New Doctor
│  ├─ User enters name, specialization, phone, fee
│  ├─ Validates all inputs
│  ├─ Checks phone uniqueness
│  ├─ Inserts into database
│  └─ Refreshes table
│
├─ Select Doctor from Table
│  ├─ Row selection event triggered
│  ├─ Loads doctor data into form
│  ├─ Ready for update or delete
│
├─ Update Doctor
│  ├─ User modifies fields
│  ├─ Clicks Update
│  ├─ Updates database
│  └─ Refreshes table
│
└─ Delete Doctor
   ├─ User confirms deletion
   ├─ Removes from database
   └─ Refreshes table
```

### Features

1. **Add Doctor**
   - Input validation (non-empty fields, valid phone)
   - Phone uniqueness check
   - Auto-generated ID
   - Real-time feedback

2. **View Doctors**
   - Table display of all doctors
   - Sortable columns
   - Selection support
   - Auto-refresh

3. **Update Doctor**
   - Select doctor from table
   - Modify any field
   - Database update
   - Validation applied

4. **Delete Doctor**
   - Confirmation dialog
   - Cascade delete handling
   - Visual feedback

5. **Search/Filter** (Advanced)
   - By specialization
   - By name
   - By fee range

---

## ERROR HANDLING & VALIDATION

### Input Validation

| Field | Rules |
|-------|-------|
| **Name** | Non-empty, 3-100 chars |
| **Specialization** | Non-empty, 3-100 chars |
| **Phone** | Exactly 10 digits, unique |
| **Fee** | Non-negative number |

### Database Validation

```sql
-- Unique phone constraint
ALTER TABLE doctors ADD CONSTRAINT uk_phone UNIQUE (phone);

-- Not null constraints
ALTER TABLE doctors MODIFY COLUMN name VARCHAR(100) NOT NULL;
ALTER TABLE doctors MODIFY COLUMN specialization VARCHAR(100) NOT NULL;
ALTER TABLE doctors MODIFY COLUMN consultation_fee DOUBLE NOT NULL DEFAULT 0;
```

### Error Messages

| Error | Message | Cause |
|-------|---------|-------|
| Empty Fields | "All fields are required!" | Missing input |
| Invalid Phone | "Phone must be 10 digits!" | Wrong format |
| Duplicate Phone | "Phone number already exists!" | Non-unique phone |
| Invalid Fee | "Fee cannot be negative!" | Wrong value |
| DB Error | "Database error while..." | SQL exception |

---

## CODE EXAMPLES

### Example 1: Adding a Doctor Programmatically

```java
// Create Doctor object
Doctor newDoctor = new Doctor(
    "Dr. Rajesh Patel",
    "Orthopedics",
    "9876543210",
    550.0
);

// Initialize DAO
DoctorDAO doctorDAO = new DoctorDAO();

// Add to database
if (doctorDAO.addDoctor(newDoctor)) {
    System.out.println("Doctor added with ID: " + newDoctor.getDoctorId());
} else {
    System.out.println("Failed to add doctor");
}
```

### Example 2: Retrieving and Updating Doctor

```java
// Get all doctors
DoctorDAO dao = new DoctorDAO();
ArrayList<Doctor> doctors = dao.getAllDoctors();

// Find and update
for (Doctor doctor : doctors) {
    if (doctor.getName().equals("Dr. John Doe")) {
        doctor.setConsultationFee(600.0);
        dao.updateDoctor(doctor);
        break;
    }
}
```

### Example 3: Phone Validation

```java
public static boolean isValidPhone(String phone) {
    // Check if exactly 10 digits
    if (phone == null || phone.length() != 10) {
        return false;
    }
    // Check if all characters are digits
    return phone.matches("\\d{10}");
}
```

### Example 4: Getting Doctor Statistics

```java
String sql = "SELECT specialization, COUNT(*) as count, AVG(consultation_fee) as avg_fee " +
             "FROM doctors GROUP BY specialization";

try (Connection conn = DBConnection.getConnection();
     Statement stmt = conn.createStatement();
     ResultSet rs = stmt.executeQuery(sql)) {
    
    while (rs.next()) {
        System.out.println(rs.getString("specialization") + 
                         ": " + rs.getInt("count") + 
                         " doctors, Avg Fee: ₹" + rs.getDouble("avg_fee"));
    }
}
```

---

## DATABASE BACKUP & MAINTENANCE

### Backup Query

```sql
-- Export doctors table
mysqldump -u root -p hospital_db doctors > doctors_backup.sql

-- Restore doctors table
mysql -u root -p hospital_db < doctors_backup.sql
```

---

**Document Version:** 1.0  
**Last Updated:** April 2026  
**System:** Hospital Management System  
**Module:** Doctor Management
