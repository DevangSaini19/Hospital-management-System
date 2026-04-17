# Hospital Management System - Complete Code Reference & Examples

## 📚 Quick Reference Guide

### 1. PATIENT MODULE - Complete Example

```java
// ==================== ADDING A PATIENT ====================

import dao.PatientDAO;
import models.Patient;
import utils.ValidationUtil;

public class PatientExample {
    public static void main(String[] args) {
        // Initialize DAO
        PatientDAO patientDAO = new PatientDAO();

        // Method 1: Create and add patient with all details
        Patient patient1 = new Patient(
            "Ramesh",              // First Name
            "Kumar",               // Last Name
            45,                    // Age
            "Male",                // Gender
            "AB+",                 // Blood Group
            "9876543210",          // Phone
            "ramesh@email.com",    // Email
            "123 Main Street, City", // Address
            "Diabetes, Hypertension" // Medical History
        );
        patient1.setStatus("Active"); // Set status

        // Add to database
        if (patientDAO.addPatient(patient1)) {
            System.out.println("✓ Patient added successfully!");
            System.out.println("Patient ID: " + patient1.getPatientId());
        } else {
            System.out.println("✗ Failed to add patient");
        }

        // ==================== RETRIEVING PATIENTS ====================

        // Get all patients
        LinkedList<Patient> allPatients = patientDAO.getAllPatients();
        for (Patient p : allPatients) {
            System.out.println(p); // Uses toString() method
        }

        // Search patient by name/phone
        LinkedList<Patient> searchResults = patientDAO.searchPatients("Ramesh");
        System.out.println("Found: " + searchResults.size() + " patients");

        // Get specific patient by ID
        Patient patient = patientDAO.getPatientById(1);
        if (patient != null) {
            System.out.println("Patient Name: " + patient.getFullName());
            System.out.println("Phone: " + patient.getPhone());
            System.out.println("Blood Group: " + patient.getBloodGroup());
        }

        // ==================== VALIDATION EXAMPLES ====================

        // Check if phone already exists
        if (patientDAO.isPhoneExists("9876543210")) {
            System.out.println("Phone already registered");
        }

        // Using ValidationUtil
        if (ValidationUtil.isValidPhone("9876543210")) {
            System.out.println("Valid phone number");
        }

        if (ValidationUtil.isValidEmail("user@email.com")) {
            System.out.println("Valid email");
        }

        if (ValidationUtil.isValidAge(45)) {
            System.out.println("Valid age");
        }

        // ==================== UPDATE PATIENT ====================

        Patient existingPatient = patientDAO.getPatientById(1);
        if (existingPatient != null) {
            existingPatient.setPhone("9999999999");
            existingPatient.setStatus("Critical");
            if (patientDAO.updatePatient(existingPatient)) {
                System.out.println("✓ Patient updated successfully");
            }
        }

        // ==================== STATISTICS ====================

        int totalPatients = patientDAO.getTotalPatientCount();
        System.out.println("Total patients: " + totalPatients);
    }
}
```

---

### 2. APPOINTMENT MODULE - Complete Example

