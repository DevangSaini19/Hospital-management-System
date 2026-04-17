package models;

import java.sql.Date;
import java.sql.Time;

public class Appointment {
    private int appointmentId;
    private int patientId;
    private int doctorId;
    private String patientName;
    private String doctorName;
    private Date appointmentDate;
    private Time appointmentTime;
    private String department;
    private String reason;
    private String status;
    private int tokenNumber;
    private String notes;

    // Constructor for new appointment
    public Appointment(int patientId, int doctorId, Date appointmentDate,
                       Time appointmentTime, String department, String reason) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.department = department;
        this.reason = reason;
        this.status = "Scheduled";
    }

    // Full constructor
    public Appointment(int appointmentId, int patientId, int doctorId,
                       String patientName, String doctorName,
                       Date appointmentDate, Time appointmentTime,
                       String department, String reason, String status,
                       int tokenNumber, String notes) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.department = department;
        this.reason = reason;
        this.status = status;
        this.tokenNumber = tokenNumber;
        this.notes = notes;
    }

    // Getters and Setters
    public int getAppointmentId() { return appointmentId; }
    public int getPatientId() { return patientId; }
    public int getDoctorId() { return doctorId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }
    public Time getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(Time appointmentTime) { this.appointmentTime = appointmentTime; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(int tokenNumber) { this.tokenNumber = tokenNumber; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
