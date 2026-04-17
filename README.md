# Hospital Management System (HMS)
## Inspired by Bombay Hospital, Jaipur

A comprehensive Java-based Hospital Management System with MySQL backend and Java Swing GUI.

---

## 📋 PROJECT STRUCTURE

```
Hospital Management/
├── src/
│   ├── Main.java                 (Entry point)
│   ├── db/
│   │   └── DBConnection.java     (MySQL connection management)
│   ├── models/
│   │   ├── Patient.java          (Patient model)
│   │   └── Appointment.java      (Appointment model)
│   ├── dao/
│   │   ├── PatientDAO.java       (Data Access with LinkedList)
│   │   └── AppointmentDAO.java   (Data Access with ArrayList)
│   └── ui/
│       ├── MainDashboard.java    (Main window with CardLayout)
│       ├── AddPatientForm.java   (Patient registration form)
│       └── AppointmentForm.java  (Appointment booking form)
├── bin/                          (Compiled .class files)
├── lib/
│   └── mysql-connector-j-9.6.0/  (MySQL JDBC driver)
├── database/
│   └── hospital_db.sql           (Database schema & sample data)
└── .vscode/
    └── settings.json             (VS Code Java configuration)
```

---

## 🛠️ TECH STACK

- **Frontend:** Java Swing (javax.swing)
- **Backend/Logic:** Core Java + Advanced Java (JDBC)
- **Database:** MySQL
- **DB Connector:** mysql-connector-j-9.6.0.jar
- **Data Structures:** 
  - LinkedList (patient queue in PatientDAO)
  - ArrayList (appointment list in AppointmentDAO)
- **IDE:** VS Code with Java Extension Pack
- **Build:** Manual javac compilation

---

## 📦 FEATURES

### 1. **Dashboard**
   - Total patients count
   - Today's appointments count
   - Doctor statistics
   - Real-time database connection status

### 2. **Patient Management**
   - Register new patients with validation
   - View all registered patients
   - Search patients by name or phone
   - Patient details: ID, name, age, gender, blood group, phone, email, address, medical history
   - Patient status tracking (Active, Discharged, Critical)

### 3. **Appointment Booking**
   - Select patient and doctor from dropdowns
   - Choose department and time slot
   - Auto-generate token numbers
   - View all appointments with patient/doctor details
   - Track appointment status (Scheduled, Completed, Cancelled, Pending)

### 4. **Data Persistence**
   - All data stored in MySQL database
   - 8 pre-loaded doctors with specializations
   - Referential integrity with foreign keys
   - Timestamps for all operations

---

## ⚙️ SETUP INSTRUCTIONS

### Step 1: Install MySQL
```bash
# macOS (using Homebrew)
brew install mysql

# Start MySQL service
brew services start mysql

# Secure installation
mysql_secure_installation
```

### Step 2: Create Database
```bash
# Open MySQL command line
mysql -u root -p

# Run the SQL script
SOURCE /path/to/Hospital\ Management/database/hospital_db.sql;
```

### Step 3: Update Database Credentials
Edit `src/db/DBConnection.java`:
```java
private static final String PASSWORD = "your_mysql_password"; // ← Change this
```

### Step 4: Verify MySQL Connector
Ensure `lib/mysql-connector-j-9.6.0/` exists with JAR files.

### Step 5: Compile Project
```bash
cd "/Users/devangsaini/Desktop/Hospital Management"
javac -cp "lib/*" -d bin $(find src -name "*.java")
```

### Step 6: Run Application (VS Code)
1. Install **Extension Pack for Java** by Microsoft
2. Open `src/Main.java`
3. Right-click → **Run Java** (or click ▶️ Play button)

### Step 7: Run Application (Terminal)
```bash
cd "/Users/devangsaini/Desktop/Hospital Management"
java -cp "lib/*:bin" Main
```

---

## 🎨 UI COLOR SCHEME

| Element | Color | Hex |
|---------|-------|-----|
| Primary (Header/Sidebar) | Deep Blue | #006699 |
| Accent (Buttons) | Green | #009966 |
| Background | Light Blue | #F5F8FC |
| Text | White/Dark | #FFFFFF / #333333 |

---

## 📊 DATABASE SCHEMA

### **DOCTORS TABLE**
```sql
CREATE TABLE doctors (
    doctor_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    specialization VARCHAR(100),
    phone VARCHAR(15),
    email VARCHAR(100)
);
```

### **PATIENTS TABLE**
```sql
CREATE TABLE patients (
    patient_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    age INT,
    gender ENUM('Male', 'Female', 'Other'),
    blood_group ENUM('A+','A-','B+','B-','AB+','AB-','O+','O-'),
    phone VARCHAR(15) UNIQUE,
    email VARCHAR(100),
    address TEXT,
    medical_history TEXT,
    registration_date DATE,
    status ENUM('Active', 'Discharged', 'Critical')
);
```

### **APPOINTMENTS TABLE**
```sql
CREATE TABLE appointments (
    appointment_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT,
    doctor_id INT,
    appointment_date DATE,
    appointment_time TIME,
    department VARCHAR(100),
    reason TEXT,
    status ENUM('Scheduled', 'Completed', 'Cancelled', 'Pending'),
    token_number INT,
    notes TEXT,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)
);
```

