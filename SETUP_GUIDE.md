# 🔧 Complete Setup & Configuration Guide

Comprehensive guide for setting up the Hospital Management System with all configuration options, best practices, and troubleshooting.

---

## 📋 Table of Contents

1. [System Requirements](#system-requirements)
2. [Installation Steps](#installation-steps)
3. [Database Setup](#database-setup)
4. [Credential Configuration](#credential-configuration)
5. [Compilation Options](#compilation-options)
6. [Running the Application](#running-the-application)
7. [Security Best Practices](#security-best-practices)
8. [Troubleshooting](#troubleshooting)

---

## 🖥️ System Requirements

### Minimum Requirements
- **Java JDK:** 11.0.0 or higher
- **MySQL Server:** 5.7.0 or higher
- **RAM:** 2 GB minimum
- **Disk Space:** 500 MB for project + libraries

### Recommended
- Java JDK 17 LTS or higher
- MySQL 8.0 LTS
- 4+ GB RAM
- SSD (for faster database operations)

### Verify Installations

```bash
# Check Java version
java -version

# Check MySQL version
mysql --version

# Check MySQL service status (macOS)
brew services list | grep mysql
```

---

## 📥 Installation Steps

### Step 1: Obtain the Project

**Option A: Clone from GitHub**
```bash
git clone https://github.com/DevangSaini19/Hospital-management-System.git
cd Hospital-management-System
```

**Option B: Download ZIP**
1. Visit the GitHub repository
2. Click "Code" → "Download ZIP"
3. Extract to desired location
4. Open terminal in extracted folder

### Step 2: Verify Project Structure

```bash
# Check essential files exist
ls -la src/Main.java
ls -la database/hospital_db.sql
ls -la lib/mysql-connector-j-9.6.0/

# All should show file/directory exists
```

---

## 🗄️ Database Setup

### Step 1: Start MySQL Service

**macOS (Homebrew):**
```bash
# Start MySQL service
brew services start mysql

# Verify it's running
brew services list | grep mysql
```

**Linux (Ubuntu/Debian):**
```bash
sudo systemctl start mysql
sudo systemctl status mysql
```

**Windows:**
- Open Services (Press `Win + R`, type `services.msc`)
- Find "MySQL" service
- Right-click → Start

### Step 2: Create Database

**Method 1: Using SQL Script (Recommended)**
```bash
mysql -u root -p < database/hospital_db.sql
```
Enter your MySQL root password when prompted.

**Method 2: Manual Setup**
```bash
# Connect to MySQL
mysql -u root -p

# Run these commands:
CREATE DATABASE hospital_db;
USE hospital_db;
SOURCE database/hospital_db.sql;
```

**Method 3: Using MySQL Workbench**
1. Open MySQL Workbench
2. Connect to your MySQL server
3. File → Open SQL Script → Select `hospital_db.sql`
4. Execute (Ctrl+Enter)

### Step 3: Verify Database Creation

```bash
# Connect to MySQL
mysql -u root -p

# Run verification
SHOW DATABASES;  -- Should see "hospital_db"
USE hospital_db;
SHOW TABLES;     -- Should see "doctors", "patients", "appointments"
SELECT COUNT(*) FROM doctors;  -- Should show 2+
```

---

## 🔐 Credential Configuration

### ⚠️ SECURITY FIRST: Never Hardcode Passwords!

The application supports secure credential methods:

---

### Method 1: Environment Variables (PRODUCTION RECOMMENDED)

**Why:** Safest option, no files to protect.

**macOS/Linux - Temporary (Current Session):**
```bash
export DB_URL="jdbc:mysql://localhost:3306/hospital_db"
export DB_USER="root"
export DB_PASSWORD="YOUR_DB_PASSWORD"

# Verify
echo $DB_URL
echo $DB_USER
```

**macOS/Linux - Permanent (Add to ~/.zshrc or ~/.bash_profile):**
```bash
# Add these lines to end of file
export DB_URL="jdbc:mysql://localhost:3306/hospital_db"
export DB_USER="root"
export DB_PASSWORD="YOUR_DB_PASSWORD"

# Apply changes
source ~/.zshrc  # or source ~/.bash_profile
```

**Windows PowerShell:**
```powershell
# Current session only
$env:DB_URL = "jdbc:mysql://localhost:3306/hospital_db"
$env:DB_USER = "root"
$env:DB_PASSWORD = "YOUR_DB_PASSWORD"
```

---

### Method 2: Configuration File (DEVELOPMENT)

**File Location:** `src/config/db.properties`

**Create/Edit File:**
```properties
# Hospital Management System Database Configuration
# ⚠️ WARNING: This file contains sensitive information
# DO NOT commit to Git - already in .gitignore

# Database Connection URL
db.url=jdbc:mysql://localhost:3306/hospital_db

# Database User
db.user=root

# Database Password
db.password=YOUR_DB_PASSWORD
```

**Why it's secure:**
- Already added to `.gitignore` (won't be pushed to GitHub)
- Different for each developer/environment

**Verify it's ignored:**
```bash
git status
# Output should NOT show "db.properties"

git check-ignore src/config/db.properties
# Output: "src/config/db.properties"
```

---

### Password Security Tips

✅ **Strong Password Requirements:**
- Minimum 12 characters
- Mix of uppercase, lowercase, numbers, symbols
- Avoid common words or patterns
- Never reuse passwords

**Example strong password:**
```
Hsp@tl2024!Secure#Pwd
```

---

## 🔨 Compilation Options

### Option 1: Simple Compilation (Recommended)

```bash
cd "your-project-folder"
javac -cp "lib/mysql-connector-j-9.6.0/*:src" \
  src/models/*.java \
  src/db/*.java \
  src/dao/*.java \
  src/utils/*.java \
  src/ui/*.java \
  src/Main.java \
  -d bin/
```

### Option 2: With Debug Information

```bash
javac -g -cp "lib/mysql-connector-j-9.6.0/*:src" \
  src/models/*.java src/db/*.java src/dao/*.java \
  src/utils/*.java src/ui/*.java src/Main.java -d bin/
```

### Option 3: Create Build Script

**File: `build.sh` (macOS/Linux)**
```bash
#!/bin/bash

echo "🔨 Compiling Hospital Management System..."

javac -cp "lib/mysql-connector-j-9.6.0/*:src" \
  src/models/*.java \
  src/db/*.java \
  src/dao/*.java \
  src/utils/*.java \
  src/ui/*.java \
  src/Main.java \
  -d bin/

if [ $? -eq 0 ]; then
  echo "✅ Compilation successful!"
else
  echo "❌ Compilation failed!"
  exit 1
fi
```

**Usage:**
```bash
chmod +x build.sh
./build.sh
```

---

## 🚀 Running the Application

### Method 1: Command Line (Simplest)

```bash
java -cp "lib/mysql-connector-j-9.6.0/*:bin" Main
```

**Create Alias for Easy Access:**

Add to `~/.zshrc` or `~/.bash_profile`:
```bash
alias hms="cd ~/Desktop/Hospital\ Management && \
  java -cp lib/mysql-connector-j-9.6.0/*:bin Main"
```

Then simply run: `hms`

### Method 2: Run Script

**File: `run.sh` (macOS/Linux)**
```bash
#!/bin/bash

echo "🏥 Starting Hospital Management System..."

cd "your-project-folder"

# Check if database credentials are set
if [ -z "$DB_URL" ] && [ ! -f "src/config/db.properties" ]; then
  echo "❌ Error: Database credentials not configured"
  echo "Set environment variables or create src/config/db.properties"
  exit 1
fi

java -cp "lib/mysql-connector-j-9.6.0/*:bin" Main
```

**Usage:**
```bash
chmod +x run.sh
./run.sh
```

### Method 3: From VS Code

**Install Extension:**
1. Open VS Code
2. Go to Extensions (Ctrl+Shift+X)
3. Search "Extension Pack for Java"
4. Install by Microsoft

**Run:**
- Open `src/Main.java`
- Click the ▶️ **Run** button

### Method 4: From IDE

**IntelliJ IDEA:**
1. Right-click `src/Main.java`
2. Select "Run 'Main'"

**Eclipse:**
1. Right-click `src/Main.java`
2. Select "Run As" → "Java Application"

---

## 🔒 Security Best Practices

### Git Security

**1. Verify .gitignore Protects Secrets**

```bash
# Check .gitignore contents
cat .gitignore | grep -i "properties\|env\|password"

# Verify sensitive files won't be committed
git check-ignore src/config/db.properties
```

**2. Pre-commit Hook (Optional)**

Create `.git/hooks/pre-commit`:
```bash
#!/bin/bash

# Prevent committing files with "password" in content
if git diff --cached | grep -i "password.*=" | grep -v "YOUR_DB_PASSWORD"; then
  echo "❌ ERROR: Credentials detected in staged changes!"
  exit 1
fi
```

Make it executable:
```bash
chmod +x .git/hooks/pre-commit
```

### Code Security

✅ **Implemented:**
- All SQL queries use PreparedStatement (prevents SQL injection)
- Input validation for all user entries
- Secure password handling

✅ **Recommended:**
- Implement user authentication layer
- Add audit logging
- Regular security updates

### Database Security

```sql
-- Create restricted database user (not root)
CREATE USER 'hms_app'@'localhost' IDENTIFIED BY 'YOUR_STRONG_PASSWORD';
GRANT SELECT, INSERT, UPDATE, DELETE ON hospital_db.* TO 'hms_app'@'localhost';
FLUSH PRIVILEGES;
```

---

## 🐛 Troubleshooting

### Database Connection Issues

**Error: "Connection refused"**
```bash
# Check if MySQL is running
brew services list | grep mysql

# Start MySQL
brew services start mysql
```

**Error: "Unknown database 'hospital_db'"**
```bash
# Recreate database
mysql -u root -p < database/hospital_db.sql

# Verify
mysql -u root -p -e "SHOW DATABASES;" | grep hospital_db
```

### Compilation Issues

**Error: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"**
```bash
# Verify JDBC driver location
ls lib/mysql-connector-j-9.6.0/

# Ensure lib is in classpath (-cp argument)
```

---

## ✅ Verification Checklist

After setup, verify:

- [ ] Java installed: `java -version` shows 11+
- [ ] MySQL running: `brew services list` shows active
- [ ] Database created: `mysql -u root -p -e "SHOW DATABASES;"` shows hospital_db
- [ ] Source files present: `ls src/` shows all .java files
- [ ] Project compiles: No errors from javac command
- [ ] Application runs: GUI window appears

---

**Last Updated:** April 2026  
**Version:** 1.0
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
