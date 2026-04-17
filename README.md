# 🏥 Hospital Management System (HMS)

A comprehensive Java-based Hospital Management System featuring a user-friendly GUI, MySQL database integration, and complete patient and appointment management functionality.

---

## 📋 Project Overview

**Hospital Management System** is a desktop application built with Java Swing that enables hospitals to manage:
- Patien-Management-System/
├── src/
│   ├── Main.java                      # Application entry point
│   ├── db/
│   │   └── DBConnection.java          # MySQL connection management
│   ├── models/
│   │   ├── Patient.java               # Patient data model
│   │   └── Appointment.java           # Appointment data model
│   ├── dao/
│   │   ├── PatientDAO.java            # Patient database operations
│   │   └── AppointmentDAO.java        # Appointment database operations
│   ├── utils/
│   │   └── ValidationUtil.java        # Input validation utilities
│   └── ui/
│       ├── MainDashboard.java         # Main application window
│       ├── AddPatientForm.java        # Patient registration form
│       └── AppointmentForm.java       # Appointment booking form
├── database/
│   └── hospital_db.sql                # Database schema
├── lib/
│   └── mysql-connector-j-9.6.0/       # MySQL JDBC driver
├── bin/                               # Compiled Java classes
├── .gitignore                         # Git ignore configuration
└── README.md                          # This file
## 📁 Project Structure

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

---echnology Stack

| Component | Technology |
|-----------|-----------|
| **Frontend** | Java Swing (javax.swing) |
| **Backend** | Core Java & JDBC |
| **Database** | MySQL 5.7+ |
| **JDBC Driver** | mysql-connector-j-9.6.0 |
| **Build** | Manual javac compilation |
| **IDE** | VS Code / IntelliJ / Eclipse |

---

## 📋 Prerequisites

Before you begin, ensure you have installed:

- **Java JDK** 11 or higher ([Download](https://www.oracle.com/java/technologies/downloads/))
- **MySQL Server** 5.7 or higher ([Download](https://dev.mysql.com/downloads/mysql/))
- **Git** (optional, for version control)
- **VS Code** or any Java IDE (optional)

**Verify installations:**
```bash
java -version
mysql --version
```

---

## ⚙️ Setup Instructions

### Step 1: Clone or Download the Project

```bash
# Using Git
git clone https://github.com/DevangSaini19/Hospital-management-System.git
cd Hospital-management-System

# Or download and extract the ZIP file
```

### Step 2: Install MySQL and Create Database

**Start MySQL:**
```bash
# macOS
brew services start mysql

# Linux
sudo systemctl start mysql

# Windows
# Start MySQL via Services or MySQL Workbench
```

**Create Database:**
```bash
mysql -u root -p < database/hospital_db.sql
```

When prompted, enter your MySQL root password.

### Step 3: Configure Database Credentials

**⚠️ SECURITY WARNING:** Never hardcode passwords in source code!

**Option A: Using Environment Variables (Recommended)**

```bash
# macOS/Linux
export DB_URL="jdbc:mysql://localhost:3306/hospital_db"
export DB_USER="root"
export DB_PASSWORD="YOUR_DB_PASSWORD"

# Windows (PowerShell)
$env:DB_URL = "jdbc:mysql://localhost:3306/hospital_db"
$env:DB_USER = "root"
$env:DB_PASSWORD = "YOUR_DB_PASSWORD"
```

**Option B: Using Configuration File**

Create/Update `src/config/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/hospital_db
db.user=root
db.password=YOUR_DB_PASSWORD
```

**Important:** `db.properties` is already in `.gitignore` to prevent accidentally pushing credentials.

### Step 4: Compile the Project

```bash
cd "youser Interface

### Color Scheme

| Element | Color | Hex Code |
|---------|-------|----------|
| Header & Sidebar | Deep Blue | #006699 |
| Buttons & Highlights | Green | #009966 |
| Background | Light Blue | #F5F8FC |
| Text (Primary) | Dark Gray | #333333 |
| Text (Secondary) | White | #FFFFFF |

### Main Sections

1. **Dashboard** - Overview with statistics
2. *DOCTORS Table
```sql
CREATE TABLE doctors (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100)
);
```

### PATIENTS Table
```sql
CREATE TABLE patients (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    age INT NOT NULL,
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    blood_group ENUM('A+','A-','B+','B-','AB+','AB-','O+','O-') NOT NULL,
    phone VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100),
    address TEXT,
    medical_history TEXT,
    registration_date DATE DEFAULT CURRENT_DATE,
    status ENUM('Active', 'Discharged', 'Critical') DEFAULT 'Active'
);
```

### APPOINTMENTS Table
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
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE
);
```

---

## 🔐 Security Practices

✅ **Implemented:**
- No hardcoded credentials in source code
- Passwords stored in `.env` or configuration files (excluded via `.gitignore`)
- Input validation for all user entries
- SQL injection prevention using PreparedStatements
- Secure password handling with environment variables

✅ **Best Practices:**
- Always use `.gitignore` to exclude sensitive files
- Never commit `db.properties` or `.env` files
- Rotate passwords regularly
- Use strong, complex passwords
- Keep MySQL and Java updated

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| **Connection refused** | Ensure MySQL is running: `brew services start mysql` |
| **Database not found** | Run: `mysql -u root -p < database/hospital_db.sql` |
| **Compilation error** | Check Java version: `java -version` (requires 11+) |
| **ClassNotFoundException** | Ensure MySQL JDBC driver is in `lib/` folder |
| **Wrong password error** | Update credentials in `src/config/db.properties` |

---

## 📝 Usage Examples

### Adding a Patient (GUI)
1. Click "➕ Add Patient" tab
2. Fill in all fields with valid data
3. Click "Add Patient" button
4. Confirm success message

### Booking an Appointment (GUI)
1. Click "📅 Appointment" tab
2. Select patient and doctor from dropdowns
3. Enter appointment date and time
4. Click "Book Appointment" button
5. Receive appointment confirmation with token number

### Searching Patients (GUI)
1. Click "👥 All Patients" tab
2. View list of all registered patients
3. Use dashboard search feature for specific patients

---

## 👥 Team Collaboration

**For Team Members:**
1. Clone the repository: `git clone [repository-url]`
2. Create own database using: `mysql -u root -p < database/hospital_db.sql`
3. Set up credentials in `src/config/db.properties` (won't be tracked by Git)
4. Compile and run the project
5. Create new branches for features: `git checkout -b feature/your-feature`

---

## 📄 License

This project is provided as-is for educational purposes.

---

## ✉️ Contact & Support

- **Issues/Feedback:** Report via GitHub Issues
- **Questions:** Contact the development team
- **Contributing:** Submit pull requests for improvements

---

## 🔑 KEY CLASSES & METHODS

### **DBConnection.java**
- `getConnection()` - Get or create database connection
- `testConnection()` - Verify database connectivity
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