```java
// ==================== BOOKING AN APPOINTMENT ====================

import dao.AppointmentDAO;
import models.Appointment;
import java.sql.Date;
import java.sql.Time;

public class AppointmentExample {
    public static void main(String[] args) {
        // Initialize DAO
        AppointmentDAO appointmentDAO = new AppointmentDAO();

        // Method 1: Create and book appointment
        Appointment appointment = new Appointment(
            1,                                   // Patient ID (must exist)
            2,                                   // Doctor ID (must exist)
            Date.valueOf("2024-05-25"),         // Appointment Date (YYYY-MM-DD)
            Time.valueOf("10:00:00"),           // Appointment Time (HH:MM:SS)
            "Cardiology",                       // Department
            "Regular checkup and consultation"  // Reason
        );

        // Book appointment (includes all validations)
        if (appointmentDAO.bookAppointment(appointment)) {
            System.out.println("✓ Appointment booked successfully!");
            System.out.println("Token Number: " + appointment.getTokenNumber());
        } else {
            System.out.println("✗ Failed to book appointment");
            System.out.println("Possible reasons:");
            System.out.println("- Invalid patient ID");
            System.out.println("- Invalid doctor ID");
            System.out.println("- Doctor already booked at this time");
        }

        // ==================== RETRIEVING APPOINTMENTS ====================

        // Get all appointments with patient and doctor details
        ArrayList<Appointment> allAppointments = appointmentDAO.getAllAppointments();
        for (Appointment appt : allAppointments) {
            System.out.println("Appointment ID: " + appt.getAppointmentId());
            System.out.println("Patient: " + appt.getPatientName());
            System.out.println("Doctor: " + appt.getDoctorName());
            System.out.println("Date: " + appt.getAppointmentDate());
            System.out.println("Time: " + appt.getAppointmentTime());
            System.out.println("Token: " + appt.getTokenNumber());
            System.out.println("Status: " + appt.getStatus());
            System.out.println("---");
        }

        // Get appointments for specific patient
        ArrayList<Appointment> patientAppointments = appointmentDAO.getPatientAppointments(1);
        System.out.println("Patient has " + patientAppointments.size() + " appointments");

        // Get today's appointment count
        int todayAppointments = appointmentDAO.getTodayAppointmentsCount();
        System.out.println("Appointments today: " + todayAppointments);

        // ==================== VALIDATION EXAMPLES ====================

        // Check if patient exists
        if (appointmentDAO.isPatientExists(1)) {
            System.out.println("Patient exists");
        }

        // Check if doctor exists
        if (appointmentDAO.isDoctorExists(2)) {
            System.out.println("Doctor exists");
        }

        // Check for duplicate appointment
        if (appointmentDAO.isDuplicateAppointment(2, Date.valueOf("2024-05-25"), Time.valueOf("10:00:00"))) {
            System.out.println("Doctor is already booked at this time");
        }

        // ==================== GET DOCTORS FOR DROPDOWN ====================

        ArrayList<String[]> doctors = appointmentDAO.getAllDoctors();
        for (String[] doc : doctors) {
            System.out.println("[ID: " + doc[0] + "] " + doc[1] + " - " + doc[2]);
        }

        // ==================== UPDATE APPOINTMENT STATUS ====================

        // Valid statuses: Scheduled, Completed, Cancelled, Pending
        if (appointmentDAO.updateAppointmentStatus(1, "Completed")) {
            System.out.println("✓ Appointment marked as completed");
        }

        // ==================== STATISTICS ====================

        int totalAppointments = appointmentDAO.getTotalAppointmentCount();
        System.out.println("Total appointments: " + totalAppointments);
    }
}
```

---

### 3. UI FORM EVENT HANDLING - Complete Example

```java
// ==================== BUTTON CLICK EVENT - ADD PATIENT ====================

private void handleAddPatientSubmit() {
    try {
        // Step 1: Get all form values
        String firstName = txtFirst.getText().trim();
        String lastName = txtLast.getText().trim();
        String ageStr = txtAge.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String gender = (String) cbGender.getSelectedItem();
        String bloodGroup = (String) cbBlood.getSelectedItem();
        String address = txtAddress.getText().trim();
        String medicalHistory = txtHistory.getText().trim();
        String status = (String) cbStatus.getSelectedItem();

        // Step 2: Validate required fields
        if (firstName.isEmpty() || lastName.isEmpty() || ageStr.isEmpty() || phone.isEmpty()) {
            showErrorMessage("All required fields (*) must be filled!");
            return;
        }

        // Step 3: Parse and validate age
        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            showErrorMessage("Age must be a valid number!");
            return;
        }

        // Step 4: Use ValidationUtil for format validation
        if (!ValidationUtil.isValidAge(age)) {
            showErrorMessage("Age must be between 1 and 149!");
            return;
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            showErrorMessage("Phone must be exactly 10 digits!");
            return;
        }

        if (!email.isEmpty() && !ValidationUtil.isValidEmail(email)) {
            showErrorMessage("Invalid email format!");
            return;
        }

        // Step 5: Create Patient object
        Patient patient = new Patient(
            firstName, lastName, age, gender, bloodGroup,
            phone, email, address, medicalHistory
        );
        patient.setStatus(status);

        // Step 6: Call DAO
        if (patientDAO.addPatient(patient)) {
            showSuccessMessage("✓ Patient registered! ID: " + patient.getPatientId());
            clearFormFields();
            dashboard.refreshPatientList(); // Refresh UI
        } else {
            showErrorMessage("Failed to add patient. Phone may already exist!");
        }

    } catch (Exception e) {
        showErrorMessage("Error: " + e.getMessage());
        e.printStackTrace();
    }
}

// ==================== SHOW ERROR MESSAGE ====================

private void showErrorMessage(String message) {
    lblMessage.setText(message);
    lblMessage.setForeground(new Color(244, 67, 54)); // Red
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
}

// ==================== SHOW SUCCESS MESSAGE ====================

private void showSuccessMessage(String message) {
    lblMessage.setText(message);
    lblMessage.setForeground(new Color(76, 175, 80)); // Green
    JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
}

// ==================== CLEAR FORM FIELDS ====================

private void clearFormFields() {
    txtFirst.setText("");
    txtLast.setText("");
    txtAge.setText("");
    txtPhone.setText("");
    txtEmail.setText("");
    txtAddress.setText("");
    txtHistory.setText("");
    cbGender.setSelectedIndex(0);
    cbBlood.setSelectedIndex(0);
    cbStatus.setSelectedIndex(0);
}
```