---

## 🔑 KEY CLASSES & METHODS

### **DBConnection.java**
- `getConnection()` - Get or create database connection
- `closeConnection()` - Close database connection

### **PatientDAO.java**
- `addPatient(Patient)` - Register new patient
- `getAllPatients()` - Get all patients as LinkedList
- `searchPatients(keyword)` - Search by name/phone
- `getPatientById(id)` - Fetch single patient
- `getTotalPatients()` - Patient count
- `peekNextInQueue()` - Peek first patient in queue
- `serveNextPatient()` - Remove and return first patient

### **AppointmentDAO.java**
- `bookAppointment(Appointment)` - Schedule appointment
- `getAllAppointments()` - Get all appointments as ArrayList
- `getTodayAppointmentsCount()` - Count today's appointments
- `getAllDoctors()` - Get doctor list for dropdown
- `generateTokenNumber()` - Auto-generate token

### **MainDashboard.java**
- CardLayout for multi-panel navigation
- Real-time statistics
- Sidebar menu with navigation buttons

### **AddPatientForm.java**
- GridBagLayout form
- Input validation
- Duplicate phone check
- Form reset functionality

### **AppointmentForm.java**
- Doctor selection from dropdown
- Date/time picker
- Token number assignment
- Live appointment table

---

## 🧪 TESTING

### Test Patient Registration
1. Click **Add Patient**
2. Fill form with valid data
3. Click **Save Patient**
4. Verify in database: `SELECT * FROM patients;`

### Test Appointment Booking
1. Click **Appointment**
2. Select patient & doctor
3. Choose date/time
4. Click **Book Appointment**
5. Check token number display

### Test Dashboard Stats
1. Verify total patients count updates
2. Check today's appointments counter
3. Doctor count displays as 8

---

## ⚠️ IMPORTANT NOTES

1. **MySQL Must Be Running**
   ```bash
   # Check status
   brew services list | grep mysql
   
   # Start if not running
   brew services start mysql
   ```

2. **Database Password**
   - Update `DBConnection.java` with your MySQL password
   - Default setup uses `root` as username

3. **Classpath Configuration**
   - `.vscode/settings.json` configures Java path for VS Code
   - Library path points to `lib/**/*.jar`
   - Output path is `bin/`

4. **Phone Number is Unique**
   - Duplicate phone numbers will cause save failure
   - Error message: "Phone may already exist"

5. **Data Structure Usage**
   - PatientDAO uses **LinkedList** for queue operations
   - AppointmentDAO uses **ArrayList** for list operations
   - Follows strict tech stack requirements

---

## 🐛 TROUBLESHOOTING

### "Database Connection Failed"
- ✅ Check MySQL is running: `brew services list`
- ✅ Verify credentials in `DBConnection.java`
- ✅ Ensure database exists: `SHOW DATABASES;`

### "Class Not Found: com.mysql.cj.jdbc.Driver"
- ✅ Verify JAR in `/lib/mysql-connector-j-9.6.0/`
- ✅ Check `.vscode/settings.json` has correct path
- ✅ Recompile: `javac -cp "lib/*" -d bin $(find src -name "*.java")`

### "No Patients/Doctors in Dropdown"
- ✅ Check database tables populated
- ✅ Run: `SELECT COUNT(*) FROM doctors;`
- ✅ Verify foreign key relationships

### Form Validation Errors
- ✅ Age must be between 1-149
- ✅ Phone must be unique
- ✅ All required fields (*) must be filled

---

## 📝 SAMPLE DATA

The system includes 8 pre-loaded doctors:

| Doctor | Specialization | Contact |
|--------|----------------|---------|
| Dr. Ramesh Sharma | Cardiology | 9876500001 |
| Dr. Priya Gupta | Neurology | 9876500002 |
| Dr. Anil Mehta | Orthopaedics | 9876500003 |
| Dr. Sunita Joshi | Gynaecology & Obstetrics | 9876500004 |
| Dr. Vikram Singh | General Medicine | 9876500005 |
| Dr. Kavita Patel | Paediatrics | 9876500006 |
| Dr. Ajay Verma | Urology | 9876500007 |
| Dr. Meena Rao | Gastroenterology | 9876500008 |

---

## 📞 HOSPITAL DETAILS

**Bombay Hospital, Jaipur**
- Located in Jaipur, Rajasthan, India
- This HMS is inspired by their operations and management practices

---

## 📄 LICENSE

This project is for educational purposes.

---

## 👨‍💻 DEVELOPER NOTES

- All JDBC operations use PreparedStatement for SQL injection prevention
- Connection pooling handled via singleton pattern in DBConnection
- GridBagLayout provides responsive UI across different screen sizes
- CardLayout enables seamless navigation between modules
- LinkedList provides O(1) queue operations for patient management
- ArrayList provides O(1) random access for appointments

---

**Version:** 1.0  
**Last Updated:** April 16, 2026  
**Java Version:** 8+  
**MySQL Version:** 5.7+
