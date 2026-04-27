# 🏥 Hospital Management System (HMS)
## Complete Project Documentation & Presentation Guide

**Project Date:** April 2026  
**Status:** Production Ready | **Version:** 1.0  
**Platform:** Java Swing Desktop Application  

---

## 📑 Table of Contents

1. [Executive Overview](#executive-overview)
2. [Project Architecture](#project-architecture)
3. [Technology Stack](#technology-stack)
4. [Database Design & Schema](#database-design--schema)
5. [Module 1: Patient Management](#module-1-patient-management)
6. [Module 2: Appointment Scheduling](#module-2-appointment-scheduling)
7. [Module 3: Doctor Management](#module-3-doctor-management)
8. [Module 4: Billing System](#module-4-billing-system)
9. [Java Swing Frontend Implementation](#java-swing-frontend-implementation)
10. [Data Access Layer (DAO Pattern)](#data-access-layer-dao-pattern)
11. [Validation & Security](#validation--security)
12. [Project Structure](#project-structure)
13. [Installation & Execution](#installation--execution)

---

## Executive Overview

### What is Hospital Management System?

The **Hospital Management System (HMS)** is a comprehensive desktop application designed to streamline hospital operations. It provides an integrated platform for managing patients, appointments, doctors, and billing with a modern graphical user interface built using Java Swing.

### Key Features ✨

| Feature | Description |
|---------|-------------|
| **Patient Management** | Register, update, search, and manage patient information |
| **Appointment Scheduling** | Book appointments with automatic token generation |
| **Doctor Management** | Manage doctor profiles, specializations, and consultation fees |
| **Billing System** | Generate bills, track payments, and create invoices |
| **Data Validation** | Real-time input validation and duplicate prevention |
| **Database Integration** | MySQL database for persistent data storage |
| **User-Friendly GUI** | Intuitive Java Swing interface with responsive design |

### Target Users

- Hospital Administrators
- Reception Staff
- Doctors
- Billing Department
- Hospital Management

---

## Project Architecture

### System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                      Java Swing GUI (Frontend)              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  MainDashboard (CardLayout with 6 panels)            │  │
│  │  - Dashboard Panel                                   │  │
│  │  - Add Patient Form                                  │  │
│  │  - View All Patients                                 │  │
│  │  - Appointment Booking                               │  │
│  │  - Doctor Management                                 │  │
│  │  - Billing System                                    │  │
│  └──────────────────────────────────────────────────────┘  │
└──────────────────────┬──────────────────────────────────────┘
                       │
        ┌──────────────┼──────────────┐
        │              │              │
   Models Layer   DAO Layer    Validation Layer
        │              │              │
   ┌─────────────┐ ┌──────────────┐ ┌────────────────┐
   │ Patient     │ │ PatientDAO   │ │ ValidationUtil │
   │ Appointment │ │ AppointmentD │ │ (Regex-based)  │
   │ Doctor      │ │ DoctorDAO    │ │ - Email        │
   │ Bill        │ │ BillDAO      │ │ - Phone        │
   └─────────────┘ └──────────────┘ │ - Age          │
                                     │ - Blood Group  │
                                     └────────────────┘
        │
   Database Layer
        │
   ┌──────────────────┐
   │ DBConnection.java│
   │ (JDBC Connection)│
   └──────────────────┘
        │
   ┌──────────────────┐
   │   MySQL 5.7+     │
   │ (hospital_db)    │
   └──────────────────┘
```

### Layered Architecture Benefits

1. **Separation of Concerns:** Each layer has specific responsibility
2. **Code Reusability:** Models and DAOs can be used in multiple UI forms
3. **Maintainability:** Easy to update any layer without affecting others
4. **Testability:** Each component can be tested independently
5. **Scalability:** Easy to add new modules or features

---

## Technology Stack

### Frontend Layer
| Component | Technology | Details |
|-----------|-----------|---------|
| UI Framework | **Java Swing** | javax.swing.* (standard Java library) |
| Look & Feel | **Nimbus UI** | Modern, cross-platform appearance |
| Layout Managers | **CardLayout, BorderLayout, GridLayout, GridBagLayout** | For responsive UI design |
| Containers | **JFrame, JPanel, JButton, JTextField, JComboBox, JTextArea** | Standard Swing components |

### Backend Layer
| Component | Technology | Details |
|-----------|-----------|---------|
| Language | **Java (Core Java)** | JDK 11 or higher |
| Database Access | **JDBC (Java Database Connectivity)** | java.sql.* package |
| Connection Pool | **Manual Connection Management** | DBConnection singleton pattern |
| Data Structures | **LinkedList (Patient), ArrayList (Appointment)** | In-memory collection support |

### Database Layer
| Component | Technology | Details |
|-----------|-----------|---------|
| Database | **MySQL Server** | Version 5.7 LTS or higher |
| JDBC Driver | **mysql-connector-j-9.6.0** | Official MySQL connector for Java |
| Connection | **TCP/IP** | localhost:3306 (default) |
| Encoding | **UTF-8** | International character support |

### Build & Execution
| Tool | Purpose |
|------|---------|
| **javac** | Java Compiler (command-line compilation) |
| **java** | Java Runtime Environment |
| **classpath** | Includes mysql-connector-j-9.6.0.jar |

---

## Database Design & Schema

### Database: hospital_db

```sql
CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;
```

### Table 1: DOCTORS

**Purpose:** Store doctor information and specializations

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

**Fields Explanation:**
- `doctor_id`: Unique identifier (AUTO_INCREMENT)
- `name`: Doctor's full name
- `specialization`: Medical field (Cardiology, Neurology, etc.)
- `phone`: Contact number
- `consultation_fee`: Charges for consultation
- `created_at`: Record creation timestamp

**Sample Data:**
```sql
INSERT INTO doctors VALUES
(1, 'Dr. Ramesh Sharma', 'Cardiology', '9876500001', 500.0, NOW()),
(2, 'Dr. Priya Gupta', 'Neurology', '9876500002', 600.0, NOW());
```

---

### Table 2: PATIENTS

**Purpose:** Store patient demographic and medical information

```sql
CREATE TABLE IF NOT EXISTS patients (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    age INT NOT NULL CHECK (age > 0 AND age < 150),
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    blood_group ENUM('A+','A-','B+','B-','AB+','AB-','O+','O-') NOT NULL,
    phone VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100),
    address TEXT,
    medical_history TEXT,
    registration_date DATE DEFAULT (CURDATE()),
    status ENUM('Active', 'Discharged', 'Critical') DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Fields Explanation:**
- `patient_id`: Unique identifier (AUTO_INCREMENT)
- `first_name`, `last_name`: Patient's name
- `age`: Age with constraint (1-149 years)
- `gender`: Male, Female, or Other (ENUM)
- `blood_group`: Blood type (8 types using ENUM)
- `phone`: Unique phone number (prevents duplicates)
- `email`: Optional email address
- `address`: Full address/location
- `medical_history`: Past medical conditions and treatments
- `registration_date`: When patient registered
- `status`: Active/Discharged/Critical
- `created_at`: Record creation timestamp

**Key Constraints:**
- UNIQUE on phone (prevents duplicate registrations)
- CHECK constraint on age (1-149)
- ENUM prevents invalid data entry

---

### Table 3: APPOINTMENTS

**Purpose:** Track all appointment bookings with status

```sql
CREATE TABLE IF NOT EXISTS appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    department VARCHAR(100) NOT NULL,
    reason TEXT,
    status ENUM('Scheduled', 'Completed', 'Cancelled', 'Pending') DEFAULT 'Scheduled',
    token_number INT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE
);
```

**Fields Explanation:**
- `appointment_id`: Unique appointment identifier
- `patient_id`: Reference to patients table (FOREIGN KEY)
- `doctor_id`: Reference to doctors table (FOREIGN KEY)
- `appointment_date`: Date of appointment (YYYY-MM-DD)
- `appointment_time`: Time of appointment (HH:MM:SS)
- `department`: Medical department
- `reason`: Reason for visit
- `status`: Scheduled/Completed/Cancelled/Pending (ENUM)
- `token_number`: Queue token for patients
- `notes`: Additional medical notes
- `created_at`: When appointment was booked

**Foreign Key Constraints:**
- ON DELETE CASCADE: If patient/doctor deleted, appointments also deleted
- Maintains referential integrity

---

### Table 4: BILLS

**Purpose:** Generate and track billing information

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

**Fields Explanation:**
- `bill_id`: Unique bill identifier
- `patient_id`: Reference to patient (FOREIGN KEY)
- `doctor_id`: Reference to doctor (FOREIGN KEY)
- `appointment_id`: Related appointment (can be NULL)
- `doctor_fee`: Consultation charges
- `medicine_charges`: Medication costs
- `room_charges`: Hospital room charges
- `other_charges`: Additional charges
- `total_amount`: Sum of all charges
- `bill_date`: When bill was generated

**Indexes (for performance):**
- `idx_patient_id`: Fast patient lookup
- `idx_doctor_id`: Fast doctor lookup
- `idx_bill_date`: Fast date-based queries

---

### Database Relationships (ERD)

```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│   DOCTORS   │         │ APPOINTMENTS │         │  PATIENTS   │
├─────────────┤         ├──────────────┤         ├─────────────┤
│ doctor_id◄──┼─────────┤ doctor_id    │         │ patient_id◄─┼────────┐
│ name        │ 1:N     │ patient_id──┼─────────►│ first_name  │ 1:N   │
│ speciality  │         │ date         │         │ last_name   │       │
│ phone       │         │ time         │ N:1     │ phone       │       │
│ fee         │         │ status       │         │ email       │       │
└─────────────┘         │ token        │         │ address     │       │
                        │ notes        │         └─────────────┘       │
                        └──────────────┘                              │
                               ▲                                      │
                               │ 1:N                                  │
                               │                                      │
                        ┌──────┴──────┐                               │
                        │   BILLS     │◄──────────────────────────────┘
                        ├─────────────┤
                        │ bill_id     │
                        │ patient_id  │
                        │ doctor_id   │
                        │ appt_id     │
                        │ doctor_fee  │
                        │ medicine$   │
                        │ room_fee    │
                        │ other_fee   │
                        │ total_amt   │
                        └─────────────┘
```

---

## Module 1: Patient Management

### Module Overview

The Patient Management module handles all operations related to patient registration and information management.

### Database Operations

#### 1. **ADD PATIENT**

**SQL Query:**
```sql
INSERT INTO patients (first_name, last_name, age, gender, blood_group, 
                      phone, email, address, medical_history, status) 
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
```

**Implementation in PatientDAO:**
```java
public boolean addPatient(Patient patient) {
    // 1. Validate patient input (name, age, phone, email, blood group)
    // 2. Check if phone already exists (duplicate prevention)
    // 3. If phone exists → Show error message and return false
    // 4. Prepare SQL INSERT statement with PreparedStatement
    // 5. Set parameters safely (prevents SQL injection)
    // 6. Execute update and get generated patient_id
    // 7. Add patient to LinkedList for in-memory caching
    // 8. Return success/failure
    
    String sql = "INSERT INTO patients (...) VALUES (?, ?, ?, ...)";
    PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    // Set all parameters
    pst.executeUpdate();
    // Get generated key
    ResultSet rs = pst.getGeneratedKeys();
    return true;
}
```

**Validation Steps:**
- ✅ First name: 2-50 characters, letters only
- ✅ Last name: 2-50 characters, letters only
- ✅ Age: Between 1-149 years
- ✅ Gender: Must be Male/Female/Other
- ✅ Blood Group: One of 8 valid groups
- ✅ Phone: Exactly 10 digits (no duplicates)
- ✅ Email: Valid email format (if provided)

#### 2. **SEARCH PATIENTS**

**SQL Query:**
```sql
SELECT * FROM patients 
WHERE first_name LIKE ? OR last_name LIKE ? OR phone LIKE ?
ORDER BY patient_id DESC;
```

**Implementation:**
```java
public ArrayList<Patient> searchPatients(String keyword) {
    String sql = "SELECT * FROM patients " +
                 "WHERE first_name LIKE ? OR last_name LIKE ? OR phone LIKE ? " +
                 "ORDER BY patient_id DESC";
    
    // Uses LIKE operator with wildcards (%)
    // Returns matching patients as ArrayList
}
```

#### 3. **GET ALL PATIENTS**

**SQL Query:**
```sql
SELECT * FROM patients ORDER BY patient_id DESC;
```

**Returns:** Complete list of all registered patients with pagination support

#### 4. **UPDATE PATIENT**

**SQL Query:**
```sql
UPDATE patients 
SET first_name=?, last_name=?, age=?, gender=?, blood_group=?, 
    phone=?, email=?, address=?, medical_history=?, status=?
WHERE patient_id = ?;
```

**Implementation:** Full patient record update with validation

#### 5. **GET TOTAL COUNT**

**SQL Query:**
```sql
SELECT COUNT(*) FROM patients;
```

**Usage:** Display total patient count on dashboard

### Patient Model Class

```java
public class Patient {
    // Properties
    private int patientId;
    private String firstName;
    private String lastName;
    private int age;
    private String gender;           // Male/Female/Other
    private String bloodGroup;       // A+/A-/B+/B-/AB+/AB-/O+/O-
    private String phone;            // 10 digits
    private String email;
    private String address;
    private String medicalHistory;
    private Date registrationDate;
    private String status;           // Active/Discharged/Critical
    
    // Constructor 1: New patient (no ID)
    public Patient(String firstName, String lastName, int age, 
                   String gender, String bloodGroup, String phone, 
                   String email, String address, String medicalHistory)
    
    // Constructor 2: Complete patient (from database)
    public Patient(int patientId, String firstName, String lastName, 
                   int age, String gender, String bloodGroup, String phone,
                   String email, String address, String medicalHistory, 
                   Date registrationDate, String status)
    
    // Getters and Setters for all fields
    public String getFullName() { 
        return firstName + " " + lastName; 
    }
}
```

### Data Structure Used

**LinkedList<Patient> patientQueue**
- Simulates a queue data structure
- Used for in-memory caching of patients
- Operations: addLast(), removeFirst()
- Benefit: FIFO order, fast insertion/deletion at ends

---

## Module 2: Appointment Scheduling

### Module Overview

The Appointment module manages all appointment bookings with token generation and duplicate prevention.

### Database Operations

#### 1. **BOOK APPOINTMENT**

**SQL Query:**
```sql
INSERT INTO appointments (patient_id, doctor_id, appointment_date, 
                         appointment_time, department, reason, 
                         status, token_number) 
VALUES (?, ?, ?, ?, ?, ?, ?, ?);
```

**Implementation Logic:**
```java
public boolean bookAppointment(Appointment appt) {
    // Step 1: Validate appointment input (date, time, department)
    // Step 2: Check if patient exists in database
    if (!isPatientExists(appt.getPatientId())) {
        return false;  // Patient not found
    }
    
    // Step 3: Check if doctor exists in database
    if (!isDoctorExists(appt.getDoctorId())) {
        return false;  // Doctor not found
    }
    
    // Step 4: Prevent duplicate appointments
    // (Doctor can't have two appointments at same time)
    if (isDuplicateAppointment(doctorId, date, time)) {
        return false;  // Slot already booked
    }
    
    // Step 5: Auto-generate token number based on date
    int tokenNumber = generateTokenNumber(date);
    
    // Step 6: Insert into database with all validations passed
    String sql = "INSERT INTO appointments (...) VALUES (...)";
    PreparedStatement pst = conn.prepareStatement(sql);
    pst.setInt(1, patientId);
    pst.setInt(2, doctorId);
    pst.setDate(3, appointmentDate);
    pst.setTime(4, appointmentTime);
    // ... set other parameters
    
    // Step 7: Get auto-generated appointment ID
    ResultSet rs = pst.getGeneratedKeys();
    return true;
}
```

**Key Validations:**
- ✅ Patient exists in database
- ✅ Doctor exists in database
- ✅ Appointment date is not in the past
- ✅ Doctor is not already booked at that time
- ✅ Appointment reason is provided

#### 2. **DUPLICATE APPOINTMENT PREVENTION**

**SQL Query:**
```sql
SELECT COUNT(*) FROM appointments 
WHERE doctor_id = ? AND appointment_date = ? 
      AND appointment_time = ?;
```

**Logic:**
- Query database for same doctor + same date + same time
- If count > 0, appointment slot is already taken
- Prevents overbooking

#### 3. **TOKEN NUMBER GENERATION**

**Algorithm:**
```
Token Number = (Count of appointments on same date) + 1

Example:
- Date: 2026-04-28
- 1st appointment token = 1
- 2nd appointment token = 2
- 3rd appointment token = 3
```

**SQL Query:**
```sql
SELECT COUNT(*) FROM appointments 
WHERE appointment_date = ? AND status != 'Cancelled';
```

#### 4. **GET ALL APPOINTMENTS**

**SQL Query:**
```sql
SELECT a.*, p.first_name, p.last_name, d.name 
FROM appointments a
JOIN patients p ON a.patient_id = p.patient_id
JOIN doctors d ON a.doctor_id = d.doctor_id
ORDER BY a.appointment_date DESC, a.appointment_time DESC;
```

**Returns:** List with patient name, doctor name, and appointment details

#### 5. **UPDATE APPOINTMENT STATUS**

**SQL Query:**
```sql
UPDATE appointments 
SET status = ? 
WHERE appointment_id = ?;
```

**Valid Status Values:**
- `Scheduled` - Upcoming appointment
- `Completed` - Appointment finished
- `Cancelled` - Appointment cancelled
- `Pending` - Awaiting confirmation

### Appointment Model Class

```java
public class Appointment {
    // Properties
    private int appointmentId;
    private int patientId;
    private int doctorId;
    private String patientName;      // Fetched from patient table
    private String doctorName;       // Fetched from doctor table
    private Date appointmentDate;    // YYYY-MM-DD
    private Time appointmentTime;    // HH:MM:SS
    private String department;       // Cardiology, Neurology, etc.
    private String reason;           // Reason for visit
    private String status;           // Scheduled/Completed/Cancelled/Pending
    private int tokenNumber;         // Auto-generated
    private String notes;            // Additional notes
    
    // Constructors
    public Appointment(int patientId, int doctorId, Date appointmentDate,
                       Time appointmentTime, String department, String reason)
    
    public Appointment(int appointmentId, int patientId, int doctorId,
                       String patientName, String doctorName,
                       Date appointmentDate, Time appointmentTime,
                       String department, String reason, String status,
                       int tokenNumber, String notes)
    
    // Getters and Setters
}
```

### Data Structure Used

**ArrayList<Appointment> appointmentList**
- Stores appointments in sequential order
- Supports fast random access by index
- Used for displaying appointment lists
- Benefit: Fast iteration, searchable

---

## Module 3: Doctor Management

### Module Overview

The Doctor Management module handles doctor profiles, specializations, and consultation fees.

### Database Operations

#### 1. **ADD DOCTOR**

**SQL Query:**
```sql
INSERT INTO doctors (name, specialization, phone, consultation_fee) 
VALUES (?, ?, ?, ?);
```

#### 2. **GET ALL DOCTORS**

**SQL Query:**
```sql
SELECT * FROM doctors ORDER BY specialization, name;
```

#### 3. **UPDATE DOCTOR**

**SQL Query:**
```sql
UPDATE doctors 
SET name=?, specialization=?, phone=?, consultation_fee=?
WHERE doctor_id = ?;
```

#### 4. **GET DOCTORS BY SPECIALIZATION**

**SQL Query:**
```sql
SELECT * FROM doctors 
WHERE specialization = ?;
```

**Usage:** Filter doctors in appointment booking form

#### 5. **GET TOTAL DOCTORS**

**SQL Query:**
```sql
SELECT COUNT(*) FROM doctors;
```

### Doctor Model Class

```java
public class Doctor {
    private int doctorId;
    private String name;
    private String specialization;      // Cardiology, Neurology, etc.
    private String phone;               // Contact number
    private double consultationFee;     // Consultation charges
    private Date createdAt;
    
    // Constructors, Getters, Setters
}
```

### Common Specializations

- Cardiology (Heart & Cardiovascular)
- Neurology (Nervous system, Brain)
- Orthopedics (Bones & Joints)
- Pediatrics (Children)
- Psychiatry (Mental Health)
- General Practice (Family Medicine)

---

## Module 4: Billing System

### Module Overview

The Billing module generates invoices and tracks payment information for patients.

### Database Operations

#### 1. **CREATE BILL**

**SQL Query:**
```sql
INSERT INTO bills (patient_id, doctor_id, appointment_id, 
                   doctor_fee, medicine_charges, room_charges, 
                   other_charges, total_amount) 
VALUES (?, ?, ?, ?, ?, ?, ?, ?);
```

**Calculation:**
```
Total Amount = Doctor Fee + Medicine Charges + Room Charges + Other Charges
```

#### 2. **GET BILL**

**SQL Query:**
```sql
SELECT b.*, p.first_name, p.last_name, d.name, a.reason
FROM bills b
JOIN patients p ON b.patient_id = p.patient_id
JOIN doctors d ON b.doctor_id = d.doctor_id
LEFT JOIN appointments a ON b.appointment_id = a.appointment_id
WHERE b.bill_id = ?;
```

#### 3. **GET PATIENT BILLS**

**SQL Query:**
```sql
SELECT * FROM bills 
WHERE patient_id = ? 
ORDER BY bill_date DESC;
```

#### 4. **GET TOTAL BILL AMOUNT**

**SQL Query:**
```sql
SELECT SUM(total_amount) FROM bills 
WHERE patient_id = ?;
```

### Bill Model Class

```java
public class Bill {
    private int billId;
    private int patientId;
    private int doctorId;
    private int appointmentId;        // Optional, can be NULL
    private double doctorFee;         // Consultation fee
    private double medicineCharges;   // Medicine costs
    private double roomCharges;       // Hospital room costs
    private double otherCharges;      // Other miscellaneous charges
    private double totalAmount;       // Sum of all charges
    private Date billDate;
    
    // Constructors, Getters, Setters
}
```

### Bill Components

| Component | Example Amount | Description |
|-----------|-----------------|-------------|
| Doctor Fee | ₹500 | Consultation/treatment fee |
| Medicine Charges | ₹2,000 | Medications prescribed |
| Room Charges | ₹5,000 | Hospital bed/room charges |
| Other Charges | ₹500 | Lab tests, scans, etc. |
| **Total Amount** | **₹8,000** | Sum of all above |

---

## Java Swing Frontend Implementation

### Overview of Java Swing Components

**Java Swing** is a GUI toolkit that provides components for building desktop applications.

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
```

### 1. Main Application Window (MainDashboard)

#### Class Structure

```java
public class MainDashboard extends JFrame {
    // DAO Objects
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;
    private DoctorDAO doctorDAO;
    private BillDAO billDAO;
    
    // GUI Components
    private JPanel contentArea;        // Main content with CardLayout
    private JLabel statusLabel;        // Status bar
    private JLabel lblPatients;        // Patient count
    private JLabel lblAppointments;    // Appointment count
    
    // Theme Colors (Bombay Hospital Branding)
    private static final Color MAROON = new Color(139, 0, 0);
    private static final Color LIGHT_MAROON = new Color(180, 30, 30);
    private static final Color NAVY = new Color(25, 50, 100);
    private static final Color WHITE_BG = new Color(255, 255, 255);
    private static final Color LIGHT_GRAY = new Color(245, 248, 252);
}
```

#### Key Swing Features Used

**1. JFrame - Main Window Container**
```java
public class MainDashboard extends JFrame {
    public MainDashboard() {
        setTitle("Hospital Management System — Bombay Hospital");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);                    // Window size
        setLocationRelativeTo(null);           // Center on screen
        setLayout(new BorderLayout());         // Layout manager
        setVisible(true);                      // Show window
    }
}
```

**Properties:**
- `setTitle()` - Window title
- `setDefaultCloseOperation()` - What happens when window closes
- `setSize()` - Width and height in pixels
- `setLocationRelativeTo(null)` - Centers on screen
- `setLayout()` - Manages component positioning

**2. CardLayout - Panel Switching**

Purpose: Switch between different panels in the same space

```java
JPanel contentArea = new JPanel(new CardLayout());

// Add multiple panels with unique names
contentArea.add(dashboardPanel, "📋 Dashboard");
contentArea.add(patientForm, "➕ Add Patient");
contentArea.add(appointmentForm, "📅 Appointments");
contentArea.add(billingForm, "💳 Billing");

// Switch to specific panel
CardLayout cl = (CardLayout) contentArea.getLayout();
cl.show(contentArea, "📋 Dashboard");
```

**Benefits:**
- Only one panel visible at a time
- Memory efficient
- Smooth navigation

**3. BorderLayout - Panel Organization**

```
┌──────────────────────────────────────┐
│           NORTH (Header)              │
├──────────────────────────────────────┤
│ WEST │                             │ EAST │
│ (Nav)│      CENTER (Content)       │ (Side) │
│      │                             │        │
├──────────────────────────────────────┤
│           SOUTH (Status)             │
└──────────────────────────────────────┘
```

```java
JFrame frame = new JFrame();
frame.setLayout(new BorderLayout());

frame.add(headerPanel, BorderLayout.NORTH);     // Top
frame.add(contentArea, BorderLayout.CENTER);    // Middle (main content)
frame.add(statusBar, BorderLayout.SOUTH);       // Bottom
```

**4. GridLayout - Uniform Grid**

```java
// Create 2x3 grid (2 rows, 3 columns)
JPanel gridPanel = new JPanel(new GridLayout(2, 3, 10, 10));

// Add components - automatically fills grid
gridPanel.add(button1);
gridPanel.add(button2);
gridPanel.add(button3);
gridPanel.add(button4);
gridPanel.add(button5);
gridPanel.add(button6);
```

**5. GridBagLayout - Complex Forms**

```java
JPanel formPanel = new JPanel(new GridBagLayout());
GridBagConstraints gbc = new GridBagConstraints();

// Row 0
gbc.gridx = 0; gbc.gridy = 0;
formPanel.add(new JLabel("First Name"), gbc);
gbc.gridx = 1;
formPanel.add(txtFirstName, gbc);

// Row 1
gbc.gridx = 0; gbc.gridy = 1;
formPanel.add(new JLabel("Age"), gbc);
gbc.gridx = 1;
formPanel.add(txtAge, gbc);
```

### 2. Input Components (Text Fields, Combo Boxes)

#### JTextField - Single Line Text Input

```java
JTextField txtPhone = new JTextField(15);  // 15 columns width
txtPhone.setText("9876543210");            // Set default value
String phone = txtPhone.getText();         // Get value
txtPhone.setToolTipText("Enter 10-digit phone");
txtPhone.setEditable(false);               // Read-only
```

#### JTextArea - Multi-line Text Input

```java
JTextArea txtAddress = new JTextArea(3, 30);  // 3 rows, 30 columns
txtAddress.setLineWrap(true);                 // Wrap long lines
txtAddress.setWrapStyleWord(true);            // Wrap at word boundaries

// Add scrollbar to textarea
JScrollPane scrollPane = new JScrollPane(txtAddress);
```

#### JComboBox - Dropdown Selection

```java
String[] genders = {"Male", "Female", "Other"};
JComboBox<String> cbGender = new JComboBox<>(genders);

// Get selected value
String selected = (String) cbGender.getSelectedItem();

// Set selected value
cbGender.setSelectedItem("Female");

// Add listener for change events
cbGender.addActionListener(e -> {
    String selected = (String) cbGender.getSelectedItem();
    System.out.println("Selected: " + selected);
});
```

#### JLabel - Display Text/Images

```java
JLabel lblTitle = new JLabel("Hospital Management System");
lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
lblTitle.setForeground(new Color(139, 0, 0));  // Maroon text
lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
```

### 3. Button and Event Handling

#### JButton - Clickable Buttons

```java
JButton btnAdd = new JButton("Add Patient");
btnAdd.setBackground(new Color(139, 0, 0));      // Maroon background
btnAdd.setForeground(Color.WHITE);               // White text
btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand cursor on hover

// Add click listener
btnAdd.addActionListener(e -> {
    // Action when button is clicked
    String firstName = txtFirstName.getText();
    if (firstName.isEmpty()) {
        JOptionPane.showMessageDialog(this, "First name is required!");
    } else {
        addPatientToDatabase(firstName);
    }
});
```

#### Event Handling Pattern

```java
button.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Code executed when button is clicked
    }
});

// OR using Lambda (Java 8+)
button.addActionListener(e -> {
    // Code executed when button is clicked
});
```

### 4. Dialog Boxes

#### JOptionPane - Simple Dialogs

**Message Dialog:**
```java
JOptionPane.showMessageDialog(this, "Patient added successfully!");
JOptionPane.showMessageDialog(this, "Error: Invalid phone number!", 
                             "Error", JOptionPane.ERROR_MESSAGE);
```

**Confirmation Dialog:**
```java
int response = JOptionPane.showConfirmDialog(this, 
    "Are you sure you want to delete?",
    "Confirm", 
    JOptionPane.YES_NO_OPTION);

if (response == JOptionPane.YES_OPTION) {
    deletePatient();
}
```

**Input Dialog:**
```java
String input = JOptionPane.showInputDialog(this, "Enter patient name:");
```

### 5. Table Display

#### JTable - Data Grid

```java
// Column headers
String[] columns = {"Patient ID", "Name", "Phone", "Status"};

// Sample data
Object[][] data = {
    {1, "Ravi Sharma", "9876543210", "Active"},
    {2, "Priya Gupta", "9876543211", "Active"},
    {3, "Amit Kumar", "9876543212", "Discharged"}
};

// Create table
JTable table = new JTable(data, columns);
JScrollPane scrollPane = new JScrollPane(table);

// Make columns non-editable
table.setDefaultEditor(Object.class, null);

// Add scroll pane to panel
panel.add(scrollPane);
```

### 6. List Display (JList)

```java
DefaultListModel<String> listModel = new DefaultListModel<>();
listModel.addElement("Patient 1");
listModel.addElement("Patient 2");
listModel.addElement("Patient 3");

JList<String> list = new JList<>(listModel);
list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

// Get selected item
String selected = list.getSelectedValue();

// Add listener
list.addListSelectionListener(e -> {
    String selected = list.getSelectedValue();
    System.out.println("Selected: " + selected);
});
```

### 7. Custom Colors and Fonts

#### Font Configuration

```java
// Create custom font
Font boldFont = new Font("Segoe UI", Font.BOLD, 18);
Font regularFont = new Font("Arial", Font.PLAIN, 12);
Font italicFont = new Font("Times New Roman", Font.ITALIC, 14);

// Apply to components
lblTitle.setFont(boldFont);
lblTitle.setForeground(new Color(139, 0, 0));  // Maroon
```

#### Color Scheme (Bombay Hospital)

```java
// Professional Hospital Colors
private static final Color MAROON = new Color(139, 0, 0);        // #8B0000
private static final Color LIGHT_MAROON = new Color(180, 30, 30);
private static final Color NAVY = new Color(25, 50, 100);
private static final Color WHITE_BG = new Color(255, 255, 255);
private static final Color LIGHT_GRAY = new Color(245, 248, 252);
private static final Color SUCCESS_GREEN = new Color(34, 139, 34);
private static final Color ERROR_RED = new Color(220, 20, 60);
```

### 8. Look and Feel Configuration

```java
// In Main.java
public static void main(String[] args) {
    try {
        // Set Nimbus Look and Feel (modern appearance)
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    // Run GUI on Event Dispatch Thread (thread-safe)
    SwingUtilities.invokeLater(() -> new MainDashboard());
}
```

**Why SwingUtilities.invokeLater()?**
- Ensures GUI components are created on Event Dispatch Thread
- Prevents threading issues
- Standard practice for Swing applications

### Complete Form Example: Add Patient Form

```java
public class AddPatientForm extends JPanel {
    // Components
    private JTextField txtFirst, txtLast, txtAge, txtPhone, txtEmail;
    private JComboBox<String> cbGender, cbBlood, cbStatus;
    private JTextArea txtAddress, txtHistory;
    private JButton btnAdd, btnClear;
    
    public AddPatientForm(PatientDAO dao, MainDashboard dashboard) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 252));
        
        // Header
        JLabel heading = new JLabel("➕ Register New Patient");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(heading, BorderLayout.NORTH);
        
        // Form Panel with GridBagLayout
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // First Name
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("First Name *"), gbc);
        gbc.gridx = 1;
        txtFirst = new JTextField(15);
        form.add(txtFirst, gbc);
        
        // Last Name
        gbc.gridx = 2;
        form.add(new JLabel("Last Name *"), gbc);
        gbc.gridx = 3;
        txtLast = new JTextField(15);
        form.add(txtLast, gbc);
        
        // Age
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Age *"), gbc);
        gbc.gridx = 1;
        txtAge = new JTextField(15);
        form.add(txtAge, gbc);
        
        // Gender
        gbc.gridx = 2;
        form.add(new JLabel("Gender *"), gbc);
        gbc.gridx = 3;
        cbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        form.add(cbGender, gbc);
        
        // Add button
        JButton btnAdd = new JButton("Add Patient");
        btnAdd.addActionListener(e -> {
            // Collect form data
            String firstName = txtFirst.getText().trim();
            String lastName = txtLast.getText().trim();
            int age = Integer.parseInt(txtAge.getText());
            String gender = (String) cbGender.getSelectedItem();
            
            // Create Patient object
            Patient patient = new Patient(firstName, lastName, age, 
                                         gender, ...);
            
            // Add to database via DAO
            if (dao.addPatient(patient)) {
                JOptionPane.showMessageDialog(this, "Patient added!");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Error adding patient!", 
                                             "Error", 
                                             JOptionPane.ERROR_MESSAGE);
            }
        });
        
        form.add(btnAdd, ...);
        add(form, BorderLayout.CENTER);
    }
}
```

---

## Data Access Layer (DAO Pattern)

### What is DAO Pattern?

DAO (Data Access Object) pattern separates database operations from business logic.

```
┌──────────────────┐
│   UI Layer       │  (JFrames, JPanels)
└────────┬─────────┘
         │
┌────────▼─────────┐
│   DAO Layer      │  (PatientDAO, AppointmentDAO)
│ - CRUD operations│
│ - SQL queries    │
└────────┬─────────┘
         │
┌────────▼──────────────┐
│   Database Layer      │  (MySQL)
│ - Tables             │
│ - Data storage       │
└───────────────────────┘
```

### PatientDAO Implementation

```java
public class PatientDAO {
    // In-memory queue for caching
    private LinkedList<Patient> patientQueue = new LinkedList<>();
    
    /**
     * Add patient to database with validation
     */
    public boolean addPatient(Patient patient) {
        // Validation
        if (!validatePatientInput(patient)) return false;
        if (isPhoneExists(patient.getPhone())) return false;
        
        // SQL INSERT
        String sql = "INSERT INTO patients (...) VALUES (?, ?, ...)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, 
                                    Statement.RETURN_GENERATED_KEYS)) {
            
            // Safe parameter binding
            pst.setString(1, patient.getFirstName());
            pst.setString(2, patient.getLastName());
            pst.setInt(3, patient.getAge());
            // ... more parameters
            
            // Execute and get generated ID
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    int patientId = rs.getInt(1);
                    patient.setPatientId(patientId);
                    patientQueue.addLast(patient);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get all patients with JOIN query
     */
    public ArrayList<Patient> getAllPatients() {
        ArrayList<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY patient_id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Patient p = new Patient(
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
                patients.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching patients: " + e.getMessage());
        }
        return patients;
    }
    
    /**
     * Search patients by name or phone
     */
    public ArrayList<Patient> searchPatients(String keyword) {
        ArrayList<Patient> results = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE " +
                     "first_name LIKE ? OR " +
                     "last_name LIKE ? OR " +
                     "phone LIKE ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            String searchTerm = "%" + keyword + "%";
            pst.setString(1, searchTerm);
            pst.setString(2, searchTerm);
            pst.setString(3, searchTerm);
            
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                // Create Patient object from ResultSet
                results.add(createPatientFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Search error: " + e.getMessage());
        }
        return results;
    }
}
```

### AppointmentDAO Implementation

```java
public class AppointmentDAO {
    // In-memory list for appointments
    private ArrayList<Appointment> appointmentList = new ArrayList<>();
    
    /**
     * Book appointment with comprehensive validation
     */
    public boolean bookAppointment(Appointment appt) {
        // Validation chain
        if (!validateAppointmentInput(appt)) return false;
        if (!isPatientExists(appt.getPatientId())) return false;
        if (!isDoctorExists(appt.getDoctorId())) return false;
        if (isDuplicateAppointment(appt.getDoctorId(), 
                                   appt.getAppointmentDate(), 
                                   appt.getAppointmentTime())) 
            return false;
        
        // Auto-generate token
        int tokenNumber = generateTokenNumber(appt.getAppointmentDate());
        
        // Insert to database
        String sql = "INSERT INTO appointments (...) VALUES (...)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql,
                                    Statement.RETURN_GENERATED_KEYS)) {
            
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
                appt.setTokenNumber(tokenNumber);
                appointmentList.add(appt);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Booking error: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Generate token number for appointment
     */
    private int generateTokenNumber(Date date) {
        String sql = "SELECT COUNT(*) as count FROM appointments " +
                     "WHERE appointment_date = ? " +
                     "AND status != 'Cancelled'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setDate(1, date);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") + 1;  // Next token number
            }
        } catch (SQLException e) {
            System.err.println("Token generation error: " + e.getMessage());
        }
        return 1;
    }
}
```

### Key DAO Principles

1. **Separation of Concerns**
   - DAO handles only database operations
   - UI doesn't contain SQL queries
   - Easy to change database without changing UI

2. **Reusability**
   - Same DAO can be used in multiple UI forms
   - Same methods work for different operations

3. **Testability**
   - Easy to mock/test DAO independently
   - Can test without actual database

4. **Exception Handling**
   - DAO catches all database exceptions
   - UI only deals with boolean return values
   - Error messages logged systematically

---

## Validation & Security

### Input Validation Utility

```java
public class ValidationUtil {
    
    // Email validation
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    // Phone validation (10 digits)
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9]{10}$");
    
    // Name validation (2-50 chars, letters/space/dash/apostrophe)
    private static final Pattern NAME_PATTERN = 
        Pattern.compile("^[a-zA-Z\\s'-]{2,50}$");
    
    /**
     * Validate name
     */
    public static boolean isValidName(String firstName) {
        return firstName != null && 
               NAME_PATTERN.matcher(firstName).matches();
    }
    
    /**
     * Validate phone (10 digits, ignores formatting)
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String cleaned = phone.replaceAll("[\\s\\-()]+", "");
        return cleaned.matches("^[0-9]{10}$");
    }
    
    /**
     * Validate email address
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate age (1-150)
     */
    public static boolean isValidAge(int age) {
        return age > 0 && age < 150;
    }
    
    /**
     * Validate blood group (8 types)
     */
    public static boolean isValidBloodGroup(String bloodGroup) {
        String[] valid = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        return Arrays.asList(valid).contains(bloodGroup);
    }
    
    /**
     * Validate gender
     */
    public static boolean isValidGender(String gender) {
        return "Male".equals(gender) || 
               "Female".equals(gender) || 
               "Other".equals(gender);
    }
    
    /**
     * Sanitize string input
     */
    public static String sanitizeString(String str) {
        if (str == null) return null;
        String trimmed = str.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
```

### Security Measures

#### 1. **Prepared Statements (SQL Injection Prevention)**

❌ **Unsafe (vulnerable to SQL injection):**
```java
String sql = "SELECT * FROM patients WHERE phone = '" + phone + "'";
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery(sql);

// Attacker input: ' OR '1'='1
// Executes: SELECT * FROM patients WHERE phone = '' OR '1'='1'
// Returns ALL patients!
```

✅ **Secure (using PreparedStatement):**
```java
String sql = "SELECT * FROM patients WHERE phone = ?";
PreparedStatement pst = conn.prepareStatement(sql);
pst.setString(1, phone);  // Parameter safely bound
ResultSet rs = pst.executeQuery();
```

#### 2. **Duplicate Prevention**

```java
// Check if phone already exists
private boolean isPhoneExists(String phone) {
    String sql = "SELECT COUNT(*) FROM patients WHERE phone = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pst = conn.prepareStatement(sql)) {
        
        pst.setString(1, phone);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}
```

#### 3. **Database Constraints**

```sql
-- UNIQUE constraint on phone (database level)
phone VARCHAR(15) NOT NULL UNIQUE

-- CHECK constraint on age (database level)
age INT NOT NULL CHECK (age > 0 AND age < 150)

-- ENUM for valid values (database level)
gender ENUM('Male', 'Female', 'Other') NOT NULL
```

#### 4. **Input Sanitization**

```java
// Remove leading/trailing whitespace
String sanitized = input.trim();

// Validate length
if (sanitized.length() < 2 || sanitized.length() > 50) {
    return false;
}

// Remove special characters
String cleaned = sanitized.replaceAll("[^a-zA-Z\\s'-]", "");
```

---

## Project Structure

```
Hospital Management/
│
├── src/
│   ├── Main.java                          # Application entry point
│   │   └── UIManager.setLookAndFeel()     # Set Nimbus theme
│   │   └── SwingUtilities.invokeLater()   # Thread-safe GUI startup
│   │
│   ├── config/
│   │   └── db.properties                  # Database configuration
│   │       └── URL=jdbc:mysql://localhost:3306/hospital_db
│   │       └── USER=root
│   │       └── PASSWORD=your_password
│   │
│   ├── db/
│   │   └── DBConnection.java              # Singleton connection manager
│   │       └── getConnection()            # Get active connection
│   │       └── loadConfiguration()        # Load db.properties
│   │
│   ├── models/
│   │   ├── Patient.java                   # Patient POJO
│   │   ├── Appointment.java               # Appointment POJO
│   │   ├── Doctor.java                    # Doctor POJO
│   │   └── Bill.java                      # Bill POJO
│   │
│   ├── dao/
│   │   ├── PatientDAO.java                # Patient database operations
│   │   │   └── addPatient()               # Add with validation
│   │   │   └── getAllPatients()           # Get all patients
│   │   │   └── searchPatients()           # Search by name/phone
│   │   │   └── updatePatient()            # Update patient info
│   │   │
│   │   ├── AppointmentDAO.java            # Appointment operations
│   │   │   └── bookAppointment()          # Book with validation
│   │   │   └── getAllAppointments()       # Get all appointments
│   │   │   └── generateTokenNumber()      # Auto token generation
│   │   │
│   │   ├── DoctorDAO.java                 # Doctor operations
│   │   │   └── addDoctor()
│   │   │   └── getAllDoctors()
│   │   │
│   │   └── BillDAO.java                   # Billing operations
│   │       └── createBill()
│   │       └── getBill()
│   │
│   ├── utils/
│   │   └── ValidationUtil.java            # Input validation utilities
│   │       └── isValidName()              # Name validation
│   │       └── isValidPhone()             # Phone validation
│   │       └── isValidEmail()             # Email validation
│   │       └── isValidAge()               # Age validation
│   │       └── isValidBloodGroup()        # Blood group validation
│   │
│   └── ui/
│       ├── MainDashboard.java             # Main window (1400x800)
│       │   └── CardLayout (6 panels)
│       │   └── Color scheme: Maroon (#8B0000)
│       │
│       ├── AddPatientForm.java            # Patient registration form
│       │   └── GridBagLayout
│       │   └── Input: First Name, Last Name, Age, Gender, Blood Group, Phone, Email, Address, Medical History, Status
│       │
│       ├── AddPatientFormWithEvents.java  # Enhanced with listeners
│       │
│       ├── AppointmentForm.java           # Appointment booking form
│       │   └── GridBagLayout
│       │   └── Input: Patient, Doctor, Date, Time, Department, Reason
│       │   └── Auto-generates token number
│       │
│       ├── AppointmentFormWithEvents.java # Enhanced version
│       │
│       ├── DoctorForm.java                # Doctor management panel
│       │
│       ├── BillingForm.java               # Billing system panel
│       │   └── Calculate: Doctor Fee + Medicine + Room + Other
│       │   └── Generate invoice/bill
│       │
│       └── (More UI forms...)
│
├── database/
│   └── hospital_db.sql                    # Complete database schema
│       └── CREATE DATABASE hospital_db
│       └── Table: doctors
│       └── Table: patients
│       └── Table: appointments
│       └── Table: bills
│       └── Sample data and indices
│
├── bin/                                   # Compiled .class files
│   ├── config/
│   │   └── db.properties
│   ├── dao/
│   │   ├── PatientDAO.class
│   │   ├── AppointmentDAO.class
│   │   └── ...
│   ├── db/
│   ├── models/
│   ├── ui/
│   ├── utils/
│   └── Main.class
│
├── lib/
│   └── mysql-connector-j-9.6.0/          # JDBC Driver
│       ├── mysql-connector-j-9.6.0.jar   # Main JAR file
│       └── ...
│
├── .vscode/
│   └── settings.json                      # VS Code Java settings
│       └── "java.project.referencedLibraries"
│       └── "java.home"
│
└── [Documentation Files]
    ├── README.md                          # Quick start guide
    ├── PROJECT_COMPREHENSIVE_REPORT.md    # Full documentation
    ├── QUICKSTART.md                      # Setup instructions
    ├── SETUP_GUIDE.md                     # Detailed setup
    ├── ERD.md                             # Entity relationship diagram
    ├── IMPLEMENTATION_SUMMARY.md          # Implementation details
    ├── ADD_PATIENT_MODULE_REPORT.md       # Patient module docs
    ├── ADD_APPOINTMENT_MODULE_REPORT.md   # Appointment module docs
    ├── CODE_EXAMPLES.md                   # Code examples
    ├── SQL_QUERIES_REFERENCE.sql          # SQL reference
    └── PROJECT_PRESENTATION.md            # This file!
```

---

## Installation & Execution

### Prerequisites

1. **Java JDK 11+**
   ```bash
   java -version
   ```

2. **MySQL Server 5.7+**
   ```bash
   mysql --version
   ```

3. **VS Code** (or any Java IDE)

### Step 1: Set Up Database

**Open MySQL and run:**
```bash
mysql -u root -p
```

**Execute SQL file:**
```sql
SOURCE /path/to/hospital_db.sql;
```

Or copy entire content of `database/hospital_db.sql` and execute in MySQL client.

**Verify tables:**
```sql
USE hospital_db;
SHOW TABLES;
DESC patients;
DESC appointments;
DESC doctors;
DESC bills;
```

### Step 2: Configure Database Connection

**Edit file: `src/config/db.properties`**
```properties
# MySQL Connection Properties
DB_URL=jdbc:mysql://localhost:3306/hospital_db
DB_USER=root
DB_PASSWORD=your_mysql_password
```

### Step 3: Compile Project

**Using terminal (macOS/Linux):**
```bash
cd /Users/devangsaini/Desktop/Hospital\ Management

# Compile all Java files
javac -cp lib/mysql-connector-j-9.6.0/mysql-connector-j-9.6.0.jar \
      -d bin \
      src/db/*.java \
      src/models/*.java \
      src/dao/*.java \
      src/utils/*.java \
      src/ui/*.java \
      src/Main.java
```

### Step 4: Run Application

```bash
java -cp bin:lib/mysql-connector-j-9.6.0/mysql-connector-j-9.6.0.jar Main
```

**Expected Output:**
```
✓ Configuration loaded from: src/config/db.properties
✓ Connected to MySQL Database | Total Patients: 5 | Total Appointments: 8
```

### Troubleshooting

**Problem:** `ClassNotFoundException: com.mysql.cj.jdbc.Driver`  
**Solution:** Ensure mysql-connector-j-9.6.0.jar is in classpath

**Problem:** `Connection refused`  
**Solution:** Verify MySQL is running and credentials are correct

**Problem:** `Database hospital_db does not exist`  
**Solution:** Run hospital_db.sql script in MySQL

---

## Summary

This Hospital Management System demonstrates:

✅ **Object-Oriented Programming** - Models, DAOs, layered architecture  
✅ **Database Design** - Normalization, relationships, constraints  
✅ **Java JDBC** - Connection pooling, prepared statements  
✅ **GUI Development** - Java Swing components, layouts, event handling  
✅ **Input Validation** - Regex patterns, duplicate prevention  
✅ **Security** - SQL injection prevention, parameter binding  
✅ **Professional Design** - Clean code, separation of concerns, reusability  

---

## Presentation Tips

1. **Start with Architecture Diagram** - Show system layers
2. **Explain Database** - Show tables and relationships
3. **Walk Through Each Module** - Patient → Appointment → Doctor → Billing
4. **Show UI Components** - CardLayout, GridBagLayout, event handling
5. **Demonstrate Features** - Live demo if possible
6. **Discuss Validation** - Security and data integrity
7. **Show Code Examples** - Key DAO methods, UI components
8. **Mention Best Practices** - Design patterns, JDBC, error handling

---

**Good luck with your presentation! 🚀**