---

### 4. DATABASE CONNECTION - Secure Configuration

```java
// ==================== SECURE DATABASE CONNECTION ====================

// Method 1: Using Environment Variables (Production)
// Terminal:
// export DB_URL="jdbc:mysql://localhost:3306/hospital_db"
// export DB_USER="root"
// export DB_PASSWORD="secure_password"

// Method 2: Using db.properties File (Development)
// File: src/config/db.properties
// db.url=jdbc:mysql://localhost:3306/hospital_db
// db.user=root
// db.password=secure_password

// Method 3: Test Connection
public static void main(String[] args) {
    try {
        if (DBConnection.testConnection()) {
            System.out.println("✓ Connected to database successfully!");
        } else {
            System.out.println("✗ Failed to connect to database");
        }
    } finally {
        DBConnection.closeConnection();
    }
}
```

---

### 5. INPUT VALIDATION - Complete Examples

```java
// ==================== VALIDATION EXAMPLES ====================

import utils.ValidationUtil;

public class ValidationExamples {
    public static void main(String[] args) {
        
        // Validate name
        if (ValidationUtil.isValidName("Ramesh Kumar")) {
            System.out.println("✓ Valid name");
        }
        
        // Validate phone (10 digits)
        if (ValidationUtil.isValidPhone("9876543210")) {
            System.out.println("✓ Valid phone");
        }
        
        // Validate email
        if (ValidationUtil.isValidEmail("user@email.com")) {
            System.out.println("✓ Valid email");
        }
        
        // Validate age
        if (ValidationUtil.isValidAge(45)) {
            System.out.println("✓ Valid age");
        }
        
        // Validate blood group
        if (ValidationUtil.isValidBloodGroup("AB+")) {
            System.out.println("✓ Valid blood group");
        }
        
        // Validate gender
        if (ValidationUtil.isValidGender("Male")) {
            System.out.println("✓ Valid gender");
        }
        
        // Sanitize string (trim whitespace)
        String sanitized = ValidationUtil.sanitizeString("  user name  ");
        System.out.println("Sanitized: '" + sanitized + "'"); // Output: 'user name'
    }
}
```

---

## 🔍 Error Handling Patterns

```java
// ==================== PROPER ERROR HANDLING ====================

try {
    Connection conn = DBConnection.getConnection();
    PreparedStatement pst = conn.prepareStatement("SELECT * FROM patients WHERE patient_id = ?");
    pst.setInt(1, patientId);
    ResultSet rs = pst.executeQuery();
    
    if (rs.next()) {
        // Process result
    }
    
} catch (ClassNotFoundException e) {
    // MySQL driver not found
    System.err.println("MySQL driver not found: " + e.getMessage());
    
} catch (SQLException e) {
    // Database error
    if (e.getErrorCode() == 1062) {
        // Duplicate entry error
        System.err.println("Duplicate entry: " + e.getMessage());
    } else {
        System.err.println("Database error: " + e.getMessage());
    }
    e.printStackTrace();
    
} finally {
    // Always close resources
    DBConnection.closeConnection();
}
```

---

## 📊 Data Structure Usage

```java
// ==================== LINKEDLIST - PATIENT QUEUE ====================

// PatientDAO uses LinkedList for queue operations

// Peek (view without removing)
Patient nextPatient = patientDAO.peekNextInQueue();

// Serve (remove from queue)
Patient servedPatient = patientDAO.serveNextPatient();

// ==================== ARRAYLIST - APPOINTMENT LIST ====================

// AppointmentDAO uses ArrayList for flexible storage
ArrayList<Appointment> appointments = appointmentDAO.getAllAppointments();

// Add appointment
appointments.add(new Appointment(...));

// Iterate
for (Appointment appt : appointments) {
    System.out.println(appt.getAppointmentId());
}
```

---

## 🚨 Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| Connection timeout | Check MySQL server is running |
| Duplicate phone | Phone already exists in database |
| Doctor not available | Doctor booked at that time |
| Invalid patient ID | Patient doesn't exist in database |
| Password exposed in GitHub | Immediately rotate password and remove from repo |
| db.properties not found | Create file or use environment variables |

---

## 📝 Testing Checklist

- [ ] Database connection working
- [ ] Add patient with valid data
- [ ] Prevent duplicate phone numbers
- [ ] Add patient with invalid data (should fail)
- [ ] Book appointment for valid patient and doctor
- [ ] Prevent duplicate appointments
- [ ] Book appointment with invalid patient (should fail)
- [ ] Book appointment with invalid doctor (should fail)
- [ ] Search patients works
- [ ] Get all appointments with JOIN query works
- [ ] Update appointment status works

---

**Version:** 1.0.0  
**Last Updated:** April 2024
