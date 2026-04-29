package ui;

import dao.BillDAO;
import dao.PatientDAO;
import dao.DoctorDAO;
import models.Bill;
import models.Patient;
import models.Doctor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Billing Form - Fixed Version
 * Proper billing workflow with direct doctor selection and auto-filled fees
 */
public class BillingForm extends JPanel {

    private JComboBox<String> cbPatient, cbDoctor, cbPaymentMethod, cbPaymentStatus;
    private JTextField txtDoctorFee, txtMedicineCharges, txtRoomCharges, txtOtherCharges, txtTotal;
    private JButton btnGenerateBill, btnClear, btnPrint, btnViewBills;
    private JLabel lblMessage;
    private JTable billTable;
    private DefaultTableModel tableModel;
    private BillDAO billDAO;
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private MainDashboard dashboard;

    private static final Color MAROON = new Color(139, 0, 0);
    private static final Color LIGHT_GRAY = new Color(245, 248, 252);
    private static final Color WHITE_BG = new Color(255, 255, 255);

    public BillingForm(BillDAO billDAO, PatientDAO patientDAO, DoctorDAO doctorDAO, MainDashboard dashboard) {
        this.billDAO = billDAO;
        this.patientDAO = patientDAO;
        this.doctorDAO = doctorDAO;
        this.dashboard = dashboard;

        setLayout(new BorderLayout());
        setBackground(LIGHT_GRAY);

        // Header
        JLabel heading = new JLabel("  💳 Billing System", SwingConstants.LEFT);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(MAROON);
        heading.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 0));
        add(heading, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Form Panel (Top)
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.NORTH);

        // Table Panel (Bottom)
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(WHITE_BG);
        panel.setBorder(BorderFactory.createTitledBorder("Generate New Bill"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Patient Selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Select Patient *"), gbc);
        gbc.gridx = 1;
        cbPatient = new JComboBox<>();
        cbPatient.addActionListener(e -> onPatientSelected());
        panel.add(cbPatient, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Select Doctor *"), gbc);
        gbc.gridx = 3;
        cbDoctor = new JComboBox<>();
        cbDoctor.addActionListener(e -> onDoctorSelected());
        panel.add(cbDoctor, gbc);

        // Row 1: Doctor Fee (Auto-filled)
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Doctor Fee (₹)"), gbc);
        gbc.gridx = 1;
        txtDoctorFee = new JTextField(15);
        txtDoctorFee.setText("0.0");
        txtDoctorFee.setEditable(false);
        txtDoctorFee.setBackground(new Color(230, 230, 250));
        panel.add(txtDoctorFee, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Medicine Charges"), gbc);
        gbc.gridx = 3;
        txtMedicineCharges = new JTextField(15);
        txtMedicineCharges.setText("0.0");
        txtMedicineCharges.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTotal();
            }
        });
        panel.add(txtMedicineCharges, gbc);

        // Row 2: Room & Other Charges
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Room Charges"), gbc);
        gbc.gridx = 1;
        txtRoomCharges = new JTextField(15);
        txtRoomCharges.setText("0.0");
        txtRoomCharges.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTotal();
            }
        });
        panel.add(txtRoomCharges, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Other Charges"), gbc);
        gbc.gridx = 3;
        txtOtherCharges = new JTextField(15);
        txtOtherCharges.setText("0.0");
        txtOtherCharges.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTotal();
            }
        });
        panel.add(txtOtherCharges, gbc);

        // Row 3: Total
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Total Amount"), gbc);
        gbc.gridx = 1;
        txtTotal = new JTextField(15);
        txtTotal.setText("0.0");
        txtTotal.setEditable(false);
        txtTotal.setBackground(new Color(230, 230, 250));
        panel.add(txtTotal, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 2;
        lblMessage = new JLabel(" ");
        lblMessage.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblMessage.setForeground(MAROON);
        panel.add(lblMessage, gbc);

        // Row 4: Payment Method & Payment Status
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Payment Method *"), gbc);
        gbc.gridx = 1;
        cbPaymentMethod = new JComboBox<>(new String[]{"Cash", "UPI", "Insurance"});
        cbPaymentMethod.setSelectedIndex(0);
        panel.add(cbPaymentMethod, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Payment Status *"), gbc);
        gbc.gridx = 3;
        cbPaymentStatus = new JComboBox<>(new String[]{"Unpaid", "Partial Paid", "Paid"});
        cbPaymentStatus.setSelectedIndex(0);
        panel.add(cbPaymentStatus, gbc);

        // Row 5: Buttons
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        buttonsPanel.setOpaque(false);

        btnGenerateBill = createButton("💰 Generate Bill", MAROON);
        btnPrint = createButton("🖨️ Print", new Color(70, 130, 180));
        btnViewBills = createButton("👁️ View All Bills", new Color(34, 139, 34));
        btnClear = createButton("🔄 Clear", new Color(100, 100, 100));

        btnGenerateBill.addActionListener(e -> generateBill());
        btnPrint.addActionListener(e -> printBill());
        btnViewBills.addActionListener(e -> refreshBillsTable());
        btnClear.addActionListener(e -> clearForm());

        buttonsPanel.add(btnGenerateBill);
        buttonsPanel.add(btnPrint);
        buttonsPanel.add(btnViewBills);
        buttonsPanel.add(btnClear);

        panel.add(buttonsPanel, gbc);

        // Load patients and doctors
        loadPatients();
        loadDoctors();

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE_BG);
        panel.setBorder(BorderFactory.createTitledBorder("Bills History"));

        // Table
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Make all cells non-editable
            }
        };
        tableModel.setColumnIdentifiers(new String[]{"Bill ID", "Patient", "Doctor", "Doctor Fee", "Medicine", "Room", "Other", "Total", "Payment Method", "Payment Status", "Date"});
        billTable = new JTable(tableModel);
        billTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        billTable.setRowHeight(25);
        billTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        billTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Set column widths
        billTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // Bill ID
        billTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Patient
        billTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Doctor
        billTable.getColumnModel().getColumn(3).setPreferredWidth(90);  // Doctor Fee
        billTable.getColumnModel().getColumn(4).setPreferredWidth(90);  // Medicine
        billTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Room
        billTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Other
        billTable.getColumnModel().getColumn(7).setPreferredWidth(90);  // Total
        billTable.getColumnModel().getColumn(8).setPreferredWidth(120); // Payment Method
        billTable.getColumnModel().getColumn(9).setPreferredWidth(120); // Payment Status
        billTable.getColumnModel().getColumn(10).setPreferredWidth(100); // Date
        
        billTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JLabel label = (JLabel) c;
                
                // Center align Bill ID and Date columns
                if (column == 0 || column == 10) {
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                }
                // Center align Payment Method and Payment Status
                else if (column == 8 || column == 9) {
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                }
                // Right align numeric columns (Doctor Fee, Medicine, Room, Other, Total)
                else if (column >= 3 && column <= 7) {
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
                }
                // Left align text columns
                else {
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                }
                
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(billTable);
        scrollPane.setPreferredSize(new Dimension(1000, 250));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadPatients() {
        cbPatient.removeAllItems();
        LinkedList<Patient> patients = patientDAO.getAllPatients();
        for (Patient p : patients) {
            cbPatient.addItem(p.getPatientId() + " - " + p.getFirstName() + " " + p.getLastName());
        }
    }

    private void loadDoctors() {
        cbDoctor.removeAllItems();
        ArrayList<Doctor> doctors = doctorDAO.getAllDoctors();
        for (Doctor d : doctors) {
            cbDoctor.addItem(d.getDoctorId() + " - " + d.getName() + " (" + d.getSpecialization() + ")");
        }
    }

    private void onPatientSelected() {
        // Patient selection doesn't trigger any auto-fill
    }

    private void onDoctorSelected() {
        if (cbDoctor.getSelectedIndex() >= 0) {
            String selected = (String) cbDoctor.getSelectedItem();
            int doctorId = Integer.parseInt(selected.split(" - ")[0]);

            Doctor doctor = doctorDAO.getDoctorById(doctorId);
            if (doctor != null) {
                txtDoctorFee.setText(String.format("%.2f", doctor.getConsultationFee()));
                calculateTotal();
            }
        }
    }

    private void calculateTotal() {
        try {
            double doctor = Double.parseDouble(txtDoctorFee.getText());
            double medicine = Double.parseDouble(txtMedicineCharges.getText());
            double room = Double.parseDouble(txtRoomCharges.getText());
            double other = Double.parseDouble(txtOtherCharges.getText());
            double total = doctor + medicine + room + other;
            txtTotal.setText(String.format("%.2f", total));
        } catch (NumberFormatException e) {
            txtTotal.setText("0.0");
        }
    }

    private void generateBill() {
        if (cbPatient.getSelectedIndex() < 0 || cbDoctor.getSelectedIndex() < 0) {
            showMessage("✗ Please select both patient and doctor!", true);
            return;
        }

        try {
            String patientStr = (String) cbPatient.getSelectedItem();
            String doctorStr = (String) cbDoctor.getSelectedItem();
            int patientId = Integer.parseInt(patientStr.split(" - ")[0]);
            int doctorId = Integer.parseInt(doctorStr.split(" - ")[0]);

            double doctorFee = Double.parseDouble(txtDoctorFee.getText());
            double medicine = Double.parseDouble(txtMedicineCharges.getText());
            double room = Double.parseDouble(txtRoomCharges.getText());
            double other = Double.parseDouble(txtOtherCharges.getText());

            if (doctorFee < 0 || medicine < 0 || room < 0 || other < 0) {
                showMessage("✗ All charges must be non-negative!", true);
                return;
            }

            Bill bill = new Bill(patientId, doctorId, doctorFee, medicine, room, other);
            
            // Set payment method and status from dropdowns
            bill.setPaymentMethod((String) cbPaymentMethod.getSelectedItem());
            bill.setPaymentStatus((String) cbPaymentStatus.getSelectedItem());

            if (billDAO.generateBill(bill)) {
                showMessage("✓ Bill generated successfully! Bill ID: " + bill.getBillId(), false);
                clearForm();
                refreshBillsTable();
            } else {
                showMessage("✗ Error generating bill!", true);
            }
        } catch (NumberFormatException e) {
            showMessage("✗ Please enter valid numbers for charges!", true);
        }
    }

    private void printBill() {
        if (billTable.getSelectedRow() < 0) {
            showMessage("✗ Please select a bill to print!", true);
            return;
        }

        int billId = (int) tableModel.getValueAt(billTable.getSelectedRow(), 0);
        Bill bill = billDAO.getBillById(billId);

        if (bill != null) {
            showBillPrintPreview(bill);
        }
    }

    private void showBillPrintPreview(Bill bill) {
        JDialog printDialog = new JDialog((Frame) null, "Bill Preview - #" + bill.getBillId(), true);
        printDialog.setSize(600, 700);
        printDialog.setLocationRelativeTo(null);

        JTextArea billText = new JTextArea();
        billText.setEditable(false);
        billText.setFont(new Font("Monospaced", Font.PLAIN, 11));
        billText.setLineWrap(false);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║             BOMBAY HOSPITAL - BILL RECEIPT                 ║\n");
        sb.append("╚════════════════════════════════════════════════════════════╝\n\n");
        sb.append("BILL ID: ").append(bill.getBillId()).append("\n");
        sb.append("DATE: ").append(sdf.format(bill.getBillDate())).append("\n\n");
        sb.append("─────────────────────────────────────────────────────────────\n");
        sb.append("PATIENT DETAILS\n");
        sb.append("─────────────────────────────────────────────────────────────\n");
        sb.append("Name: ").append(bill.getPatientName()).append("\n");
        sb.append("Patient ID: ").append(bill.getPatientId()).append("\n");
        sb.append("Doctor: ").append(bill.getDoctorName()).append("\n\n");
        sb.append("─────────────────────────────────────────────────────────────\n");
        sb.append("CHARGES BREAKDOWN\n");
        sb.append("─────────────────────────────────────────────────────────────\n");
        sb.append(String.format("Doctor Fee              : ₹ %10.2f\n", bill.getDoctorFee()));
        sb.append(String.format("Medicine Charges       : ₹ %10.2f\n", bill.getMedicineCharges()));
        sb.append(String.format("Room Charges           : ₹ %10.2f\n", bill.getRoomCharges()));
        sb.append(String.format("Other Charges          : ₹ %10.2f\n", bill.getOtherCharges()));
        sb.append("─────────────────────────────────────────────────────────────\n");
        sb.append(String.format("TOTAL AMOUNT           : ₹ %10.2f\n", bill.getTotalAmount()));
        sb.append("─────────────────────────────────────────────────────────────\n\n");
        sb.append("TERMS & CONDITIONS:\n");
        sb.append("• This is a computer-generated bill\n");
        sb.append("• Payment should be made within 7 days\n");
        sb.append("• For any queries, contact hospital administration\n\n");
        sb.append("Thank you for choosing Bombay Hospital!\n");
        sb.append("═════════════════════════════════════════════════════════════\n");

        billText.setText(sb.toString());

        JScrollPane scrollPane = new JScrollPane(billText);
        printDialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton printBtn = new JButton("🖨️ Print");
        printBtn.addActionListener(e -> {
            try {
                billText.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(printDialog, "Error printing: " + ex.getMessage());
            }
        });
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> printDialog.dispose());

        buttonPanel.add(printBtn);
        buttonPanel.add(closeBtn);
        printDialog.add(buttonPanel, BorderLayout.SOUTH);

        printDialog.setVisible(true);
    }

    private void refreshBillsTable() {
        tableModel.setRowCount(0);
        ArrayList<Bill> bills = billDAO.getAllBills();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Bill bill : bills) {
            tableModel.addRow(new Object[]{
                bill.getBillId(),
                bill.getPatientName(),
                bill.getDoctorName(),
                String.format("%.2f", bill.getDoctorFee()),
                String.format("%.2f", bill.getMedicineCharges()),
                String.format("%.2f", bill.getRoomCharges()),
                String.format("%.2f", bill.getOtherCharges()),
                String.format("%.2f", bill.getTotalAmount()),
                bill.getPaymentMethod() != null ? bill.getPaymentMethod() : "Cash",
                bill.getPaymentStatus() != null ? bill.getPaymentStatus() : "Paid",
                sdf.format(bill.getBillDate())
            });
        }
    }

    private void clearForm() {
        cbPatient.setSelectedIndex(-1);
        cbDoctor.setSelectedIndex(-1);
        cbPaymentMethod.setSelectedIndex(0);
        cbPaymentStatus.setSelectedIndex(0);
        txtDoctorFee.setText("0.0");
        txtMedicineCharges.setText("0.0");
        txtRoomCharges.setText("0.0");
        txtOtherCharges.setText("0.0");
        txtTotal.setText("0.0");
        lblMessage.setText(" ");
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
     * Public method to refresh doctors and patients lists
     * Called when billing tab is activated to ensure latest data
     */
    public void refreshDropdowns() {
        loadPatients();
        loadDoctors();
        refreshBillsTable();
    }
}
