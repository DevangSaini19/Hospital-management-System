package ui;

import dao.PatientDAO;
import models.Patient;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class AddPatientForm extends JPanel {

    private JTextField txtFirst, txtLast, txtAge, txtPhone, txtEmail;
    private JComboBox<String> cbGender, cbBlood, cbStatus;
    private JTextArea txtAddress, txtHistory;
    private PatientDAO patientDAO;
    private MainDashboard dashboard;

    public AddPatientForm(PatientDAO dao, MainDashboard dashboard) {
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

        // Row 0
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

        // Row 1
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

        // Row 2
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Blood Group *"), gbc);
        gbc.gridx = 1;
        cbBlood = new JComboBox<>(new String[]{"A+","A-","B+","B-","AB+","AB-","O+","O-"});
        form.add(cbBlood, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Phone (10 digits) *"), gbc);
        gbc.gridx = 3;
        txtPhone = new JTextField(15);
        txtPhone.setToolTipText("Enter 10-digit phone number (e.g., 9876543210)");
        form.add(txtPhone, gbc);

        // Row 3
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

        // Row 4 — Address
        gbc.gridx = 0; gbc.gridy = 4;
        form.add(new JLabel("Address"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtAddress = new JTextArea(3, 30);
        txtAddress.setLineWrap(true);
        form.add(new JScrollPane(txtAddress), gbc);
        gbc.gridwidth = 1;

        // Row 5 — Medical History
        gbc.gridx = 0; gbc.gridy = 5;
        form.add(new JLabel("Medical History"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtHistory = new JTextArea(3, 30);
        txtHistory.setLineWrap(true);
        form.add(new JScrollPane(txtHistory), gbc);
        gbc.gridwidth = 1;

        // Buttons
        gbc.gridx = 1; gbc.gridy = 6; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        btnPanel.setOpaque(false);

        JButton btnSave = new JButton("✅ Save Patient");
        btnSave.setBackground(new Color(0, 153, 76));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> savePatient());

        JButton btnClear = new JButton("🔄 Clear Form");
        btnClear.setBackground(new Color(150, 150, 150));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClear.setFocusPainted(false);
        btnClear.addActionListener(e -> clearForm());

        btnPanel.add(btnSave);
        btnPanel.add(btnClear);
        form.add(btnPanel, gbc);

        add(new JScrollPane(form), BorderLayout.CENTER);
    }

    private void savePatient() {
        // Validation
        if (txtFirst.getText().trim().isEmpty() || txtLast.getText().trim().isEmpty() ||
            txtAge.getText().trim().isEmpty() || txtPhone.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields (*)", 
                                          "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate phone format before attempting to save
        String phone = txtPhone.getText().trim();
        if (!phone.matches("^[0-9]{10}$")) {
            JOptionPane.showMessageDialog(this, 
                "Invalid phone number!\n\n" +
                "Phone must be exactly 10 digits (e.g., 9876543210)\n" +
                "Currently entered: " + phone,
                "Phone Number Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int age = Integer.parseInt(txtAge.getText().trim());
            if (age <= 0 || age >= 150) throw new NumberFormatException();

            Patient p = new Patient(
                txtFirst.getText().trim(),
                txtLast.getText().trim(),
                age,
                (String) cbGender.getSelectedItem(),
                (String) cbBlood.getSelectedItem(),
                txtPhone.getText().trim(),
                txtEmail.getText().trim(),
                txtAddress.getText().trim(),
                txtHistory.getText().trim()
            );

            if (patientDAO.addPatient(p)) {
                JOptionPane.showMessageDialog(this,
                    "✅ Patient Registered Successfully!\nPatient ID: " + p.getPatientId(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                // Refresh dashboard and all patient list to show updated data
                dashboard.refreshDashboard();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ Failed to save patient!\n\n" +
                    "Possible reasons:\n" +
                    "• Phone number already exists (must be unique)\n" +
                    "• Database connection error\n" +
                    "• Invalid data in one of the fields\n\n" +
                    "Please try again with a different phone number.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid age (1-149).",
                "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearForm() {
        txtFirst.setText(""); txtLast.setText(""); txtAge.setText("");
        txtPhone.setText(""); txtEmail.setText("");
        txtAddress.setText(""); txtHistory.setText("");
        cbGender.setSelectedIndex(0); cbBlood.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
    }
}
