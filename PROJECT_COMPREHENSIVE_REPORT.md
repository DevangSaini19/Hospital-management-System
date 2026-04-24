# 🏥 Hospital Management System - Comprehensive Project Report

**Project Date:** April 2026  
**Status:** Production Ready  
**Version:** 1.0  

---

## 📑 Table of Contents

1. [Executive Summary](#executive-summary)
2. [Project Overview](#project-overview)
3. [Technology Stack](#technology-stack)
4. [System Architecture](#system-architecture)
5. [Module Documentation](#module-documentation)
6. [Database Design](#database-design)
7. [Data Access Layer (DAO)](#data-access-layer-dao)
8. [User Interface (Java Swing)](#user-interface-java-swing)
9. [Security & Validation](#security--validation)
10. [Installation & Setup](#installation--setup)
11. [Project Structure](#project-structure)

---

## Executive Summary

The **Hospital Management System (HMS)** is a desktop application designed to streamline hospital operations. It provides comprehensive management of patients, doctors, appointments, and billing with a professional GUI interface and secure MySQL database backend.

### Key Features
- ✅ Patient Registration & Management
- ✅ Appointment Booking & Scheduling
- ✅ Doctor Management & Specializations
- ✅ Billing & Invoice Generation
- ✅ Real-time Data Validation
- ✅ Duplicate Prevention
- ✅ Token Generation for Appointments
- ✅ Comprehensive Search Capabilities

---

## Project Overview

### Purpose
The HMS enables hospitals to efficiently manage:
- Patient information and medical history
- Doctor schedules and availability
- Appointment bookings with token system
- Billing and payment tracking

### Target Users
- Hospital Administrators
- Receptionist Staff
- Doctors
- Billing Department
- Hospital Management

### Core Capabilities
| Feature | Description |
|---------|-------------|
| Patient Module | Add, view, update, search patient records |
| Appointment Module | Schedule appointments, manage availability, token generation |
| Doctor Module | Manage doctors, specializations, consultation fees |
| Billing Module | Generate bills, track payments, invoice management |
| Dashboard | Real-time statistics and quick access |

---

## Technology Stack

### Frontend
| Component | Technology | Version |
|-----------|-----------|---------|
| UI Framework | Java Swing | Java 11+ |
| Look & Feel | Nimbus UI | Built-in |
| Layout Manager | CardLayout, BorderLayout, GridLayout | Standard |

### Backend
| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java Core | JDK 11+ |
| Database Driver | MySQL Connector/J | 9.6.0 |
| JDBC | Java Database Connectivity | Standard |

### Database
| Component | Technology | Version |
|-----------|-----------|---------|
| Database | MySQL | 5.7 LTS or higher |
| Connection Type | TCP/IP | localhost:3306 |
| Encoding | UTF-8 | Standard |

### Build & Execution
| Tool | Usage |
|------|-------|
| Compilation | javac (Java Compiler) |
| Execution | java (Java Runtime) |
| Libraries | mysql-connector-j-9.6.0.jar |

---

## System Architecture

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                     USER INTERFACE LAYER                     │
│  (Java Swing - JFrame, JPanel, JButton, JTable, JComboBox)  │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Dashboard  │  │  Add Patient │  │ Appointment  │      │
│  │              │  │     Form     │  │     Form     │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Billing    │  │    Doctor    │  │  All Records │      │
│  │    Module    │  │   Management │  │     View     │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   BUSINESS LOGIC LAYER                       │
│              (DAO - Data Access Objects)                     │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  PatientDAO  │  │AppointmentDAO│  │  DoctorDAO   │      │
│  │ - addPatient │  │- bookAppointm│  │- addDoctor   │      │
│  │- searchPatient  │- searchAppointm  │- updateFee    │      │
│  │- updatePatient  │- updateStatus │  │- listDoctors │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              VALIDATION LAYER                        │   │
│  │  (ValidationUtil - Input Validation & Sanitization) │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                  DATA ACCESS LAYER                           │
│              (JDBC - Database Connectivity)                  │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐    │
│  │      DBConnection - Connection Management           │    │
│  │  - Load configuration from db.properties or env vars│    │
│  │  - Establish MySQL connection                       │    │
│  │  - Handle connection pooling                        │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   DATABASE LAYER                             │
│                    MySQL Server                              │
│                                                              │
│  ┌──────────┐  ┌───────────┐  ┌──────────────┐  ┌────────┐ │
│  │ patients │  │ doctors   │  │ appointments │  │ bills  │ │
│  ├──────────┤  ├───────────┤  ├──────────────┤  ├────────┤ │
│  │ patient_ │  │ doctor_id │  │ appointment_ │  │ bill_  │ │
│  │ id (PK)  │  │ (PK)      │  │ id (PK)      │  │ id(PK) │ │
│  │ name     │  │ name      │  │ patient_id   │  │patient │ │
│  │ phone    │  │ special.  │  │ (FK)         │  │ (FK)   │ │
│  │ email    │  │ fee       │  │ doctor_id    │  │ doctor │ │
│  │ medical_ │  │ phone     │  │ (FK)         │  │ (FK)   │ │
│  │ history  │  │           │  │ date & time  │  │ amount │ │
│  └──────────┘  └───────────┘  └──────────────┘  └────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### Design Patterns Used

1. **DAO Pattern (Data Access Object)**
   - Separates business logic from database operations
   - Classes: `PatientDAO`, `AppointmentDAO`, `DoctorDAO`, `BillDAO`

2. **Model-View-Controller (MVC)**
   - Models: `Patient.java`, `Appointment.java`, `Doctor.java`, `Bill.java`
   - Views: UI Forms in `ui/` package
   - Controllers: DAOs handle business logic

3. **Singleton Pattern**
   - `DBConnection.getConnection()` - ensures single database connection

4. **Card Layout Pattern**
   - `MainDashboard` uses CardLayout for multi-panel switching

---

## Module Documentation

### 1. Patient Module

#### Purpose
Manage patient information, medical history, and registration.

#### Key Features
- **Add Patient**: Register new patients with validation
- **Search Patient**: Find by name or phone number
- **View All**: List all registered patients
- **Update Patient**: Modify patient information
- **Delete Patient**: Remove patient records

#### Patient Data Fields
| Field | Type | Constraints | Example |
|-------|------|-----------|---------|
| Patient ID | INT | Auto-increment, Primary Key | 1, 2, 3... |
| First Name | VARCHAR(50) | Required, 2-50 chars | John |
| Last Name | VARCHAR(50) | Required, 2-50 chars | Doe |
| Age | INT | Required, 1-149 | 28 |
| Gender | ENUM | Male, Female, Other | Male |
| Blood Group | ENUM | A+, A-, B+, B-, AB+, AB-, O+, O- | O+ |
| Phone | VARCHAR(15) | Required, Unique, 10 digits | 9876543210 |
| Email | VARCHAR(100) | Optional, Valid format | john@email.com |
| Address | TEXT | Optional | 123 Main Street |
| Medical History | TEXT | Optional | Previous surgeries, allergies |
| Status | ENUM | Active, Discharged, Critical | Active |

#### Sample SQL Operations
```sql
-- Add Patient
INSERT INTO patients (first_name, last_name, age, gender, blood_group, 
                     phone, email, address, medical_history, status) 
VALUES ('John', 'Doe', 28, 'Male', 'O+', '9876543210', 
        'john@email.com', '123 Main St', 'No history', 'Active');

-- Search Patient
SELECT * FROM patients WHERE phone = '9876543210';
SELECT * FROM patients WHERE first_name LIKE 'John%' OR last_name LIKE '%Doe';

-- Get All Patients
SELECT * FROM patients ORDER BY patient_id DESC;

-- Update Patient
UPDATE patients SET age = 29, email = 'newemail@email.com' 
WHERE patient_id = 1;

-- Delete Patient
DELETE FROM patients WHERE patient_id = 1;
```

#### Validation Rules
- **Phone**: Exactly 10 digits, must be unique
- **Email**: Valid email format (optional)
- **Name**: 2-50 characters, letters only
- **Age**: 1-149 years
- **Blood Group**: Must match predefined values
- **Gender**: Male, Female, or Other

---

### 2. Appointment Module

#### Purpose
Schedule and manage doctor appointments with automatic token generation.

#### Key Features
- **Book Appointment**: Schedule appointment with validation
- **Token Generation**: Automatic sequential token numbers
- **Duplicate Prevention**: Prevent double-booking same doctor/time
- **Status Management**: Track appointment status (Scheduled, Completed, Cancelled)
- **Search Appointments**: Find by date, patient, or doctor

#### Appointment Data Fields
| Field | Type | Constraints | Example |
|-------|------|-----------|---------|
| Appointment ID | INT | Auto-increment, Primary Key | 1, 2, 3... |
| Patient ID | INT | Foreign Key → patients.patient_id | 1 |
| Doctor ID | INT | Foreign Key → doctors.doctor_id | 1 |
| Appointment Date | DATE | Required, Future date | 2026-04-25 |
| Appointment Time | TIME | Required | 10:00:00 |
| Department | VARCHAR(100) | Required | Cardiology |
| Reason | TEXT | Optional | Regular Checkup |
| Status | ENUM | Scheduled, Completed, Cancelled | Scheduled |
| Token Number | INT | Auto-generated | 1, 2, 3... |

#### Token Generation Logic
```
Token Number = Sequential counter based on appointment date
Example:
- 2026-04-25: Token 1, 2, 3... (resets daily)
- 2026-04-26: Token 1, 2, 3... (resets daily)
```

#### Sample SQL Operations
```sql
-- Book Appointment
INSERT INTO appointments (patient_id, doctor_id, appointment_date, 
                         appointment_time, department, reason, status, token_number)
VALUES (1, 1, '2026-04-25', '10:00:00', 'Cardiology', 
        'Regular Checkup', 'Scheduled', 1);

-- Get All Appointments with Details (JOIN)
SELECT a.appointment_id, p.first_name, p.last_name, d.name, 
       a.appointment_date, a.appointment_time, a.token_number, a.status
FROM appointments a
JOIN patients p ON a.patient_id = p.patient_id
JOIN doctors d ON a.doctor_id = d.doctor_id
ORDER BY a.appointment_date DESC;

-- Search by Date
SELECT * FROM appointments 
WHERE appointment_date = '2026-04-25' 
ORDER BY appointment_time;

-- Update Appointment Status
UPDATE appointments SET status = 'Completed' 
WHERE appointment_id = 1;

-- Check Doctor Availability
SELECT * FROM appointments 
WHERE doctor_id = 1 AND appointment_date = '2026-04-25' 
AND appointment_time = '10:00:00';
```

#### Validation Rules
- **Patient ID**: Must exist in patients table
- **Doctor ID**: Must exist in doctors table
- **Date**: Must be current or future date
- **Time**: Must be in valid hospital hours
- **Duplicate Check**: No same doctor + date + time combination

---

### 3. Doctor Module

#### Purpose
Manage doctor information, specializations, and consultation fees.

#### Key Features
- **Add Doctor**: Register new doctors
- **View All Doctors**: List with specializations
- **Update Fees**: Adjust consultation charges
- **Filter by Specialization**: Find doctors by department

#### Doctor Data Fields
| Field | Type | Constraints | Example |
|-------|------|-----------|---------|
| Doctor ID | INT | Auto-increment, Primary Key | 1, 2... |
| Name | VARCHAR(100) | Required | Dr. Ramesh Sharma |
| Specialization | VARCHAR(100) | Required | Cardiology |
| Phone | VARCHAR(15) | Optional | 9876500001 |
| Consultation Fee | DOUBLE | Required, > 0 | 500.00 |

#### Sample SQL Operations
```sql
-- Add Doctor
INSERT INTO doctors (name, specialization, phone, consultation_fee)
VALUES ('Dr. Ramesh Sharma', 'Cardiology', '9876500001', 500.0);

-- Get All Doctors
SELECT * FROM doctors ORDER BY specialization;

-- Find by Specialization
SELECT * FROM doctors WHERE specialization = 'Cardiology';

-- Update Consultation Fee
UPDATE doctors SET consultation_fee = 600.0 WHERE doctor_id = 1;
```

---

### 4. Billing Module

#### Purpose
Generate bills and track payments for appointments and services.

#### Key Features
- **Generate Bill**: Create bill for appointment
- **Calculate Total**: Sum of all charges
- **Payment Tracking**: Record payment status
- **Invoice Management**: Generate receipts

#### Bill Data Fields
| Field | Type | Constraints | Example |
|-------|------|-----------|---------|
| Bill ID | INT | Auto-increment, Primary Key | 1, 2... |
| Patient ID | INT | Foreign Key → patients.patient_id | 1 |
| Doctor ID | INT | Foreign Key → doctors.doctor_id | 1 |
| Appointment ID | INT | Foreign Key → appointments.appointment_id | 1 |
| Doctor Fee | DOUBLE | Required, Default 0 | 500.00 |
| Medicine Charges | DOUBLE | Required, Default 0 | 150.00 |
| Room Charges | DOUBLE | Required, Default 0 | 1000.00 |
| Other Charges | DOUBLE | Required, Default 0 | 50.00 |
| Total Amount | DOUBLE | Calculated | 1700.00 |
| Bill Date | TIMESTAMP | Auto-set to current | 2026-04-23 10:30:45 |

#### Billing Calculation
```
Total Amount = Doctor Fee + Medicine Charges + Room Charges + Other Charges
```

#### Sample SQL Operations
```sql
-- Generate Bill
INSERT INTO bills (patient_id, doctor_id, appointment_id, 
                  doctor_fee, medicine_charges, room_charges, 
                  other_charges, total_amount)
VALUES (1, 1, 1, 500.00, 150.00, 1000.00, 50.00, 1700.00);

-- Get All Bills with Details
SELECT b.bill_id, p.first_name, p.last_name, d.name, 
       b.doctor_fee, b.medicine_charges, b.room_charges, 
       b.other_charges, b.total_amount, b.bill_date
FROM bills b
JOIN patients p ON b.patient_id = p.patient_id
JOIN doctors d ON b.doctor_id = d.doctor_id
ORDER BY b.bill_date DESC;

-- Get Bills by Patient
SELECT * FROM bills WHERE patient_id = 1 ORDER BY bill_date DESC;

-- Total Revenue (by date)
SELECT DATE(bill_date) as date, SUM(total_amount) as daily_revenue
FROM bills
GROUP BY DATE(bill_date)
ORDER BY date DESC;
```

---

## Database Design

### Database Name
`hospital_db`

### Tables Overview

#### 1. patients Table
```sql
CREATE TABLE patients (
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_phone (phone),
    INDEX idx_status (status)
);
```

**Key Points:**
- `patient_id`: Auto-increment primary key
- `phone`: UNIQUE constraint prevents duplicate phone numbers
- `blood_group`: ENUM restricts to valid blood group values
- `age`: CHECK constraint ensures valid age range (1-149)
- Indexes on `phone` and `status` for faster searches

#### 2. doctors Table
```sql
CREATE TABLE doctors (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    consultation_fee DOUBLE NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_specialization (specialization)
);
```

**Key Points:**
- `doctor_id`: Auto-increment primary key
- `consultation_fee`: Default 0, can be updated
- Index on `specialization` for filtering by department

#### 3. appointments Table
```sql
CREATE TABLE appointments (
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
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE,
    INDEX idx_date (appointment_date),
    INDEX idx_doctor_date (doctor_id, appointment_date)
);
```

**Key Points:**
- Foreign key constraints maintain referential integrity
- ON DELETE CASCADE: Automatically deletes appointments if patient/doctor removed
- Composite index on (doctor_id, appointment_date) for finding availability

#### 4. bills Table
```sql
CREATE TABLE bills (
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

**Key Points:**
- Multiple indexes for efficient bill retrieval
- ON DELETE SET NULL for appointment_id allows bills to exist even if appointment deleted

### Entity Relationship Diagram (ERD)

```
┌──────────────────┐         ┌────────────────────┐
│     patients     │         │      doctors       │
├──────────────────┤         ├────────────────────┤
│ patient_id (PK)  │         │ doctor_id (PK)     │
│ first_name       │         │ name               │
│ last_name        │         │ specialization     │
│ age              │         │ phone              │
│ gender           │         │ consultation_fee   │
│ blood_group      │         │ created_at         │
│ phone (UNIQUE)   │         └────────────────────┘
│ email            │                  ▲
│ address          │                  │ (1:N)
│ medical_history  │                  │
│ status           │                  │
│ registration_date│         ┌────────────────────┐
│ created_at       │         │  appointments      │
└──────────────────┘         ├────────────────────┤
         ▲                    │appointment_id (PK) │
         │(1:N)              │patient_id (FK)     │
         │                   │doctor_id (FK)      │
         │                   │appointment_date    │
         │                   │appointment_time    │
    ┌────┴───────┐           │department          │
    │             │           │reason              │
    │             │           │status              │
    │             │           │token_number        │
    │             │           │created_at          │
    │             │           └────────────────────┘
    │             │                    ▲
    │             │                    │ (1:N)
    │             │           ┌────────┴────────┐
    │             │           │                 │
    │             │      ┌─────────────────┐   │
    │             └─────→│      bills      │◄──┘
    │                    ├─────────────────┤
    └───────────────────→│ bill_id (PK)    │
         (1:N)           │patient_id (FK)  │
                         │doctor_id (FK)   │
                         │appointment_id   │
                         │doctor_fee       │
                         │medicine_charges │
                         │room_charges     │
                         │other_charges    │
                         │total_amount     │
                         │bill_date        │
                         └─────────────────┘
```

**Relationships:**
- `1:N` - One Patient can have many Appointments
- `1:N` - One Doctor can have many Appointments
- `1:N` - One Patient can have many Bills
- `1:N` - One Doctor can have many Bills
- `1:N` - One Appointment can have one Bill (optional)

---

## Data Access Layer (DAO)

### DAO Pattern Overview

The DAO pattern separates database operations from business logic, making code more maintainable and testable.

### PatientDAO

**Location:** `src/dao/PatientDAO.java`

**Data Structure:** LinkedList (for queue simulation)

**Key Methods:**

```java
// Add new patient with validation
boolean addPatient(Patient patient)

// Search by phone number
Patient searchByPhone(String phone)

// Search by name
LinkedList<Patient> searchByName(String name)

// Get all patients
LinkedList<Patient> getAllPatients()

// Update patient information
boolean updatePatient(Patient patient)

// Check if phone number already exists
boolean isPhoneExists(String phone)

// Validate patient input
private boolean validatePatientInput(Patient patient)

// Get total patient count
int getTotalPatientCount()
```

**Example Usage:**
```java
PatientDAO dao = new PatientDAO();
Patient patient = new Patient("John", "Doe", 28, "Male", "O+", 
                               "9876543210", "john@email.com", 
                               "123 St", "No history");
if (dao.addPatient(patient)) {
    System.out.println("Patient added with ID: " + patient.getPatientId());
}
```

### AppointmentDAO

**Location:** `src/dao/AppointmentDAO.java`

**Data Structure:** ArrayList

**Key Methods:**

```java
// Book new appointment
boolean bookAppointment(Appointment appt)

// Get all appointments
ArrayList<Appointment> getAllAppointments()

// Search appointments by date
ArrayList<Appointment> searchByDate(String date)

// Check for duplicate appointment
private boolean isDuplicateAppointment(int doctorId, Date date, Time time)

// Validate appointment input
private boolean validateAppointmentInput(Appointment appt)

// Generate token number
private int generateTokenNumber(String date)

// Check if patient exists
private boolean isPatientExists(int patientId)

// Check if doctor exists
private boolean isDoctorExists(int doctorId)

// Get total appointment count
int getTotalAppointmentCount()
```

**Example Usage:**
```java
AppointmentDAO dao = new AppointmentDAO();
Appointment appt = new Appointment(1, 1, Date.valueOf("2026-04-25"), 
                                    Time.valueOf("10:00:00"), 
                                    "Cardiology", "Regular Checkup");
if (dao.bookAppointment(appt)) {
    System.out.println("Appointment booked with Token: " + appt.getTokenNumber());
}
```

### DoctorDAO

**Location:** `src/dao/DoctorDAO.java`

**Key Methods:**

```java
// Add new doctor
boolean addDoctor(Doctor doctor)

// Get all doctors
ArrayList<Doctor> getAllDoctors()

// Get doctors by specialization
ArrayList<Doctor> getDoctorsBySpecialization(String specialization)

// Update consultation fee
boolean updateConsultationFee(int doctorId, double fee)
```

### BillDAO

**Location:** `src/dao/BillDAO.java`

**Key Methods:**

```java
// Generate bill
boolean generateBill(Bill bill)

// Get all bills
ArrayList<Bill> getAllBills()

// Get bills by patient
ArrayList<Bill> getBillsByPatient(int patientId)

// Calculate total for date
double getTotalRevenueByDate(String date)
```

---

## User Interface (Java Swing)

### UI Framework Details

**Framework:** Java Swing (javax.swing)  
**Look & Feel:** Nimbus UI (Modern, professional appearance)  
**Layout Managers:** CardLayout, BorderLayout, GridLayout, FlowLayout

### Main Components

#### 1. MainDashboard (Entry Point)

**Location:** `src/ui/MainDashboard.java`

**Features:**
- Main application window with CardLayout for multi-view navigation
- Professional header with hospital branding
- Sidebar navigation with quick links
- Status bar showing database connection and statistics
- Real-time patient and appointment count

**Color Scheme:**
```
- Primary: Maroon (#8B0000) - Hospital branding
- Secondary: Navy Blue (#193264)
- Background: White (#FFFFFF)
- Hover: Light Maroon (#B41E1E)
```

**Navigation Menu:**
- 📋 Dashboard - Statistics and overview
- ➕ Add Patient - Patient registration form
- 👥 All Patients - View all registered patients
- 📅 Appointments - View all appointments
- 🕐 Book Appointment - Schedule new appointment
- 👨‍⚕️ Doctor Management - Add/manage doctors
- 💳 Billing - Generate and view bills

#### 2. AddPatientForm

**Location:** `src/ui/AddPatientForm.java`

**Components:**
- Text fields for: First Name, Last Name, Email, Address
- Spinner for: Age
- ComboBox for: Gender, Blood Group
- Formatted text field for: Phone (10 digits)
- Text area for: Medical History
- Buttons: Submit, Clear

**Validation Features:**
- Real-time phone format validation
- Email format validation
- Age range validation (1-149)
- Blood group selection validation
- Phone uniqueness check before submission

#### 3. AppointmentFormWithEvents

**Location:** `src/ui/AppointmentFormWithEvents.java`

**Components:**
- ComboBox for: Patient (dropdown from database)
- ComboBox for: Doctor (dropdown from database)
- Date picker for: Appointment Date
- Time picker for: Appointment Time
- ComboBox for: Department
- Text area for: Reason
- Label showing: Token Number (auto-generated)

**Features:**
- Automatic token generation
- Doctor availability checking
- Duplicate appointment prevention
- Real-time validation

#### 4. BillingForm

**Location:** `src/ui/BillingForm.java`

**Components:**
- ComboBox for: Patient
- ComboBox for: Doctor
- ComboBox for: Appointment
- Text fields for: Doctor Fee, Medicine, Room, Other Charges
- Label for: Total Amount (auto-calculated)
- Buttons: Generate Bill, Print Receipt

**Calculations:**
- Real-time total calculation
- Fee display based on selected doctor
- Invoice generation capability

#### 5. DoctorForm

**Location:** `src/ui/DoctorForm.java`

**Components:**
- Text field for: Doctor Name
- Text field for: Specialization
- Formatted text field for: Phone
- Spinner for: Consultation Fee
- Table to display all doctors

**Features:**
- Add new doctors
- View all doctors with details
- Filter by specialization
- Update consultation fees

### UI Workflow

```
Application Launch (Main.java)
        ↓
    Nimbus L&F Applied
        ↓
    MainDashboard Created
        ↓
    ┌─────────────────────────────────┐
    │   CardLayout with 7 Panels      │
    ├─────────────────────────────────┤
    │ 1. Dashboard (Default View)      │
    │ 2. Add Patient Form              │
    │ 3. All Patients View             │
    │ 4. Appointments View             │
    │ 5. Book Appointment Form         │
    │ 6. Doctor Management             │
    │ 7. Billing Form                  │
    └─────────────────────────────────┘
        ↓
    User Clicks Sidebar Button
        ↓
    CardLayout Switches Panel
        ↓
    Form Displays / Data Loads
```

---

## Security & Validation

### Input Validation

**Location:** `src/utils/ValidationUtil.java`

**Validation Methods:**

```java
// Phone: 10 digits, removes spaces/dashes
isValidPhone(String phone)
// Returns: true if exactly 10 digits

// Email: Standard email format
isValidEmail(String email)
// Returns: true if valid email pattern

// Name: 2-50 characters, letters only
isValidName(String name)
// Returns: true if valid name format

// Age: 1-149 years
isValidAge(int age)
// Returns: true if age in valid range

// Blood Group: A+, A-, B+, B-, AB+, AB-, O+, O-
isValidBloodGroup(String bloodGroup)
// Returns: true if valid blood group

// Gender: Male, Female, Other
isValidGender(String gender)
// Returns: true if valid gender
```

### Database Security

#### 1. Prepared Statements (SQL Injection Prevention)
```java
String sql = "INSERT INTO patients (first_name, last_name, phone, ...) VALUES (?, ?, ?, ...)";
PreparedStatement pst = conn.prepareStatement(sql);
pst.setString(1, firstName);  // Parameter binding prevents SQL injection
pst.setString(2, lastName);
```

#### 2. Secure Connection Configuration

**Location:** `src/db/DBConnection.java`

**Configuration Priority:**
1. Environment Variables (Most Secure)
   ```bash
   export DB_URL="jdbc:mysql://localhost:3306/hospital_db"
   export DB_USER="root"
   export DB_PASSWORD="your_password"
   ```

2. db.properties File (File-based)
   ```
   db.url=jdbc:mysql://localhost:3306/hospital_db
   db.user=root
   db.password=your_password
   ```

3. Default Values (Fallback)

**Note:** NO hardcoded passwords in source code

#### 3. Unique Constraints
```sql
-- Phone numbers must be unique
ALTER TABLE patients ADD UNIQUE(phone);

-- Prevents duplicate patient registration
```

#### 4. Foreign Key Constraints
```sql
-- Referential integrity
FOREIGN KEY (patient_id) REFERENCES patients(patient_id)
FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)

-- Prevents orphaned records
```

### Data Sanitization

**String Sanitization:**
```java
private static String sanitizeString(String input) {
    if (input == null) return null;
    return input.trim()
                .replaceAll("^\\s+|\\s+$", "")  // Trim whitespace
                .replaceAll("\\s+", " ");        // Single space
}
```

---

## Installation & Setup

### System Requirements

**Minimum:**
- Java JDK 11 or higher
- MySQL Server 5.7 or higher
- RAM: 2 GB
- Disk Space: 500 MB

**Recommended:**
- Java JDK 17 LTS
- MySQL 8.0 LTS
- RAM: 4+ GB
- SSD storage

### Prerequisites Installation

#### 1. Install Java JDK

**macOS (Homebrew):**
```bash
brew install openjdk@17
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt-get install openjdk-17-jdk
```

**Verify:**
```bash
java -version
javac -version
```

#### 2. Install MySQL

**macOS (Homebrew):**
```bash
brew install mysql
brew services start mysql
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt-get install mysql-server
sudo systemctl start mysql
```

**Verify:**
```bash
mysql --version
mysql -u root
```

### Project Setup Steps

#### Step 1: Extract/Clone Project
```bash
cd /path/to/Hospital\ Management/
```

#### Step 2: Create Database
```bash
# Connect to MySQL
mysql -u root -p

# Run database setup (from command line)
mysql -u root -p < database/hospital_db.sql
```

#### Step 3: Configure Database Connection

**Option A: Environment Variables (Recommended)**
```bash
# Add to ~/.bash_profile or ~/.zshrc (macOS/Linux)
export DB_URL="jdbc:mysql://localhost:3306/hospital_db"
export DB_USER="root"
export DB_PASSWORD="your_mysql_password"

# Reload shell
source ~/.bash_profile
```

**Option B: db.properties File**
```
# Create: src/config/db.properties
db.url=jdbc:mysql://localhost:3306/hospital_db
db.user=root
db.password=your_mysql_password
```

#### Step 4: Compile Project
```bash
cd /path/to/Hospital\ Management/
javac -cp lib/mysql-connector-j-9.6.0/mysql-connector-j-9.6.0.jar:. \
  -d bin \
  src/**/*.java
```

Or use one-liner:
```bash
find src -name "*.java" | xargs javac -cp lib/mysql-connector-j-9.6.0/mysql-connector-j-9.6.0.jar -d bin
```

#### Step 5: Run Application
```bash
cd /path/to/Hospital\ Management/
java -cp bin:lib/mysql-connector-j-9.6.0/mysql-connector-j-9.6.0.jar Main
```

### Troubleshooting

| Issue | Solution |
|-------|----------|
| "Cannot find symbol: class JFrame" | Add JDK to classpath, ensure Java 11+ installed |
| "Unable to connect to database" | Check MySQL is running, verify credentials |
| "Connection refused: localhost:3306" | Start MySQL service: `brew services start mysql` |
| "Access denied for user 'root'" | Check db.properties password matches MySQL |
| "ClassNotFoundException: com.mysql.jdbc.Driver" | Ensure mysql-connector-j JAR in classpath |

---

## Project Structure

### Directory Layout

```
Hospital Management/
│
├── src/                                  # Source code
│   ├── Main.java                         # Application entry point
│   │
│   ├── db/
│   │   └── DBConnection.java             # Database connection management
│   │
│   ├── models/                           # Data models
│   │   ├── Patient.java                  # Patient model (12 fields)
│   │   ├── Appointment.java              # Appointment model (13 fields)
│   │   ├── Doctor.java                   # Doctor model (5 fields)
│   │   └── Bill.java                     # Bill model (9 fields)
│   │
│   ├── dao/                              # Data Access Objects
│   │   ├── PatientDAO.java               # Patient CRUD operations
│   │   ├── AppointmentDAO.java           # Appointment CRUD operations
│   │   ├── DoctorDAO.java                # Doctor CRUD operations
│   │   └── BillDAO.java                  # Bill CRUD operations
│   │
│   ├── ui/                               # User Interface (Java Swing)
│   │   ├── MainDashboard.java            # Main window, CardLayout navigation
│   │   ├── AddPatientForm.java           # Patient registration UI
│   │   ├── AddPatientFormWithEvents.java # Patient form with event handlers
│   │   ├── AppointmentForm.java          # Appointment booking UI
│   │   ├── AppointmentFormWithEvents.java# Appointment form with events
│   │   ├── BillingForm.java              # Billing UI
│   │   └── DoctorForm.java               # Doctor management UI
│   │
│   ├── utils/
│   │   └── ValidationUtil.java           # Input validation utilities
│   │
│   └── config/
│       └── db.properties                 # Database configuration
│
├── bin/                                  # Compiled .class files
│   ├── *.class                           # Compiled Java classes
│   └── config/
│       └── db.properties                 # Compiled config copy
│
├── lib/
│   └── mysql-connector-j-9.6.0/          # MySQL JDBC Driver
│       ├── mysql-connector-j-9.6.0.jar   # Main JAR file
│       └── [Other library files]
│
├── database/
│   └── hospital_db.sql                   # Database schema & sample data
│
├── resources/                            # Project resources
│
└── Documentation Files
    ├── README.md                         # Quick start guide
    ├── SETUP_GUIDE.md                    # Detailed setup instructions
    ├── IMPLEMENTATION_SUMMARY.md         # Implementation details
    ├── CODE_EXAMPLES.md                  # Code usage examples
    ├── SQL_QUERIES_REFERENCE.sql         # SQL query reference
    ├── ADD_PATIENT_MODULE_REPORT.md      # Patient module documentation
    ├── ADD_APPOINTMENT_MODULE_REPORT.md  # Appointment module documentation
    ├── QUICKSTART.md                     # Quick start guide
    └── PROJECT_COMPREHENSIVE_REPORT.md   # This file
```

### File Count Summary

| Category | Count | Purpose |
|----------|-------|---------|
| Java Source Files | 14 | Core application code |
| DAO Classes | 4 | Database operations |
| Model Classes | 4 | Data representation |
| UI Forms | 7 | User interface components |
| Configuration Files | 2 | db.properties |
| Database Files | 1 | hospital_db.sql |
| Documentation | 10 | Project documentation |

---

## Key Metrics & Statistics

### Code Metrics

| Metric | Value |
|--------|-------|
| Total Java Classes | 14 |
| Total Lines of Code | ~2,500+ |
| DAO Methods | 30+ |
| Validation Methods | 8 |
| Database Tables | 4 |
| UI Forms | 7 |

### Database Metrics

| Table | Records | Indexes |
|-------|---------|---------|
| patients | Sample data included | 2 |
| doctors | 2 sample doctors | 1 |
| appointments | 5 sample appointments | 2 |
| bills | 0 (user-generated) | 3 |

### Supported Data Types

| Type | Examples |
|------|----------|
| Numeric | Patient ID, Age, Phone, Fees, Token Number |
| Text | Names, Addresses, Medical History, Reasons |
| Dates | Registration Date, Appointment Date, Bill Date |
| Enums | Gender, Blood Group, Department, Status |
| Boolean | Status flags, Validation checks |

---

## Performance Considerations

### Optimization Strategies

1. **Database Indexing**
   - Indexes on frequently searched fields (phone, status, date)
   - Composite indexes for multi-column queries

2. **Data Structure Selection**
   - LinkedList for patients (queue-like operations)
   - ArrayList for appointments (random access)

3. **Lazy Loading**
   - UI panels load data on-demand
   - Background threads prevent UI freezing

4. **Prepared Statements**
   - Prevent SQL injection
   - Improve query performance through statement caching

### Scalability

**Current Capacity:**
- Supports thousands of patient records
- Efficient appointment searching (indexed by date)
- Real-time billing operations

**Future Optimization:**
- Connection pooling for multiple concurrent users
- Caching frequently accessed data
- Database replication for high availability

---

## Maintenance & Support

### Regular Maintenance Tasks

1. **Database Backups**
   ```bash
   mysqldump -u root -p hospital_db > backup_$(date +%Y%m%d).sql
   ```

2. **Log Monitoring**
   - Monitor console output for errors
   - Check MySQL error logs

3. **Performance Tuning**
   - Review slow query logs
   - Add indexes as needed

### Common Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| Slow appointment search | Missing indexes | Add index on appointment_date |
| Duplicate patient records | Validation bypass | Check ValidationUtil logic |
| Connection timeouts | Long-running queries | Optimize query or increase timeout |

---

## Future Enhancement Opportunities

1. **Multi-User Support**
   - User authentication and roles
   - User-based access control (RBAC)

2. **Reporting Module**
   - Generate PDF reports
   - Statistical analysis dashboards
   - Revenue tracking

3. **Mobile Application**
   - Cross-platform mobile app
   - Patient appointment reminders

4. **Cloud Integration**
   - Cloud database hosting
   - Real-time synchronization

5. **Advanced Features**
   - Prescription management
   - Lab test integration
   - Telemedicine capabilities

---

## Conclusion

The Hospital Management System is a comprehensive, production-ready desktop application built with modern Java technologies. It demonstrates proper implementation of:

✅ **Architecture Patterns** - DAO, MVC, Singleton  
✅ **Security Practices** - Input validation, SQL injection prevention  
✅ **Database Design** - Proper normalization, referential integrity  
✅ **UI/UX** - Professional Swing interface with intuitive navigation  
✅ **Error Handling** - Comprehensive exception management  
✅ **Code Organization** - Clear package structure, separation of concerns  

The system is ready for deployment in hospital environments and can handle typical hospital workflows efficiently.

---

## Document Information

- **Created Date:** April 23, 2026
- **Last Updated:** April 23, 2026
- **Version:** 1.0
- **Status:** Complete
- **Author:** Hospital Management System Development Team

---

**End of Report**
