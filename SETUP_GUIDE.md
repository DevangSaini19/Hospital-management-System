# Hospital Management System - Setup & Configuration Guide

## 🔐 SECURITY WARNING: Remove Sensitive Data from GitHub

If you've already pushed the repository with the password hardcoded, follow these steps:

### Step 1: Remove Password History from Git

```bash
# Install BFG Repo-Cleaner (recommended)
brew install bfg

# Or use git-filter-branch
git filter-branch --tree-filter 'find . -name "DBConnection.java" -exec sed -i "" "s/private static final String PASSWORD = \"dev@2006\";/private static final String PASSWORD = \"\";/g" {} \;' HEAD
```

### Step 2: Force Push to GitHub (⚠️ Destructive - Use with Caution)

```bash
git push origin --force --all
git push origin --force --tags
```

### Step 3: Rotate Your MySQL Password

```sql
ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_secure_password';
FLUSH PRIVILEGES;
```

---

## 📋 Project Structure

```
Hospital Management/
├── src/
│   ├── db/
│   │   └── DBConnection.java          (Secure connection manager)
│   ├── dao/
│   │   ├── PatientDAO.java             (Patient database operations)
│   │   └── AppointmentDAO.java         (Appointment database operations)
│   ├── models/
│   │   ├── Patient.java
│   │   └── Appointment.java
│   ├── ui/
│   │   ├── AddPatientFormWithEvents.java
│   │   ├── AppointmentFormWithEvents.java
│   │   └── MainDashboard.java
│   ├── utils/
│   │   └── ValidationUtil.java         (Input validation)
│   └── config/
│       └── db.properties               (Database configuration)
├── database/
│   └── hospital_db.sql                 (Database schema)
└── lib/
    └── mysql-connector-j-9.6.0/
```

---

## 🔧 Configuration

### Option 1: Using Environment Variables (RECOMMENDED for Production)

Set environment variables in your system:

**macOS/Linux:**
```bash
export DB_URL="jdbc:mysql://localhost:3306/hospital_db"
export DB_USER="root"
export DB_PASSWORD="your_secure_password"
```

**Windows (PowerShell):**
```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/hospital_db"
$env:DB_USER = "root"
$env:DB_PASSWORD = "your_secure_password"
```

### Option 2: Using db.properties File (For Development)

Create/Update `src/config/db.properties`:

```properties
# Database Configuration
db.url=jdbc:mysql://localhost:3306/hospital_db
db.user=root
db.password=your_secure_password
```

⚠️ **IMPORTANT:** Add `db.properties` to `.gitignore` to prevent committing passwords:

```bash
echo "src/config/db.properties" >> .gitignore
git add .gitignore
git commit -m "Add db.properties to gitignore"
```

---

## 📊 Database Setup

### 1. Create Database and Tables

```bash
mysql -u root -p < database/hospital_db.sql
```

### 2. Insert Sample Doctors

```sql
INSERT INTO doctors (name, specialization, phone, email) VALUES
('Dr. Ramesh Sharma', 'Cardiology', '9876500001', 'ramesh@hospital.com'),
('Dr. Priya Gupta', 'Neurology', '9876500002', 'priya@hospital.com'),
('Dr. Amit Patel', 'Orthopaedics', '9876500003', 'amit@hospital.com');
```

---

## 🎯 Key Features Implemented

### 1. **Add Patient Module**
- ✅ Insert new patient with all fields
- ✅ Validate phone number uniqueness
- ✅ Input validation (age, phone, email, name format)
- ✅ Auto-generated patient ID
- ✅ Exception handling with specific error messages

**Usage:**
```java
PatientDAO dao = new PatientDAO();
Patient patient = new Patient("John", "Doe", 30, "Male", "O+", 
                               "9876543210", "john@email.com", 
                               "Address 123", "No medical history");
boolean success = dao.addPatient(patient); // Returns true if successful
```

### 2. **Appointment Module**
- ✅ Book appointment with validation
- ✅ Prevent duplicate appointments (same doctor, same time)
- ✅ Validate patient and doctor existence
- ✅ Generate unique token number
- ✅ Support appointment status updates

**Usage:**
```java
AppointmentDAO dao = new AppointmentDAO();
Appointment appt = new Appointment(1, 2, Date.valueOf("2024-05-20"), 
                                   Time.valueOf("09:00:00"), 
                                   "Cardiology", "Regular checkup");
boolean success = dao.bookAppointment(appt); // Returns true if booked
```

