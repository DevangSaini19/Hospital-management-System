/**
 * SQL QUERIES REFERENCE FOR HOSPITAL MANAGEMENT SYSTEM
 * 
 * This file contains all SQL queries used in the application
 * with explanations and proper formatting
 */

// ==================== PATIENT QUERIES ====================

// 1. INSERT NEW PATIENT
INSERT INTO patients (first_name, last_name, age, gender, blood_group, phone, email, address, medical_history, status) 
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);

// 2. VALIDATE UNIQUE PHONE NUMBER
SELECT COUNT(*) FROM patients WHERE phone = ?;

// 3. GET PATIENT BY ID
SELECT * FROM patients WHERE patient_id = ?;

// 4. GET ALL PATIENTS (SORTED BY REGISTRATION DATE)
SELECT * FROM patients ORDER BY registration_date DESC;

// 5. SEARCH PATIENTS BY NAME OR PHONE
SELECT * FROM patients 
WHERE first_name LIKE ? OR last_name LIKE ? OR phone LIKE ? 
ORDER BY first_name ASC;

// 6. UPDATE PATIENT INFORMATION
UPDATE patients 
SET first_name=?, last_name=?, age=?, gender=?, blood_group=?, 
    email=?, address=?, medical_history=?, status=? 
WHERE patient_id=?;

// 7. GET TOTAL PATIENT COUNT
SELECT COUNT(*) FROM patients;

// 8. GET PATIENTS BY STATUS
SELECT * FROM patients WHERE status = ? ORDER BY registration_date DESC;

// ==================== APPOINTMENT QUERIES ====================

// 1. INSERT NEW APPOINTMENT
INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, 
                         department, reason, status, token_number) 
VALUES (?, ?, ?, ?, ?, ?, ?, ?);

// 2. VALIDATE PATIENT EXISTS
SELECT COUNT(*) FROM patients WHERE patient_id = ?;

// 3. VALIDATE DOCTOR EXISTS
SELECT COUNT(*) FROM doctors WHERE doctor_id = ?;

// 4. CHECK FOR DUPLICATE APPOINTMENT (SAME DOCTOR, SAME TIME)
SELECT COUNT(*) FROM appointments 
WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? 
AND status IN ('Scheduled', 'Pending');

// 5. GET ALL APPOINTMENTS WITH PATIENT AND DOCTOR NAMES (JOIN)
SELECT a.*, CONCAT(p.first_name,' ',p.last_name) AS patient_name, d.name AS doctor_name 
FROM appointments a 
JOIN patients p ON a.patient_id = p.patient_id 
JOIN doctors d ON a.doctor_id = d.doctor_id 
ORDER BY a.appointment_date DESC, a.appointment_time ASC;

// 6. GET APPOINTMENTS FOR SPECIFIC PATIENT
SELECT a.*, CONCAT(p.first_name,' ',p.last_name) AS patient_name, d.name AS doctor_name 
FROM appointments a 
JOIN patients p ON a.patient_id = p.patient_id 
JOIN doctors d ON a.doctor_id = d.doctor_id 
WHERE a.patient_id = ? 
ORDER BY a.appointment_date DESC;

// 7. GET TODAY'S APPOINTMENTS COUNT
SELECT COUNT(*) FROM appointments WHERE appointment_date = CURDATE();

// 8. GENERATE TOKEN NUMBER (COUNT EXISTING APPOINTMENTS FOR THE DATE)
SELECT COUNT(*) FROM appointments WHERE appointment_date = ?;

// 9. UPDATE APPOINTMENT STATUS
UPDATE appointments SET status = ? WHERE appointment_id = ?;

// 10. GET ALL DOCTORS FOR DROPDOWN
SELECT doctor_id, name, specialization FROM doctors ORDER BY name;

// 11. GET TOTAL APPOINTMENT COUNT
SELECT COUNT(*) FROM appointments;

// 12. GET DOCTOR AVAILABILITY (FREE SLOTS)
SELECT appointment_time FROM appointments 
WHERE doctor_id = ? AND appointment_date = ? 
AND status IN ('Scheduled', 'Pending');

// ==================== DOCTOR QUERIES ====================

// 1. GET ALL DOCTORS
SELECT doctor_id, name, specialization, phone, email FROM doctors ORDER BY name;

// 2. GET DOCTOR BY SPECIALIZATION
SELECT * FROM doctors WHERE specialization = ? ORDER BY name;

// 3. GET DOCTOR BY ID
SELECT * FROM doctors WHERE doctor_id = ?;

// ==================== STATISTICAL QUERIES ====================

// 1. TOTAL PATIENTS REGISTERED
SELECT COUNT(*) FROM patients;

// 2. PATIENTS BY STATUS
SELECT status, COUNT(*) as count FROM patients GROUP BY status;

// 3. APPOINTMENTS BY DOCTOR
SELECT d.doctor_id, d.name, COUNT(a.appointment_id) as total_appointments 
FROM doctors d 
LEFT JOIN appointments a ON d.doctor_id = a.doctor_id 
GROUP BY d.doctor_id, d.name 
ORDER BY total_appointments DESC;

// 4. APPOINTMENTS TODAY
SELECT COUNT(*) FROM appointments WHERE appointment_date = CURDATE();

// 5. BLOOD GROUP DISTRIBUTION
SELECT blood_group, COUNT(*) as count FROM patients GROUP BY blood_group;

// ==================== DATA INTEGRITY QUERIES ====================

// 1. CHECK ORPHANED APPOINTMENTS (NO PATIENT)
SELECT a.* FROM appointments a 
LEFT JOIN patients p ON a.patient_id = p.patient_id 
WHERE p.patient_id IS NULL;

// 2. CHECK ORPHANED APPOINTMENTS (NO DOCTOR)
SELECT a.* FROM appointments a 
LEFT JOIN doctors d ON a.doctor_id = d.doctor_id 
WHERE d.doctor_id IS NULL;

// 3. CHECK DUPLICATE PHONE NUMBERS
SELECT phone, COUNT(*) as count FROM patients 
GROUP BY phone HAVING count > 1;

// ==================== CLEANUP QUERIES ====================

// WARNING: Use these with caution!

// 1. DELETE APPOINTMENT BY ID (checks FK constraints)
DELETE FROM appointments WHERE appointment_id = ?;

// 2. DELETE PATIENT BY ID (cascades to appointments)
DELETE FROM patients WHERE patient_id = ?;

// 3. DELETE ALL CANCELLED APPOINTMENTS
DELETE FROM appointments WHERE status = 'Cancelled' AND appointment_date < CURDATE();
