package ui;

import dao.AppointmentDAO;
import dao.PatientDAO;
import models.Appointment;
import models.Patient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Appointment Form with Event Handlers
 * Demonstrates appointment booking with validation and duplicate prevention
 */
public class AppointmentFormWithEvents extends JPanel {

    private JComboBox<String> cbPatient, cbDoctor, cbTime;
    private JTextField txtDate;
    private JTextArea txtReason;
    private JButton btnBook, btnClear;
    private JLabel lblMessage;
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private MainDashboard dashboard;
    private ArrayList<String[]> doctors;

    public AppointmentFormWithEvents(AppointmentDAO apptDAO, PatientDAO patDAO, MainDashboard dashboard) {
        this.appointmentDAO = apptDAO;
        this.patientDAO = patDAO;
        this.dashboard = dashboard;
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

        // Patient Dropdown
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

        // Doctor Dropdown
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

        // Date
        gbc.gridx = 2; gbc.gridy = 1;
        form.add(new JLabel("Appointment Date *"), gbc);
        gbc.gridx = 3;
        txtDate = new JTextField(LocalDate.now().toString(), 12);
        form.add(txtDate, gbc);

        // Time Slot
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Time Slot *"), gbc);
        gbc.gridx = 1;
        String[] times = {"09:00","09:30","10:00","10:30","11:00","11:30",
                           "12:00","14:00","14:30","15:00","15:30","16:00","16:30"};
        cbTime = new JComboBox<>(times);
        form.add(cbTime, gbc);

        // Reason for appointment
        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Reason / Symptoms"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtReason = new JTextArea(3, 30);
        txtReason.setLineWrap(true);
        form.add(new JScrollPane(txtReason), gbc);
        gbc.gridwidth = 1;

        // Buttons
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        btnBook = new JButton("✓ Book Appointment");
        btnBook.setBackground(new Color(33, 150, 243));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFont(new Font("Arial", Font.BOLD, 12));
        btnBook.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        form.add(btnBook, gbc);

        gbc.gridx = 2;
        btnClear = new JButton("✕ Clear");
        btnClear.setBackground(new Color(244, 67, 54));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFont(new Font("Arial", Font.BOLD, 12));
        btnClear.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        form.add(btnClear, gbc);

        // Message Label
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4;
        lblMessage = new JLabel("");
        lblMessage.setFont(new Font("Arial", Font.PLAIN, 12));
        form.add(lblMessage, gbc);

        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // ==================== EVENT LISTENERS ====================

        /**
         * BOOK APPOINTMENT BUTTON CLICK EVENT
         * Validates all inputs and books appointment with duplicate prevention
         */
        btnBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleBookAppointmentSubmit();
            }
        });

        /**
         * CLEAR BUTTON CLICK EVENT
         */
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFormFields();
                lblMessage.setText("");
                lblMessage.setForeground(Color.BLACK);
            }
        });
    }

    /**
     * Handle Book Appointment Form Submission
     * Validates input → Checks for duplicates → Creates Appointment → Calls DAO
     */
    private void handleBookAppointmentSubmit() {
        try {
            // Extract patient ID from dropdown
            String patientStr = (String) cbPatient.getSelectedItem();
            if (patientStr == null || patientStr.isEmpty()) {
                showErrorMessage("Please select a patient!");
                return;
            }
            int patientId = Integer.parseInt(patientStr.split("\\[")[1].split("\\]")[0]);

            // Extract doctor ID from dropdown
            String doctorStr = (String) cbDoctor.getSelectedItem();
            if (doctorStr == null || doctorStr.isEmpty()) {
                showErrorMessage("Please select a doctor!");
                return;
            }
            int doctorId = Integer.parseInt(doctorStr.split("\\[")[1].split("\\]")[0]);

            // Get department from the selected doctor's specialization
            String department = "";
            for (String[] doctor : doctors) {
                if (Integer.parseInt(doctor[0]) == doctorId) {
                    department = doctor[2]; // Index 2 contains the specialization
                    break;
                }
            }
            
            String dateStr = txtDate.getText().trim();
            String timeStr = (String) cbTime.getSelectedItem();
            String reason = txtReason.getText().trim();

            // Validate date format (YYYY-MM-DD)
            if (dateStr.isEmpty()) {
                showErrorMessage("Please enter appointment date!");
                return;
            }

            Date appointmentDate;
            try {
                appointmentDate = Date.valueOf(dateStr);
            } catch (IllegalArgumentException e) {
                showErrorMessage("Invalid date format. Use YYYY-MM-DD!");
                return;
            }

            // Validate date is not in the past
            if (appointmentDate.before(Date.valueOf(LocalDate.now()))) {
                showErrorMessage("Appointment date cannot be in the past!");
                return;
            }

            // Convert time string to Time object
            Time appointmentTime = Time.valueOf(timeStr + ":00");

            // Create Appointment object
            Appointment appointment = new Appointment(
                patientId, doctorId, appointmentDate, appointmentTime,
                department, reason
            );

            // Call DAO to book appointment (includes validation and duplicate check)
            if (appointmentDAO.bookAppointment(appointment)) {
                showSuccessMessage("✓ Appointment booked successfully! Token: " + appointment.getTokenNumber());
                clearFormFields();

                // Refresh dashboard
                if (dashboard != null) {
                    dashboard.refreshDashboard();
                }
            } else {
                showErrorMessage("Failed to book appointment. Check if doctor is available!");
            }

        } catch (NumberFormatException e) {
            showErrorMessage("Invalid input format!");
            e.printStackTrace();
        } catch (Exception e) {
            showErrorMessage("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Clear all form fields
     */
    private void clearFormFields() {
        if (cbPatient.getItemCount() > 0) cbPatient.setSelectedIndex(0);
        if (cbDoctor.getItemCount() > 0) cbDoctor.setSelectedIndex(0);
        if (cbTime.getItemCount() > 0) cbTime.setSelectedIndex(0);
        txtDate.setText(LocalDate.now().toString());
        txtReason.setText("");
    }

    /**
     * Show success message
     */
    private void showSuccessMessage(String message) {
        lblMessage.setText(message);
        lblMessage.setForeground(new Color(33, 150, 243)); // Blue
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show error message
     */
    private void showErrorMessage(String message) {
        lblMessage.setText(message);
        lblMessage.setForeground(new Color(244, 67, 54)); // Red
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