### 3. **Input Validation**
- ✅ Phone number: 10 digits only
- ✅ Email: Valid email format
- ✅ Name: 2-50 characters, letters/spaces/hyphens
- ✅ Age: 1-149 years
- ✅ Blood group: Valid ENUM values
- ✅ Gender: Male/Female/Other

---

## 🔍 SQL Queries Used

### Patient Operations

```sql
-- Add Patient
INSERT INTO patients (first_name, last_name, age, gender, blood_group, phone, email, address, medical_history, status) 
VALUES (?,?,?,?,?,?,?,?,?,?);

-- Check Unique Phone
SELECT COUNT(*) FROM patients WHERE phone = ?;

-- Get All Patients
SELECT * FROM patients ORDER BY registration_date DESC;

-- Search Patients
SELECT * FROM patients WHERE first_name LIKE ? OR last_name LIKE ? OR phone LIKE ?;
```

### Appointment Operations

```sql
-- Book Appointment
INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, department, reason, status, token_number) 
VALUES (?,?,?,?,?,?,?,?);

-- Check Duplicate Appointment
SELECT COUNT(*) FROM appointments 
WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? 
AND status IN ('Scheduled', 'Pending');

-- Get All Appointments with Details
SELECT a.*, CONCAT(p.first_name,' ',p.last_name) AS patient_name, d.name AS doctor_name 
FROM appointments a 
JOIN patients p ON a.patient_id = p.patient_id 
JOIN doctors d ON a.doctor_id = d.doctor_id 
ORDER BY a.appointment_date DESC, a.appointment_time ASC;
```

---

## 📦 Dependencies

- **MySQL JDBC Driver**: `mysql-connector-j-9.6.0`
- **Java Version**: 11 or higher
- **MySQL Server**: 5.7 or higher

---

## 🧪 Testing the Connection

```java
// Test database connection
boolean connected = DBConnection.testConnection();
if (connected) {
    System.out.println("✓ Database is connected!");
} else {
    System.out.println("✗ Database connection failed!");
}
```

---

## ⚠️ Error Handling

The application handles:
- **SQL Connection Errors**: Shows user-friendly error dialogs
- **Duplicate Phone Numbers**: Prevents duplicate patient registration
- **Invalid Patient/Doctor IDs**: Validates existence before booking
- **Duplicate Appointments**: Prevents double-booking
- **Input Validation Errors**: Validates before database operations
- **SQLException**: Logs detailed error messages for debugging

---

## 🔐 Security Best Practices

1. ✅ **Never hardcode passwords** - Use environment variables or properties files
2. ✅ **Use PreparedStatement** - Prevents SQL injection
3. ✅ **Validate all inputs** - Server-side and client-side
4. ✅ **Use try-with-resources** - Automatically closes connections
5. ✅ **Add .gitignore** - Prevent sensitive files from being committed
6. ✅ **Rotate passwords regularly** - After security incidents
7. ✅ **Use strong passwords** - Follow security guidelines

---

## 📝 Example Usage

### Adding a Patient

```java
// Create DAO
PatientDAO patientDAO = new PatientDAO();

// Create patient object
Patient patient = new Patient(
    "Ramesh", "Kumar", 45, "Male", "AB+",
    "9123456789", "ramesh@email.com",
    "123 Main Street", "Diabetes patient"
);

// Add to database
if (patientDAO.addPatient(patient)) {
    System.out.println("✓ Patient added with ID: " + patient.getPatientId());
} else {
    System.out.println("✗ Failed to add patient");
}
```

### Booking an Appointment

```java
// Create DAO
AppointmentDAO appointmentDAO = new AppointmentDAO();

// Create appointment
Appointment appointment = new Appointment(
    1,                                    // Patient ID
    2,                                    // Doctor ID
    Date.valueOf("2024-05-20"),          // Date
    Time.valueOf("10:00:00"),            // Time
    "Cardiology",                         // Department
    "Regular checkup"                     // Reason
);

// Book appointment
if (appointmentDAO.bookAppointment(appointment)) {
    System.out.println("✓ Appointment booked! Token: " + appointment.getTokenNumber());
} else {
    System.out.println("✗ Failed to book appointment");
}
```

---

## 🚀 Future Enhancements

- [ ] Add prescription management
- [ ] Implement billing system
- [ ] Add patient medical records
- [ ] SMS/Email notifications
- [ ] Doctor availability calendar
- [ ] Report generation
- [ ] User authentication
- [ ] Audit logging

---

## 📞 Support

For issues or questions:
1. Check console error messages
2. Review logs in `error.log`
3. Verify database connection
4. Check input validation errors

---

**Last Updated:** April 2024  
**Version:** 1.0.0
