# ⚡ Quick Start Guide

Get your Hospital Management System running in 5 minutes!

---

## 📋 Prerequisites

- Java JDK 11+ installed
- MySQL 5.7+ installed and running
- Project folder downloaded/cloned

---

## 🚀 5-Minute Setup

### 1️⃣ Create Database (30 seconds)

```bash
mysql -u root -p < database/hospital_db.sql
```

Enter your MySQL root password when prompted.

### 2️⃣ Set Database Credentials (30 seconds)

**Using Environment Variables:**
```bash
export DB_URL="jdbc:mysql://localhost:3306/hospital_db"
export DB_USER="root"
export DB_PASSWORD="YOUR_DB_PASSWORD"
```

**Or Edit Configuration File:**
Edit `src/config/db.properties`:
```properties
db.password=YOUR_DB_PASSWORD
```

### 3️⃣ Compile Project (1 minute)

```bash
cd "your-project-folder"
javac -cp "lib/mysql-connector-j-9.6.0/*:src" \
  src/models/*.java src/db/*.java src/dao/*.java \
  src/utils/*.java src/ui/*.java src/Main.java -d bin/
```

**Quick Alias (Optional):**
Add to `.zshrc` or `.bash_profile`:
```bash
alias compile-hms='javac -cp "lib/mysql-connector-j-9.6.0/*:src" \
  src/models/*.java src/db/*.java src/dao/*.java \
  src/utils/*.java src/ui/*.java src/Main.java -d bin/'
```

Then just run: `compile-hms`

### 4️⃣ Run Application (30 seconds)

```bash
java -cp "lib/mysql-connector-j-9.6.0/*:bin" Main
```

**That's it!** The GUI window will open automatically.

---

## ✅ Verify Everything Works

### Check 1: Database Connection
- Dashboard shows ✅ "Connected to MySQL Database"
- Total Patients: 3 (default demo data)
- Appointments: 2

### Check 2: Add a Patient
1. Click "➕ Add Patient"
2. Fill in form with valid data
3. Click "Add Patient"
4. Success message appears

### Check 3: Book Appointment
1. Click "📅 Appointment"
2. Select patient and doctor
3. Enter date/time
4. Book and get token number

---

## 🔧 Common Issues

| Issue | Fix |
|-------|-----|
| `Connection refused` | Start MySQL: `brew services start mysql` |
| `database hospital_db doesn't exist` | Run: `mysql -u root -p < database/hospital_db.sql` |
| `ClassNotFoundException` | Check JDBC driver in `lib/` folder |
| `Wrong password error` | Verify credentials in `src/config/db.properties` |
| `Compilation failed` | Ensure Java 11+: `java -version` |

---

## 📁 Project Files

```
✅ Database:        hospital_db.sql (schema + sample doctors)
✅ Source Code:     src/ (12 Java files)
✅ JDBC Driver:     lib/mysql-connector-j-9.6.0/
✅ Config:          src/config/db.properties
✅ Compiled:        bin/ (created after compilation)
```

---

## 🎯 Next Steps

**Learn More:**
- Read [README.md](README.md) for detailed documentation
- Check [SETUP_GUIDE.md](SETUP_GUIDE.md) for configuration options
- Review [SQL_QUERIES_REFERENCE.sql](SQL_QUERIES_REFERENCE.sql) for database queries

**Develop:**
- Create your own features
- Extend database tables
- Customize UI colors
- Add more validation

---

## ⚠️ Security Reminder

✅ **Do NOT commit your credentials to GitHub:**
- `.env` file is in `.gitignore`
- `src/config/db.properties` is in `.gitignore`
- Never push passwords or API keys

✅ **Share safely with team:**
- Share the `.sql` file only
- Team members create their own `db.properties`
- Each member sets their own password

---

**Ready?** Run: `java -cp "lib/mysql-connector-j-9.6.0/*:bin" Main`

Enjoy! 🎉

✅ **LinkedList Usage:** PatientDAO - patient queue simulation  
✅ **ArrayList Usage:** AppointmentDAO - appointment collection  
✅ **GridBagLayout:** Both forms for responsive design  
✅ **CardLayout:** MainDashboard for navigation  
✅ **JDBC PreparedStatement:** All SQL queries (injection-safe)  
✅ **Singleton Pattern:** DBConnection for connection management  
✅ **Error Handling:** Try-catch blocks in all DAO/DB operations  
✅ **Input Validation:** Phone uniqueness, age validation, required fields  

---

## 🎨 Color Scheme

| Element | Color |
|---------|-------|
| Header/Sidebar | #006699 (Deep Blue) |
| Buttons (Success) | #009966 (Green) |
| Background | #F5F8FC (Light Blue) |

---

## 📋 Sample Doctors

The database is pre-populated with 8 doctors:

1. Dr. Ramesh Sharma — Cardiology
2. Dr. Priya Gupta — Neurology
3. Dr. Anil Mehta — Orthopaedics
4. Dr. Sunita Joshi — Gynaecology & Obstetrics
5. Dr. Vikram Singh — General Medicine
6. Dr. Kavita Patel — Paediatrics
7. Dr. Ajay Verma — Urology
8. Dr. Meena Rao — Gastroenterology

---

## 🐛 Troubleshooting

| Problem | Solution |
|---------|----------|
| "Database Connection Failed" | Start MySQL: `brew services start mysql` |
| "Class not found: com.mysql.cj.jdbc.Driver" | Check `lib/mysql-connector-j-9.6.0/` exists |
| No dropdown options | Verify doctors table is populated: `SELECT COUNT(*) FROM doctors;` |
| Duplicate phone error | Phone must be unique per patient |
| Form validation fails | Fill all required fields (marked with *) |

---

## 📞 Testing Checklist

- [ ] MySQL running (`brew services list \| grep mysql`)
- [ ] Database created (`SHOW DATABASES;`)
- [ ] Password updated in `DBConnection.java`
- [ ] Recompiled Java files
- [ ] Application starts without errors
- [ ] Dashboard displays statistics
- [ ] Can register a new patient
- [ ] Can book an appointment
- [ ] View all patients table populates
- [ ] Token numbers auto-generate

---

## 📄 File Statistics

| Package | Files | Lines of Code |
|---------|-------|---------------|
| `db` | 1 | 27 |
| `models` | 2 | 125 |
| `dao` | 2 | 240 |
| `ui` | 3 | 495 |
| Root | 1 | 10 |
| Database | 1 | 60 |
| **TOTAL** | **10** | **957** |

---

## 🎓 Learning Resources

**Key Concepts Used:**
- JDBC Connection Management
- PreparedStatement for SQL Safety
- LinkedList vs ArrayList Trade-offs
- GridBagLayout for Complex Forms
- CardLayout for Multi-Panel UI
- Swing Component Hierarchy
- Event Handling (ActionListener, MouseListener)
- Lambda Expressions (Java 8)
- Stream API for Data Transformation

---

## ✨ Ready to Use!

Your Hospital Management System is fully built and compiled. All that's left is:

1. ✅ Start MySQL
2. ✅ Create the database
3. ✅ Update password in DBConnection.java
4. ✅ Recompile (optional if you changed password)
5. ✅ Run the application!

**Enjoy your HMS! 🏥**

---

**Version:** 1.0  
**Status:** ✅ PRODUCTION READY  
**Last Built:** April 16, 2026
