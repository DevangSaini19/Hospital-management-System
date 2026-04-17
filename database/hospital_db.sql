CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

CREATE TABLE doctors (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100)
);

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
    status ENUM('Active', 'Discharged', 'Critical') DEFAULT 'Active'
);

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

INSERT INTO doctors (name, specialization, phone, email) VALUES
('Dr. Ramesh Sharma', 'Cardiology', '9876500001', 'ramesh@hospital.com'),
('Dr. Priya Gupta', 'Neurology', '9876500002', 'priya@hospital.com');
