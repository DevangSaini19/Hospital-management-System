package models;

import java.sql.Date;

public class Patient {
    private int patientId;
    private String firstName;
    private String lastName;
    private int age;
    private String gender;
    private String bloodGroup;
    private String phone;
    private String email;
    private String address;
    private String medicalHistory;
    private Date registrationDate;
    private String status;

    // Constructor for new patient (no ID yet)
    public Patient(String firstName, String lastName, int age, String gender,
                   String bloodGroup, String phone, String email,
                   String address, String medicalHistory) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.medicalHistory = medicalHistory;
        this.status = "Active";
    }

    // Full constructor
    public Patient(int patientId, String firstName, String lastName, int age,
                   String gender, String bloodGroup, String phone, String email,
                   String address, String medicalHistory, Date registrationDate, String status) {
        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.medicalHistory = medicalHistory;
        this.registrationDate = registrationDate;
        this.status = status;
    }

    // Getters and Setters for all fields
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getFullName() { return firstName + " " + lastName; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
    public Date getRegistrationDate() { return registrationDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "[" + patientId + "] " + getFullName() + " | Age: " + age + " | " + phone;
    }
}
