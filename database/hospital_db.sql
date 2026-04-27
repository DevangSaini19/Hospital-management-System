CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;
CREATE TABLE IF NOT EXISTS doctors (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    consultation_fee DOUBLE NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


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


INSERT IGNORE INTO doctors (doctor_id, name, specialization, phone, consultation_fee) VALUES
(1, 'Dr. Ramesh Sharma', 'Cardiology', '9876500001', 500.0),
(2, 'Dr. Priya Gupta', 'Neurology', '9876500002', 600.0);


INSERT IGNORE INTO patients (patient_id, first_name, last_name, age, gender, blood_group, phone, email, address, status) VALUES
(1, 'Ravi', 'Sharma', 28, 'Male', 'O+', '9876543210', 'ravi.sharma@email.com', '123 Main Street, City', 'Active');

INSERT IGNORE INTO appointments (appointment_id, patient_id, doctor_id, appointment_date, appointment_time, department, reason, status, token_number) VALUES
(1, 1, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '10:00:00', 'Cardiology', 'Regular Checkup', 'Scheduled', 1),
(2, 2, 2, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '11:00:00', 'Neurology', 'Brain Scan', 'Scheduled', 2),
(3, 3, 1, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '14:00:00', 'Cardiology', 'Blood Pressure Check', 'Scheduled', 3),
(4, 4, 2, DATE_ADD(CURDATE(), INTERVAL 4 DAY), '15:30:00', 'Neurology', 'Consultation', 'Scheduled', 4),
(5, 5, 1, DATE_ADD(CURDATE(), INTERVAL 5 DAY), '09:00:00', 'Cardiology', 'ECG Test', 'Scheduled', 5);


SELECT 'Database initialized successfully!' as status, NOW() as timestamp;
