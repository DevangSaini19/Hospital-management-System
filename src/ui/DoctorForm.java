package ui;

import dao.DoctorDAO;
import models.Doctor;
import utils.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Doctor Management Form
 * Handles adding, viewing, updating, and deleting doctors
 */
public class DoctorForm extends JPanel {

    private JTextField txtName, txtSpecialization, txtPhone, txtConsultationFee;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;
    private JLabel lblMessage;
    private JTable doctorTable;
    private DefaultTableModel tableModel;
    private DoctorDAO doctorDAO;
    private MainDashboard dashboard;
    private int selectedDoctorId = -1;

    private static final Color MAROON = new Color(139, 0, 0);
    private static final Color LIGHT_GRAY = new Color(245, 248, 252);
    private static final Color WHITE_BG = new Color(255, 255, 255);

    public DoctorForm(DoctorDAO dao, MainDashboard dashboard) {
        this.doctorDAO = dao;
        this.dashboard = dashboard;
        setLayout(new BorderLayout());
        setBackground(LIGHT_GRAY);

        // Header
        JLabel heading = new JLabel("  👨‍⚕️ Doctor Management", SwingConstants.LEFT);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(MAROON);
        heading.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 0));
        add(heading, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Form Panel (Left)
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.WEST);

        // Table Panel (Right)
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Load doctors
        refreshTable();
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(WHITE_BG);
        panel.setBorder(BorderFactory.createTitledBorder("Add/Update Doctor"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Name *"), gbc);
        gbc.gridx = 1;
        txtName = new JTextField(20);
        panel.add(txtName, gbc);

        // Specialization
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Specialization *"), gbc);
        gbc.gridx = 1;
        txtSpecialization = new JTextField(20);
        panel.add(txtSpecialization, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Phone (10 digits) *"), gbc);
        gbc.gridx = 1;
        txtPhone = new JTextField(20);
        panel.add(txtPhone, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Consultation Fee (₹) *"), gbc);
        gbc.gridx = 1;
        txtConsultationFee = new JTextField(20);
        txtConsultationFee.setText("0.0");
        panel.add(txtConsultationFee, gbc);

        // Message Label
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        lblMessage = new JLabel(" ");
        lblMessage.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblMessage.setForeground(MAROON);
        panel.add(lblMessage, gbc);

        // Buttons Panel
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        buttonsPanel.setOpaque(false);

        btnAdd = createButton("➕ Add Doctor", MAROON);
        btnUpdate = createButton("✏️ Update", new Color(70, 130, 180));
        btnDelete = createButton("🗑️ Delete", new Color(220, 20, 60));
        btnClear = createButton("🔄 Clear", new Color(100, 100, 100));

        btnAdd.addActionListener(e -> addDoctor());
        btnUpdate.addActionListener(e -> updateDoctor());
        btnDelete.addActionListener(e -> deleteDoctor());
        btnClear.addActionListener(e -> clearForm());

        buttonsPanel.add(btnAdd);
        buttonsPanel.add(btnUpdate);
        buttonsPanel.add(btnDelete);
        buttonsPanel.add(btnClear);

        panel.add(buttonsPanel, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE_BG);
        panel.setBorder(BorderFactory.createTitledBorder("Doctors List"));

        // Table
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"ID", "Name", "Specialization", "Phone", "Consultation Fee"});
        doctorTable = new JTable(tableModel);
        doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        doctorTable.getSelectionModel().addListSelectionListener(e -> selectDoctor());

        JScrollPane scrollPane = new JScrollPane(doctorTable);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void addDoctor() {
        if (!validateInputs()) {
            showMessage("✗ Please fill all required fields correctly!", true);
            return;
        }

        try {
            double fee = Double.parseDouble(txtConsultationFee.getText().trim());
            if (fee < 0) {
                showMessage("✗ Consultation fee cannot be negative!", true);
                return;
            }

            Doctor doctor = new Doctor(
                txtName.getText().trim(),
                txtSpecialization.getText().trim(),
                txtPhone.getText().trim(),
                fee
            );

            if (doctorDAO.addDoctor(doctor)) {
                showMessage("✓ Doctor added successfully!", false);
                clearForm();
                refreshTable();
            } else {
                showMessage("✗ Error adding doctor. Check if phone already exists.", true);
            }
        } catch (NumberFormatException e) {
            showMessage("✗ Please enter a valid consultation fee!", true);
        }
    }

    private void updateDoctor() {
        if (selectedDoctorId == -1) {
            showMessage("✗ Please select a doctor to update!", true);
            return;
        }

        if (!validateInputs()) {
            showMessage("✗ Please fill all required fields correctly!", true);
            return;
        }

        try {
            double fee = Double.parseDouble(txtConsultationFee.getText().trim());
            if (fee < 0) {
                showMessage("✗ Consultation fee cannot be negative!", true);
                return;
            }

            Doctor doctor = new Doctor(
                selectedDoctorId,
                txtName.getText().trim(),
                txtSpecialization.getText().trim(),
                txtPhone.getText().trim(),
                fee,
                null
            );

            if (doctorDAO.updateDoctor(doctor)) {
                showMessage("✓ Doctor updated successfully!", false);
                clearForm();
                refreshTable();
            } else {
                showMessage("✗ Error updating doctor.", true);
            }
        } catch (NumberFormatException e) {
            showMessage("✗ Please enter a valid consultation fee!", true);
        }
    }

    private void deleteDoctor() {
        if (selectedDoctorId == -1) {
            showMessage("✗ Please select a doctor to delete!", true);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this doctor?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (doctorDAO.deleteDoctor(selectedDoctorId)) {
                showMessage("✓ Doctor deleted successfully!", false);
                clearForm();
                refreshTable();
            } else {
                showMessage("✗ Error deleting doctor.", true);
            }
        }
    }

    private void selectDoctor() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedDoctorId = (int) tableModel.getValueAt(selectedRow, 0);
            Doctor doctor = doctorDAO.getDoctorById(selectedDoctorId);
            if (doctor != null) {
                txtName.setText(doctor.getName());
                txtSpecialization.setText(doctor.getSpecialization());
                txtPhone.setText(doctor.getPhone());
                txtConsultationFee.setText(String.valueOf(doctor.getConsultationFee()));
            }
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtSpecialization.setText("");
        txtPhone.setText("");
        txtConsultationFee.setText("0.0");
        lblMessage.setText(" ");
        selectedDoctorId = -1;
        doctorTable.clearSelection();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        ArrayList<Doctor> doctors = doctorDAO.getAllDoctors();
        for (Doctor doctor : doctors) {
            tableModel.addRow(new Object[]{
                doctor.getDoctorId(),
                doctor.getName(),
                doctor.getSpecialization(),
                doctor.getPhone(),
                String.format("₹ %.2f", doctor.getConsultationFee())
            });
        }
    }

    private boolean validateInputs() {
        if (txtName.getText().trim().isEmpty()) return false;
        if (txtSpecialization.getText().trim().isEmpty()) return false;
        if (!ValidationUtil.isValidPhone(txtPhone.getText().trim())) return false;
        try {
            double fee = Double.parseDouble(txtConsultationFee.getText().trim());
            return fee >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showMessage(String message, boolean isError) {
        lblMessage.setText(message);
        lblMessage.setForeground(isError ? new Color(220, 20, 60) : new Color(34, 139, 34));
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        return button;
    }

    /**
     * Public method to refresh the doctor list
     * Can be called from MainDashboard when needed
     */
    public void refreshTableData() {
        refreshTable();
    }
}
