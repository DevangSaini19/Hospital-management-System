package ui;

import dao.PatientDAO;
import models.Patient;
import utils.ValidationUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Add Patient Form with Event Handlers
 * Demonstrates complete form submission with validation and database integration
 */
public class AddPatientFormWithEvents extends JPanel {

    private JTextField txtFirst, txtLast, txtAge, txtPhone, txtEmail;
    private JComboBox<String> cbGender, cbBlood, cbStatus;
    private JTextArea txtAddress, txtHistory;
    private JButton btnSubmit, btnClear;
    private JLabel lblMessage;
    private PatientDAO patientDAO;
    private MainDashboard dashboard;

    public AddPatientFormWithEvents(PatientDAO dao, MainDashboard dashboard) {
        this.patientDAO = dao;
        this.dashboard = dashboard;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 252));

        JLabel heading = new JLabel("  ➕ Register New Patient", SwingConstants.LEFT);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 0));
        add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: First Name and Last Name
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("First Name *"), gbc);
        gbc.gridx = 1;
        txtFirst = new JTextField(15);
        form.add(txtFirst, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Last Name *"), gbc);
        gbc.gridx = 3;
        txtLast = new JTextField(15);
        form.add(txtLast, gbc);

        // Row 1: Age and Gender
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Age *"), gbc);
        gbc.gridx = 1;
        txtAge = new JTextField(15);
        form.add(txtAge, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Gender *"), gbc);
        gbc.gridx = 3;
        cbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        form.add(cbGender, gbc);

        // Row 2: Blood Group and Phone
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Blood Group *"), gbc);
        gbc.gridx = 1;
        cbBlood = new JComboBox<>(new String[]{"A+","A-","B+","B-","AB+","AB-","O+","O-"});
        form.add(cbBlood, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Phone *"), gbc);
        gbc.gridx = 3;
        txtPhone = new JTextField(15);
        form.add(txtPhone, gbc);

        // Row 3: Email and Status
        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Email"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(15);
        form.add(txtEmail, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Status"), gbc);
        gbc.gridx = 3;
        cbStatus = new JComboBox<>(new String[]{"Active", "Discharged", "Critical"});
        form.add(cbStatus, gbc);

        // Row 4: Address
        gbc.gridx = 0; gbc.gridy = 4;
        form.add(new JLabel("Address"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtAddress = new JTextArea(3, 30);
        txtAddress.setLineWrap(true);
        form.add(new JScrollPane(txtAddress), gbc);
        gbc.gridwidth = 1;

        // Row 5: Medical History
        gbc.gridx = 0; gbc.gridy = 5;
        form.add(new JLabel("Medical History"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtHistory = new JTextArea(3, 30);
        txtHistory.setLineWrap(true);
        form.add(new JScrollPane(txtHistory), gbc);
        gbc.gridwidth = 1;

        // Row 6: Buttons
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        btnSubmit = new JButton("✓ Register Patient");
        btnSubmit.setBackground(new Color(76, 175, 80));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 12));
        btnSubmit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        form.add(btnSubmit, gbc);

        gbc.gridx = 2;
        btnClear = new JButton("✕ Clear");
        btnClear.setBackground(new Color(244, 67, 54));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFont(new Font("Arial", Font.BOLD, 12));
        btnClear.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        form.add(btnClear, gbc);

        // Row 7: Message Label
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 4;
        lblMessage = new JLabel("");
        lblMessage.setFont(new Font("Arial", Font.PLAIN, 12));
        form.add(lblMessage, gbc);

        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // ==================== EVENT LISTENERS ====================
        
        /**
         * SUBMIT BUTTON CLICK EVENT
         * Validates all input, creates Patient object, and calls DAO
         */
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddPatientSubmit();
            }
        });

        /**
         * CLEAR BUTTON CLICK EVENT
         * Clears all form fields
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
     * Handle Add Patient Form Submission
     * Validates input → Creates Patient object → Calls DAO → Shows result
     */
    private void handleAddPatientSubmit() {
        try {
            // Get form values
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

            // Frontend Validation
            if (firstName.isEmpty() || lastName.isEmpty() || ageStr.isEmpty() || phone.isEmpty()) {
                showErrorMessage("All required fields (*) must be filled!");
                return;
            }

            // Validate age is a number
            int age;
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                showErrorMessage("Age must be a valid number!");
                return;
            }

            // Validate age range
            if (!ValidationUtil.isValidAge(age)) {
                showErrorMessage("Age must be between 1 and 149!");
                return;
            }

            // Validate phone number format (10 digits)
            if (!ValidationUtil.isValidPhone(phone)) {
                showErrorMessage("Phone number must be exactly 10 digits!");
                return;
            }

            // Validate email if provided
            if (!email.isEmpty() && !ValidationUtil.isValidEmail(email)) {
                showErrorMessage("Invalid email format!");
                return;
            }

            // Create Patient object
            Patient patient = new Patient(
                firstName, lastName, age, gender, bloodGroup,
                phone, email, address, medicalHistory
            );
            patient.setStatus(status);

            // Call DAO to insert into database
            if (patientDAO.addPatient(patient)) {
                showSuccessMessage("✓ Patient registered successfully with ID: " + patient.getPatientId());
                clearFormFields();
                
                // Refresh dashboard or patient list
                if (dashboard != null) {
                    dashboard.refreshDashboard();
                }
            } else {
                showErrorMessage("Failed to register patient. Phone number may already exist!");
            }

        } catch (Exception e) {
            showErrorMessage("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Clear all form fields
     */
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

    /**
     * Show success message
     */
    private void showSuccessMessage(String message) {
        lblMessage.setText(message);
        lblMessage.setForeground(new Color(76, 175, 80)); // Green
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
