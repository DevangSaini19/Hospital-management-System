package ui;

import dao.AppointmentDAO;
import dao.PatientDAO;
import models.Appointment;
import models.Patient;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class AppointmentForm extends JPanel {

    private JComboBox<String> cbPatient, cbDoctor, cbTime, cbStatus;
    private JTextField txtDate;
    private JTextArea txtReason;
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private JTable appointmentTable;
    private ArrayList<String[]> doctors;

    public AppointmentForm(AppointmentDAO apptDAO, PatientDAO patDAO, MainDashboard dashboard) {
        this.appointmentDAO = apptDAO;
        this.patientDAO = patDAO;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 252));

        JLabel heading = new JLabel("  📅 Book Appointment", SwingConstants.LEFT);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 0));
        add(heading, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Patient dropdown
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Select Patient *"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        LinkedList<Patient> patients = patientDAO.getAllPatients();
        String[] patientItems = patients.stream()
            .map(p -> "[" + p.getPatientId() + "] " + p.getFullName())
            .toArray(String[]::new);
        cbPatient = new JComboBox<>(patientItems);
        form.add(cbPatient, gbc);
        gbc.gridwidth = 1;

        // Doctor dropdown
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Select Doctor *"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        doctors = appointmentDAO.getAllDoctors();
        String[] doctorItems = doctors.stream()
            .map(d -> "[" + d[0] + "] " + d[1] + " — " + d[2])
            .toArray(String[]::new);
        cbDoctor = new JComboBox<>(doctorItems);
        form.add(cbDoctor, gbc);
        gbc.gridwidth = 1;

        // Date - using JSpinner with SpinnerDateModel
        gbc.gridx = 2; gbc.gridy = 1;
        form.add(new JLabel("Appointment Date *"), gbc);
        gbc.gridx = 3;
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new java.util.Date());
        form.add(dateSpinner, gbc);
        txtDate = new JTextField(); // Keep for reference but we'll get value from spinner

        // Time
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Time Slot *"), gbc);
        gbc.gridx = 1;
        String[] times = {"09:00","09:30","10:00","10:30","11:00","11:30",
                           "12:00","14:00","14:30","15:00","15:30","16:00","16:30"};
        cbTime = new JComboBox<>(times);
        form.add(cbTime, gbc);

        // Reason
        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Reason / Symptoms"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtReason = new JTextArea(3, 30);
        txtReason.setLineWrap(true);
        form.add(new JScrollPane(txtReason), gbc);
        gbc.gridwidth = 1;

        // Button
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton btnBook = new JButton("📅 Book Appointment");
        btnBook.setBackground(new Color(0, 102, 153));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBook.setFocusPainted(false);
        btnBook.addActionListener(e -> bookAppointment(patients, doctors, dateSpinner));
        form.add(btnBook, gbc);

        // Appointment list table
        JPanel tablePanel = buildAppointmentTable();

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, form, tablePanel);
        split.setDividerLocation(320);
        add(split, BorderLayout.CENTER);
    }

    private void bookAppointment(LinkedList<Patient> patients, ArrayList<String[]> doctors, JSpinner dateSpinner) {
        try {
            int patIdx = cbPatient.getSelectedIndex();
            int docIdx = cbDoctor.getSelectedIndex();

            int patientId = patients.get(patIdx).getPatientId();
            int doctorId = Integer.parseInt(doctors.get(docIdx)[0]);
            // Get date from spinner and convert to java.sql.Date
            java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
            Date date = new Date(selectedDate.getTime());
            Time time = Time.valueOf(cbTime.getSelectedItem() + ":00");
            // Get department from the selected doctor's specialization
            String dept = doctors.get(docIdx)[2];
            String reason = txtReason.getText().trim();

            Appointment appt = new Appointment(patientId, doctorId, date, time, dept, reason);

            if (appointmentDAO.bookAppointment(appt)) {
                JOptionPane.showMessageDialog(this,
                    "✅ Appointment Booked!\nToken Number: " + appt.getTokenNumber(),
                    "Booked", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildAppointmentTable() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel("  All Appointments", SwingConstants.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 0));
        panel.add(lbl, BorderLayout.NORTH);

        String[] cols = {"ID", "Patient", "Doctor", "Date", "Time", "Department", "Token", "Status"};
        appointmentTable = new JTable(getAppointmentData(), cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        appointmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        appointmentTable.setRowHeight(26);
        panel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);
        return panel;
    }

    private Object[][] getAppointmentData() {
        ArrayList<Appointment> list = appointmentDAO.getAllAppointments();
        // Sort by appointment ID in ascending order
        Collections.sort(list, (a1, a2) -> Integer.compare(a1.getAppointmentId(), a2.getAppointmentId()));
        return list.stream().map(a -> new Object[]{
            a.getAppointmentId(), a.getPatientName(), a.getDoctorName(),
            a.getAppointmentDate(), a.getAppointmentTime(),
            a.getDepartment(), a.getTokenNumber(), a.getStatus()
        }).toArray(Object[][]::new);
    }

    private void refreshTable() {
        String[] cols = {"ID", "Patient", "Doctor", "Date", "Time", "Department", "Token", "Status"};
        appointmentTable.setModel(new javax.swing.table.DefaultTableModel(getAppointmentData(), cols));
    }
}
