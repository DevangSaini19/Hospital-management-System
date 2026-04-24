package models;

import java.sql.Date;
import java.sql.Timestamp;

public class Bill {
    private int billId;
    private int patientId;
    private int doctorId;
    private int appointmentId;
    private String patientName;
    private String doctorName;
    private double doctorFee;
    private double medicineCharges;
    private double roomCharges;
    private double otherCharges;
    private double totalAmount;
    private Timestamp billDate;

    // Constructor for new bill
    public Bill(int patientId, int doctorId, double doctorFee,
                double medicineCharges, double roomCharges, double otherCharges) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.doctorFee = doctorFee;
        this.medicineCharges = medicineCharges;
        this.roomCharges = roomCharges;
        this.otherCharges = otherCharges;
        this.totalAmount = doctorFee + medicineCharges + roomCharges + otherCharges;
    }

    // Full constructor
    public Bill(int billId, int patientId, int doctorId, int appointmentId, String patientName,
                String doctorName, double doctorFee, double medicineCharges,
                double roomCharges, double otherCharges, double totalAmount, Timestamp billDate) {
        this.billId = billId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.doctorFee = doctorFee;
        this.medicineCharges = medicineCharges;
        this.roomCharges = roomCharges;
        this.otherCharges = otherCharges;
        this.totalAmount = totalAmount;
        this.billDate = billDate;
    }

    // Getters and Setters
    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public double getDoctorFee() {
        return doctorFee;
    }

    public void setDoctorFee(double doctorFee) {
        this.doctorFee = doctorFee;
    }

    public double getMedicineCharges() {
        return medicineCharges;
    }

    public void setMedicineCharges(double medicineCharges) {
        this.medicineCharges = medicineCharges;
    }

    public double getRoomCharges() {
        return roomCharges;
    }

    public void setRoomCharges(double roomCharges) {
        this.roomCharges = roomCharges;
    }

    public double getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(double otherCharges) {
        this.otherCharges = otherCharges;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Timestamp getBillDate() {
        return billDate;
    }

    public void setBillDate(Timestamp billDate) {
        this.billDate = billDate;
    }

    /**
     * Calculate total amount from all charges
     */
    public void calculateTotal() {
        this.totalAmount = doctorFee + medicineCharges + roomCharges + otherCharges;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId=" + billId +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", patientName='" + patientName + '\'' +
                ", doctorName='" + doctorName + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
